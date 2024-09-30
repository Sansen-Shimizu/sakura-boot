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

package org.sansenshimizu.sakuraboot.cache.aop;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.cache.api.Cacheable;
import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;
import org.sansenshimizu.sakuraboot.cache.api.annotations.Caching;
import org.sansenshimizu.sakuraboot.cache.api.annotations.PutCache;
import org.sansenshimizu.sakuraboot.cache.api.annotations.RemoveCache;

/**
 * The aspect class for caching method inside {@link Cacheable} class using
 * {@link CachingUtil}.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@Order(AspectUtil.CACHE_ORDER)
@Aspect
@Component
@Slf4j
public final class CachingAspect implements AspectUtil {

    /**
     * The String that represents a null value.
     */
    private static final String NULL_STRING = "null";

    /**
     * The message for log cache in.
     */
    private static final String CACHE_IN_LOG = " in : ";

    private static String[] concatAllToCacheNames(final String[] cacheNames) {

        final String[] concatCacheNames = cacheNames.clone();

        for (int i = 0; i < concatCacheNames.length; i++) {

            concatCacheNames[i] = concatCacheNames[i] + "All";
        }
        return concatCacheNames;
    }

    /**
     * Aspect method that call
     * {@link CachingUtil#caching(String[], CachingUtil.Supplier, Object)} .
     * This aspect method is call for method annotated with {@link Caching}.
     *
     * @param  joinPoint  The method that will be cached.
     * @param  target     The target of type {@link Cacheable}.
     * @param  annotation The annotation of type {@link Caching}.
     * @return            The result of the join point.
     * @throws Throwable  If an exception occurs in the join point.
     */
    @Nullable
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object caching(
        final ProceedingJoinPoint joinPoint, final Cacheable target,
        final Caching annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        final String[] cacheNames;

        if (annotation.specificsCacheNames().length == 0) {

            cacheNames = target.getCacheNames().clone();
        } else {

            cacheNames = annotation.specificsCacheNames().clone();
        }

        if (!annotation.concatToCacheName().isBlank()) {

            for (int i = 0; i < cacheNames.length; i++) {

                cacheNames[i] = cacheNames[i] + annotation.concatToCacheName();
            }
        }

        Object key;
        final String keyAnnotation
            = (String) AspectUtil.getAnnotationValue(annotation, "key");

        if (!Objects.requireNonNull(keyAnnotation).isBlank()) {

            final MethodSignature signature
                = (MethodSignature) joinPoint.getSignature();
            final String[] parametersNames
                = Objects.requireNonNull(signature.getParameterNames());
            final Object[] parameters
                = Objects.requireNonNull(joinPoint.getArgs());
            key = parseSpelExpression(parametersNames, parameters,
                keyAnnotation);
        } else {

            key = Arrays.stream(joinPoint.getArgs()).map((final Object arg) -> {

                if (arg == null) {

                    return NULL_STRING;
                } else {

                    return arg.toString();
                }
            }).collect(Collectors.joining());
        }

        if (key == null) {

            key = "";
        }

        final Object result = target.getCachingUtil()
            .caching(cacheNames, joinPoint::proceed, key);
        log.atInfo()
            .log("get from cache or put : " + result + CACHE_IN_LOG + key);

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }

    /**
     * Aspect method that call
     * {@link CachingUtil#putCache(String[], Object, Object)}. This aspect
     * method is call for method annotated with {@link PutCache}.
     *
     * @param  joinPoint  The method that will use to put the cache.
     * @param  target     The target of type {@link Cacheable}.
     * @param  annotation The annotation of type {@link PutCache}.
     * @return            The same return value of the join point call.
     * @throws Throwable  If an exception occurs in the join point.
     */
    @Nullable
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object putCache(
        final ProceedingJoinPoint joinPoint, final Cacheable target,
        final PutCache annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        Object result = joinPoint.proceed();

        final String[] cacheNames;

        if (annotation.specificsCacheNames().length == 0) {

            cacheNames = target.getCacheNames();
        } else {

            cacheNames = annotation.specificsCacheNames();
        }

        Object key;
        Object id = null;

        if (result instanceof final DataPresentation<?> data
            && data.getId() != null) {

            id = data.getId();
        }
        final String keyAnnotation
            = (String) AspectUtil.getAnnotationValue(annotation, "key");

        if (!Objects.requireNonNull(keyAnnotation).isBlank()) {

            final MethodSignature signature
                = (MethodSignature) joinPoint.getSignature();
            final String[] parametersNames
                = Objects.requireNonNull(signature.getParameterNames());
            final Object[] parameters
                = Objects.requireNonNull(joinPoint.getArgs());
            key = parseSpelExpression(parametersNames, parameters,
                keyAnnotation);
        } else if (id != null) {

            key = id;
        } else {

            key = Arrays.stream(joinPoint.getArgs()).map((final Object arg) -> {

                if (arg == null) {

                    return NULL_STRING;
                } else {

                    return arg.toString();
                }
            }).collect(Collectors.joining());
        }

        if (key == null && id != null) {

            key = id;
        }

        if (key == null) {

            key = "";
        }

        if (annotation.refreshEntityCache()) {

            target.getCachingUtil()
                .removeAllCache(concatAllToCacheNames(cacheNames));
            log.atInfo().log("remove \"all\" cache before");
        }

        result = target.getCachingUtil().putCache(cacheNames, result, key);
        log.atInfo().log("put cache : " + result + CACHE_IN_LOG + key);

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }

    /**
     * Aspect method that call
     * {@link CachingUtil#removeCache(String[], Object)}. This aspect method is
     * call for method annotated with {@link RemoveCache}.
     *
     * @param  joinPoint  The method that will use to remove the cache.
     * @param  target     The target of type {@link Cacheable}.
     * @param  annotation The annotation of type {@link RemoveCache}.
     * @return            The same return value of the join point call.
     * @throws Throwable  If an exception occurs in the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object removeCache(
        final ProceedingJoinPoint joinPoint, final Cacheable target,
        final RemoveCache annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        final Object result = joinPoint.proceed();

        final String[] cacheNames;

        if (annotation.specificsCacheNames().length == 0) {

            cacheNames = target.getCacheNames();
        } else {

            cacheNames = annotation.specificsCacheNames();
        }

        Object key;
        final String keyAnnotation
            = (String) AspectUtil.getAnnotationValue(annotation, "key");

        if (!Objects.requireNonNull(keyAnnotation).isBlank()) {

            final MethodSignature signature
                = (MethodSignature) joinPoint.getSignature();
            final String[] parametersNames
                = Objects.requireNonNull(signature.getParameterNames());
            final Object[] parameters
                = Objects.requireNonNull(joinPoint.getArgs());
            key = parseSpelExpression(parametersNames, parameters,
                keyAnnotation);

            if (key == null) {

                key = "";
            }
            target.getCachingUtil().removeCache(cacheNames, key);
            log.atInfo().log("remove cache in : " + key);
        } else {

            target.getCachingUtil().removeAllCache(cacheNames);
            log.atInfo().log("remove cache");
        }

        if (annotation.refreshEntityCache()) {

            target.getCachingUtil()
                .removeAllCache(concatAllToCacheNames(cacheNames));
            log.atInfo().log("remove \"all\" cache");
        }

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }
}
