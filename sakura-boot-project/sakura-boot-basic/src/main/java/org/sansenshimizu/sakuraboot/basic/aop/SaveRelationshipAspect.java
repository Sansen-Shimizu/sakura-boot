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
import java.util.Set;

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
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;

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

        checkRelationship(arg);
        checkSecondRelationship(arg);

        final Object result = joinPoint.proceed();

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }

    private static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> void
        checkRelationship(final D arg) {

        final String cannotSaveMessage
            = "Can't save an entity when the relationship already has an ID";

        if (arg instanceof final DataPresentation1RelationshipAnyToOne<?,
            ?> dataRelationship) {

            final DataPresentation<?> relationship
                = dataRelationship.getRelationship();

            if (relationship != null && relationship.getId() != null) {

                throw new BadRequestException(cannotSaveMessage);
            }
        }

        if (arg instanceof final DataPresentation1RelationshipAnyToMany<?,
            ?> dataRelationship) {

            final Set<? extends DataPresentation<?>> relationship
                = dataRelationship.getRelationships();

            if (relationship != null
                && relationship.stream().anyMatch(e -> e.getId() != null)) {

                throw new BadRequestException(cannotSaveMessage);
            }
        }
    }

    private static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> void
        checkSecondRelationship(final D arg) {

        final String cannotSaveMessageSecond
            = "Can't save an entity when the second relationship already has "
                + "an ID";

        if (arg instanceof final DataPresentation2RelationshipAnyToOne<?, ?,
            ?> dataRelationship) {

            final DataPresentation<?> relationship
                = dataRelationship.getSecondRelationship();

            if (relationship != null && relationship.getId() != null) {

                throw new BadRequestException(cannotSaveMessageSecond);
            }
        }

        if (arg instanceof final DataPresentation2RelationshipAnyToMany<?, ?,
            ?> dataRelationship) {

            final Set<? extends DataPresentation<?>> relationship
                = dataRelationship.getSecondRelationships();

            if (relationship != null
                && relationship.stream().anyMatch(e -> e.getId() != null)) {

                throw new BadRequestException(cannotSaveMessageSecond);
            }
        }
    }
}
