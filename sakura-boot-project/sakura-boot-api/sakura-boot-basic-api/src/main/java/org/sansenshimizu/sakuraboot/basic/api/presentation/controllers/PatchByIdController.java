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
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.basic.api.business.services.PatchByIdService;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermedia;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNotFoundApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerOkApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerUpdateOperation;

/**
 * The base controller interface for patchById operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link PatchByIdController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends
 *     PatchByIdController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link PatchByIdController},
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
 *     implements
 *     PatchByIdController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *
 *     // Or implements your interface that extends PatchByIdController.
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
 * @param  <D> The {@link DataPresentation} type use to map the given JSON to an
 *             entity or DTO.
 * @author     Malcolm Rozé
 * @see        PatchByIdController#getService()
 * @see        PatchByIdController#patchById(DataPresentation, Comparable)
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface PatchByIdController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends SuperController<E, I> {

    @Override
    PatchByIdService<E, I> getService();

    /**
     * Partially update an existing {@link DataPresentation} by applying the
     * changes from the provided {@link DataPresentation} and associating it
     * with the specified ID.
     *
     * @param  data The {@link DataPresentation} from where to take the changes
     *              to be applied.
     * @param  id   The unique identifier of the {@link DataPresentation} to be
     *              updated.
     * @return      A ResponseEntity containing the partially updated
     *              {@link DataPresentation} and the HTTP status code 200
     *              (OK) on success. If no {@link DataPresentation} is found
     *              with the provided ID, the response will have the HTTP status
     *              code 404 (Not Found). If the provided
     *              {@link DataPresentation} is invalid or contains incomplete
     *              information, or if it has a different ID than the one
     *              provided in the URL, the response will have HTTP status
     *              code 400 (Bad Request).
     */
    @SwaggerOkApiResponse("Patch entity")
    @SwaggerBasicApiResponse
    @SwaggerNotFoundApiResponse
    @SwaggerUpdateOperation
    @PatchMapping(value = {
        "", "/{id}"
    }, consumes = {
        MediaType.APPLICATION_JSON_VALUE, "application/merge-patch+json"
    }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApplyHypermedia
    @Logging
    default ResponseEntity<DataPresentation<I>> patchById(
        @RequestBody final D data,
        @PathVariable(value = "id", required = false) @Nullable final I id) {

        return ResponseEntity.ok(getService().patchById(data, id));
    }
}
