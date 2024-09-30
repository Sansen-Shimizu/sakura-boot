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

package org.sansenshimizu.sakuraboot.hypermedia.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermedia;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermediaOnPage;

/**
 * The aspect class to apply hypermedia links inside {@link Hypermedia} class
 * using {@link RepresentationModelAssemblerSupport} and
 * {@link PagedResourcesAssembler}.
 *
 * @param  <D> The {@link DataPresentation} type.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@Order(AspectUtil.HYPERMEDIA_ORDER)
@Aspect
@Component
@Slf4j
public final class HypermediaAspect<D extends DataPresentation<?>>
    implements AspectUtil {

    /**
     * The error message for a bad return type.
     */
    private static final String ERROR_MESSAGE_BAD_TYPE
        = "The method return type must be a ResponseEntity that contains a ";

    /**
     * Aspect method that call
     * {@link RepresentationModelAssemblerSupport#toModel(Object)}.
     * This aspect method is call
     * for method annotated with {@link ApplyHypermedia}.
     *
     * @param  joinPoint  The method that return the {@link ResponseEntity} and
     *                    need to add a hypermedia link to it.
     * @param  target     The target of type {@link Hypermedia}.
     * @param  annotation The annotation of type {@link ApplyHypermedia}.
     * @return            The result of the join point with the hypermedia link
     *                    added.
     * @throws Throwable  If an exception occurs in the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object applyHypermedia(
        final ProceedingJoinPoint joinPoint, final Hypermedia<D, ?> target,
        final ApplyHypermedia annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        Object result = joinPoint.proceed();

        if (result instanceof final ResponseEntity<?> responseEntity
            && target.getDataClass().isInstance(responseEntity.getBody())) {

            final D data = target.getDataClass().cast(responseEntity.getBody());
            final RepresentationModelAssemblerSupport<D, ?> modelAssembler
                = target.getModelAssembler();

            result = ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(modelAssembler.toModel(data));

            log.atInfo().log("new result : " + result);
        } else {

            log.atError().log(ERROR_MESSAGE_BAD_TYPE + "DataPresentation.");
        }

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }

    /**
     * Aspect method that call
     * {@link PagedResourcesAssembler#toModel(Page,
     * RepresentationModelAssembler)}.
     * This aspect
     * method is call for method annotated with {@link ApplyHypermediaOnPage}.
     *
     * @param  joinPoint  The method that return the {@link ResponseEntity} and
     *                    need to add a hypermedia link to it.
     * @param  target     The target of type {@link Hypermedia}.
     * @param  annotation The annotation of type {@link ApplyHypermediaOnPage}.
     * @return            The result of the join point with the hypermedia link
     *                    added.
     * @throws Throwable  If an exception occurs in the join point.
     */
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object applyHypermediaOnPage(
        final ProceedingJoinPoint joinPoint, final Hypermedia<D, ?> target,
        final ApplyHypermediaOnPage annotation)
        throws Throwable {

        methodCallLog(log, joinPoint, target, annotation);

        Object result = joinPoint.proceed();

        if (result instanceof final ResponseEntity<?> responseEntity
            && responseEntity.getBody() instanceof final Page<?> page
            && page.getContent()
                .stream()
                .allMatch(target.getDataClass()::isInstance)) {

            final RepresentationModelAssemblerSupport<D, ?> modelAssembler
                = target.getModelAssembler();
            final String pagedResourcesAssemblerValue = (String) AspectUtil
                .getAnnotationValue(annotation, "pagedResourcesAssembler");
            final PagedResourcesAssembler<D> pagedResourcesAssembler
                = target.getPagedResourcesAssembler()
                    .get(pagedResourcesAssemblerValue);

            result = ResponseEntity.status(responseEntity.getStatusCode())
                .headers(responseEntity.getHeaders())
                .body(pagedResourcesAssembler.toModel(
                    page.map(target.getDataClass()::cast), modelAssembler));

            log.atInfo().log("new result for page : " + result);
        } else {

            log.atError()
                .log(ERROR_MESSAGE_BAD_TYPE + "Page of DataPresentation.");
        }

        methodEndLog(log, joinPoint, target, annotation);
        return result;
    }
}
