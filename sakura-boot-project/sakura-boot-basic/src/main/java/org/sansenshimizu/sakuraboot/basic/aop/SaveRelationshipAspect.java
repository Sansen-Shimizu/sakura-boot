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
import java.lang.reflect.Field;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.basic.api.business.services.SaveService;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.SaveWithRelationship;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

/**
 * The aspect class for relationship in service class.
 * Handles the relationships in save calls.
 *
 * @param  <D> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@Order(AspectUtil.SAVE_RELATIONSHIP_ORDER)
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public final class SaveRelationshipAspect<D extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> implements AspectUtil {

    /**
     * The type annotation pointcut.
     */
    private static final String TYPE_ANNOTATION_POINTCUT
        = " && @target(org.sansenshimizu.sakuraboot.basic.api.relationship"
            + ".annotations.Relationshipable)";

    /**
     * The {@link GlobalSpecification}.
     */
    private final GlobalSpecification globalSpecification;

    /**
     * Aspect method that handles the relationships in a save call.
     * Check if the relationship already has an ID and throw an exception if it
     * does.
     * This aspect method is call for method annotated with
     * {@link SaveWithRelationship}.
     *
     * @param  joinPoint  The method that will be handled.
     * @param  arg        The entity to save.
     * @param  target     The target of type {@link SaveService}.
     * @param  annotation The annotation of type
     *                    {@link SaveWithRelationship}.
     * @return            The result of the join point.
     * @throws Throwable  If an error occurs during the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT
        + FIRST_ARG_POINTCUT
        + TARGET_POINTCUT
        + ANNOTATION_POINTCUT
        + TYPE_ANNOTATION_POINTCUT)
    public Object saveWithRelationship(
        final ProceedingJoinPoint joinPoint, final D arg,
        final SaveService<D, I> target, final SaveWithRelationship annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        RelationshipUtils.doWithRelationFields(arg,
            (final Field field, final Object object) -> {

                if (object instanceof final DataPresentation<?> data
                    && data.getId() != null) {

                    throw new BadRequestException(
                        """
                        Can't save an entity when the relationship already
                        has an ID
                        """);
                }
            }, globalSpecification);

        final Object result = joinPoint.proceed();

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }
}
