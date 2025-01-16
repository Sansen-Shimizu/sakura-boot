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

package org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.DeleteAllByCriteriaService;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNoContentApiResponse;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.SuperCriteriaController;

/**
 * The controller interface for deleteAll operation with filtering.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link DeleteAllByCriteriaController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends DeleteAllByCriteriaController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements
 * {@link DeleteAllByCriteriaController},
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
 *     implements DeleteAllByCriteriaController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends DeleteAllController.
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
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        DeleteAllByCriteriaController#getService()
 * @see        DeleteAllByCriteriaController#deleteAllByCriteria(FilterPresentation)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface DeleteAllByCriteriaController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperCriteriaController<E, I, F> {

    @Override
    DeleteAllByCriteriaService<E, I, F> getService();

    /**
     * Delete all {@link DataPresentation}.
     *
     * @param  filter The {@link FilterPresentation} object containing criteria
     *                for filtering {@link DataPresentation} (optional).
     * @return        A ResponseEntity with the HTTP status code 204 (No
     *                Content).
     */
    @SwaggerNoContentApiResponse("Delete all entities with filters")
    @SwaggerBasicApiResponse
    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Logging
    default ResponseEntity<Void> deleteAllByCriteria(
        @Parameter(in = ParameterIn.QUERY, name = "filter") @ParameterObject
        @Nullable final F filter) {

        getService().deleteAllByCriteria(filter);
        return ResponseEntity.noContent().build();
    }
}
