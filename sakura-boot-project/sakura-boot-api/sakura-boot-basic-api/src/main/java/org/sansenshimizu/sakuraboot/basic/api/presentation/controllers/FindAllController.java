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

package org.sansenshimizu.sakuraboot.basic.api.presentation.controllers;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermediaOnPage;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerOkApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerUpdateOperation;

/**
 * The controller interface for findAll operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link FindAllController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends FindAllController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link FindAllController},
 * follow these steps:
 * </p>
 * <p>
 * Create a new controller class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;RestController
 * &#064;RequestMapping("/yourPath")
 * public class YourController
 *     implements FindAllController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends FindAllController.
 *     private final YourService service;
 *
 *     public YourController(final YourService service) {
 *
 *         this.service = service;
 *     }
 *
 *     public YourService getService() {
 *
 *         return this.service;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type use in the service layer.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        FindAllController#getService()
 * @see        FindAllController#findAll(Pageable)
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface FindAllController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperController<E, I> {

    @Override
    FindAllService<E, I> getService();

    /**
     * Retrieve all {@link DataPresentation} with a paginated list based on the
     * provided pageable information.
     *
     * @param  pageable The pagination information for retrieving a paginated
     *                  list of {@link DataPresentation} (optional).
     * @return          A ResponseEntity containing a page of
     *                  {@link DataPresentation} and the HTTP status code 200
     *                  (OK) on success.
     */
    @SwaggerOkApiResponse("Get all entities")
    @SwaggerBasicApiResponse
    @SwaggerUpdateOperation
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApplyHypermediaOnPage
    @Logging
    @SuppressWarnings("java:S1452")
    default ResponseEntity<Page<?>> findAll(
        @Parameter(in = ParameterIn.QUERY, name = "page")
        @ParameterObject final Pageable pageable) {

        return ResponseEntity.ok(getService().findAll(pageable));
    }
}
