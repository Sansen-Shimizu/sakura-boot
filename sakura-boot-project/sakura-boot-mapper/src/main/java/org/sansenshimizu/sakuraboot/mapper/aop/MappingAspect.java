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

package org.sansenshimizu.sakuraboot.mapper.aop;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;

/**
 * The aspect class for mapping method inside {@link Mappable} class using
 * {@link BasicMapper}.
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <D> The DTO type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@Order(AspectUtil.MAPPING_ORDER)
@Aspect
@Component
@Slf4j
public final class MappingAspect<E extends DataPresentation<?>,
    D extends DataPresentation<?>> implements AspectUtil {

    @Nullable
    private Object toEntity(final Object object, final Mappable<E, D> target) {

        final Class<D> dtoClass = target.getDtoClass();
        final Object result;

        if (dtoClass.isInstance(object)) {

            result = target.getMapper().toEntity(dtoClass.cast(object));
        } else if (object instanceof final Page<?> page
            && page.getContent().stream().allMatch(dtoClass::isInstance)) {

            result = page.map(dtoClass::cast).map(target.getMapper()::toEntity);
        } else if (object instanceof final Collection<?> collection
            && collection.stream().allMatch(dtoClass::isInstance)) {

            result = collection.stream()
                .map(dtoClass::cast)
                .map(target.getMapper()::toEntity)
                .toList();
        } else {

            result = object;
        }
        return result;
    }

    @Nullable
    private Object toDto(final Object object, final Mappable<E, D> target) {

        final Class<E> entityClass = target.getEntityClassToMap();
        final Object result;

        if (entityClass.isInstance(object)) {

            result = target.getMapper().toDto(entityClass.cast(object));
        } else if (object instanceof final Page<?> page
            && page.getContent().stream().allMatch(entityClass::isInstance)) {

            result = page.map(entityClass::cast).map(target.getMapper()::toDto);
        } else if (object instanceof final Collection<?> collection
            && collection.stream().allMatch(entityClass::isInstance)) {

            result = collection.stream()
                .map(entityClass::cast)
                .map(target.getMapper()::toDto)
                .toList();
        } else {

            result = object;
        }
        return result;
    }

    /**
     * Aspect method that maps the first argument if present and the return
     * value if any using {@link BasicMapper}.
     * This aspect method is call for method annotated with {@link Mapping}.
     *
     * @param  joinPoint  The method that will be logged.
     * @param  target     The target of type {@link Mappable}.
     * @param  annotation The annotation of type {@link Mapping}.
     * @return            The result of the join point.
     * @throws Throwable  If an exception occurs in the join point.
     */
    @Nullable
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object mapping(
        final ProceedingJoinPoint joinPoint, final Mappable<E, D> target,
        final Mapping annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);
        final Object[] args = joinPoint.getArgs().clone();

        if (annotation.mapFirstArgument()) {

            args[0] = toEntity(args[0], target);
            log.atInfo().log("new argument : " + args[0]);
        }

        Object result = joinPoint.proceed(args);

        if (annotation.mapResult()) {

            result = toDto(result, target);
            log.atInfo().log("new return : " + result);
        }
        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }
}
