/*
 * Copyright (C) 2023-2024 Malcolm Rozé.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sansenshimizu.sakuraboot.specification.aop;

import java.io.Serializable;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.basic.aop.RelationshipAspect;
import org.sansenshimizu.sakuraboot.log.aop.LogAspect;
import org.sansenshimizu.sakuraboot.log.api.annotations.AfterLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.BeforeLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.specification.api.business.services.FindAllByCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.relationship.FetchRelationshipSpecificationRepository;
import org.sansenshimizu.sakuraboot.specification.api.relationship.annotations.FindAllByCriteriaWithRelationship;

/**
 * The aspect class for relationship in service class with filter.
 * Handles the relationships in find by criteria calls.
 *
 * @param  <D> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@Order(AspectUtil.RELATIONSHIP_ORDER)
@Aspect
@Component
@Slf4j
public final class RelationshipSpecificationAspect<
    D extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> implements AspectUtil {

    /**
     * Aspect method that handles the relationships in a findAllByCriteria call.
     * Perform a join fetch if needed.
     * This aspect method is call for method annotated with
     * {@link FindAllByCriteriaWithRelationship}.
     *
     * @param  joinPoint  The method that will be handled.
     * @param  firstArg   The argument of type {@link FilterPresentation}
     * @param  secondArg  The argument of type {@link Pageable}
     * @param  target     The target of type {@link FindAllByCriteriaService}.
     * @param  annotation The annotation of type
     *                    {@link FindAllByCriteriaWithRelationship}.
     * @return            The result of the join point.
     * @throws Throwable  If an error occurs during the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT
        + FIRST_AND_SECOND_ARGS_POINTCUT
        + TARGET_POINTCUT
        + ANNOTATION_POINTCUT
        + RelationshipAspect.TYPE_ANNOTATION_POINTCUT)
    public Object findAllByCriteriaWithRelationship(
        final ProceedingJoinPoint joinPoint, final F firstArg,
        final Pageable secondArg,
        final FindAllByCriteriaService<D, I, F> target,
        final FindAllByCriteriaWithRelationship annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        final Logging loggingAnnotation
            = RelationshipAspect.getLoggingAnnotation(joinPoint);
        final Object result;

        /* @formatter:off */
        if (target.getRepository()
            instanceof final FetchRelationshipSpecificationRepository<?, ?>
            repository) {

            /* @formatter:on */
            if (RelationshipAspect.isLoggable(target, loggingAnnotation)) {

                final BeforeLogging beforeLogging
                    = RelationshipAspect.getBeforeLogging(loggingAnnotation);
                LogAspect.beforeLogging(joinPoint, beforeLogging);
            }

            @SuppressWarnings("unchecked")
            final FetchRelationshipSpecificationRepository<D, I> fetchRepository
                = (FetchRelationshipSpecificationRepository<D, I>) repository;
            final Specification<D> specification
                = target.getSpecification(firstArg);

            final Page<I> ids = fetchRepository.findAllIds(secondArg,
                specification, target.getEntityClass());
            // TODO : Use findBy with projection but currently all the field are
            // selected.
            // target.getRepository().findBy(specification, q ->
            // q.project("id").page(secondArg)).map(DataPresentation::getId);
            // When a PR is found change to that and remove the findAllIds
            // method in the fetchRepository if not needed anymore.

            final List<D> listResult = fetchRepository.findAllEagerRelationship(
                ids.getContent(), target.getEntityClass());

            result
                = new PageImpl<>(listResult, secondArg, ids.getTotalElements());
            log.atInfo()
                .log("find all by criteria using eager fetching for "
                    + "relationship.");

            if (RelationshipAspect.isLoggable(target, loggingAnnotation)) {

                final AfterLogging afterLogging
                    = RelationshipAspect.getAfterLogging(loggingAnnotation);
                LogAspect.afterLogging(joinPoint, afterLogging, result);
            }
        } else {

            result = joinPoint.proceed();
        }

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }
}
