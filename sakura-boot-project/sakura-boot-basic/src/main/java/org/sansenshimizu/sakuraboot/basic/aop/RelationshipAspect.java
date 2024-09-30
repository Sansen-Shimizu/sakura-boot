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

package org.sansenshimizu.sakuraboot.basic.aop;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.FindAllWithRelationship;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.FindByIdWithRelationship;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.Relationshipable;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.log.aop.LogAspect;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.log.api.annotations.AfterLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.BeforeLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;

/**
 * The aspect class for relationship in service class.
 * Handles the relationships in find calls.
 *
 * @param  <D> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@Order(AspectUtil.RELATIONSHIP_ORDER)
@Aspect
@Component
@Slf4j
public final class RelationshipAspect<D extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> implements AspectUtil {

    /**
     * The pointcut for class annotated with {@link Relationshipable}.
     */
    public static final String TYPE_ANNOTATION_POINTCUT
        = " && @target(org.sansenshimizu.sakuraboot.basic.api.relationship"
            + ".annotations.Relationshipable)";

    /**
     * Aspect method that handles the relationships in a findAll call.
     * Perform a join fetch if needed.
     * This aspect method is call for method annotated with
     * {@link FindAllWithRelationship}.
     *
     * @param  joinPoint  The method that will be handled.
     * @param  arg        The argument of type {@link Pageable}
     * @param  target     The target of type {@link FindAllService}.
     * @param  annotation The annotation of type
     *                    {@link FindAllWithRelationship}.
     * @return            The result of the join point.
     * @throws Throwable  If an error occurs during the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT
        + FIRST_ARG_POINTCUT
        + TARGET_POINTCUT
        + ANNOTATION_POINTCUT
        + TYPE_ANNOTATION_POINTCUT)
    public Object findAllWithRelationship(
        final ProceedingJoinPoint joinPoint, final Pageable arg,
        final FindAllService<D, I> target,
        final FindAllWithRelationship annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        final Logging loggingAnnotation = getLoggingAnnotation(joinPoint);
        final Object result;

        final BasicRepository<D, I> repository = target.getRepository();

        if (repository instanceof final FetchRelationshipRepository<?,
            ?> fetchRepository) {

            if (isLoggable(target, loggingAnnotation)) {

                final BeforeLogging beforeLogging
                    = getBeforeLogging(loggingAnnotation);
                LogAspect.beforeLogging(joinPoint, beforeLogging);
            }

            final Page<I> ids = repository.findAllIds(arg);
            @SuppressWarnings("unchecked")
            final List<D> listResult
                = ((FetchRelationshipRepository<D, I>) fetchRepository)
                    .findAllEagerRelationship(ids.getContent(),
                        target.getEntityClass());

            result = new PageImpl<>(listResult, arg, ids.getTotalElements());
            log.atInfo().log("find all using eager fetching for relationship.");

            if (isLoggable(target, loggingAnnotation)) {

                final AfterLogging afterLogging
                    = getAfterLogging(loggingAnnotation);
                LogAspect.afterLogging(joinPoint, afterLogging, result);
            }
        } else {

            result = joinPoint.proceed();
        }

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }

    /**
     * Aspect method that handles the relationships in a findById call.
     * Perform a join fetch if needed.
     * This aspect method is call for method annotated with
     * {@link FindByIdWithRelationship}.
     *
     * @param  joinPoint  The method that will be handled.
     * @param  arg        The id.
     * @param  target     The target of type {@link FindByIdService}.
     * @param  annotation The annotation of type
     *                    {@link FindByIdWithRelationship}.
     * @return            The result of the join point.
     * @throws Throwable  If an error occurs during the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT
        + FIRST_ARG_POINTCUT
        + TARGET_POINTCUT
        + ANNOTATION_POINTCUT
        + TYPE_ANNOTATION_POINTCUT)
    public Object findByIdWithRelationship(
        final ProceedingJoinPoint joinPoint, final I arg,
        final FindByIdService<D, I> target,
        final FindByIdWithRelationship annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        final Logging loggingAnnotation = getLoggingAnnotation(joinPoint);

        final Object result;

        if (target.getRepository() instanceof final FetchRelationshipRepository<
            ?, ?> repository) {

            if (isLoggable(target, loggingAnnotation)) {

                final BeforeLogging beforeLogging
                    = getBeforeLogging(loggingAnnotation);
                LogAspect.beforeLogging(joinPoint, beforeLogging);
            }

            @SuppressWarnings("unchecked")
            final FetchRelationshipRepository<D, I> fetchRepository
                = (FetchRelationshipRepository<D, I>) repository;
            result = fetchRepository
                .findByIdEagerRelationship(arg, target.getEntityClass())
                .orElseThrow(
                    () -> new NotFoundException(target.getEntityClass(), arg));

            log.atInfo()
                .log("find by ID using eager fetching for relationship.");

            if (isLoggable(target, loggingAnnotation)) {

                final AfterLogging afterLogging
                    = getAfterLogging(loggingAnnotation);
                LogAspect.afterLogging(joinPoint, afterLogging, result);
            }
        } else {

            result = joinPoint.proceed();
        }

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }

    /**
     * Check if the target is loggable.
     *
     * @param  target            The target.
     * @param  loggingAnnotation The annotation use for logging.
     * @param  <D>               The {@link DataPresentation} type.
     * @param  <I>               The ID of type Comparable and Serializable.
     * @return                   {@code true} if the target is loggable,
     *                           {@code false} otherwise.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> boolean isLoggable(
            final SuperService<D, I> target,
            @Nullable final Logging loggingAnnotation) {

        return loggingAnnotation != null
            && (target instanceof Loggable
                || loggingAnnotation.activateLogging());
    }

    /**
     * Get the logging annotation from the join point.
     *
     * @param  joinPoint The join point.
     * @return           The logging annotation of type {@link Logging}.
     */
    @Nullable
    public static Logging getLoggingAnnotation(final JoinPoint joinPoint) {

        final MethodSignature signature
            = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        return method.getAnnotation(Logging.class);
    }

    /**
     * Get the before logging annotation from the logging annotation.
     *
     * @param  loggingAnnotation The logging annotation.
     * @return                   The before logging annotation of type
     *                           {@link BeforeLogging}.
     */
    public static
        BeforeLogging getBeforeLogging(final Logging loggingAnnotation) {

        return new BeforeLogging() {

            @Override
            public Class<? extends Annotation> annotationType() {

                return BeforeLogging.class;
            }

            @Override
            public String message() {

                return loggingAnnotation.message();
            }

            @Override
            public String value() {

                return loggingAnnotation.message();
            }
        };
    }

    /**
     * Get the after logging annotation from the logging annotation.
     *
     * @param  loggingAnnotation The logging annotation.
     * @return                   The after logging annotation of type
     *                           {@link AfterLogging}.
     */
    public static
        AfterLogging getAfterLogging(final Logging loggingAnnotation) {

        return new AfterLogging() {

            @Override
            public Class<? extends Annotation> annotationType() {

                return AfterLogging.class;
            }

            @Override
            public String message() {

                return loggingAnnotation.message();
            }

            @Override
            public String value() {

                return loggingAnnotation.message();
            }
        };
    }
}
