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

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermedia;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNotFoundApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerOkApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerUpdateOperation;

/**
 * The controller interface for findById operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link FindByIdController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends FindByIdController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link FindByIdController},
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
 *     implements FindByIdController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends FindByIdController.
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
 * @see        FindByIdController#getService()
 * @see        FindByIdController#findById(Comparable)
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface FindByIdController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperController<E, I> {

    @Override
    FindByIdService<E, I> getService();

    /**
     * Retrieve a {@link DataPresentation} by it's unique identifier (ID).
     *
     * @param  id The unique identifier of the {@link DataPresentation} to be
     *            retrieved.
     * @return    A ResponseEntity containing the retrieved
     *            {@link DataPresentation} and the HTTP status code 200 (OK) on
     *            success. If no {@link DataPresentation} is found with the
     *            provided ID, the response will have the HTTP status code 404
     *            (Not Found).
     */
    @SwaggerOkApiResponse("Get entity")
    @SwaggerBasicApiResponse
    @SwaggerNotFoundApiResponse
    @SwaggerUpdateOperation
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApplyHypermedia
    @Logging
    default ResponseEntity<DataPresentation<I>> findById(
        @PathVariable("id") final I id) {

        return ResponseEntity.ok(getService().findById(id));
    }
}
