/*
 * Copyright (C) 2023-2025 Malcolm Rozé.
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

package org.sansenshimizu.sakuraboot.file.api.presentation.controllers;

import java.io.Serializable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.file.api.business.services.DeleteFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNoContentApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNotFoundApiResponse;

/**
 * The controller interface for deleteFile operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link DeleteFileController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends DeleteFileController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link DeleteFileController},
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
 *     implements DeleteFileController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends DeleteFileController.
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
 * @see        DeleteFileController#getService()
 * @see        DeleteFileController#deleteFile(Comparable, String)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface DeleteFileController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperController<E, I> {

    @Override
    DeleteFileService<E, I> getService();

    /**
     * Delete a {@link File} by the specified ID and fileFieldName.
     *
     * @param  id            The unique identifier of the
     *                       {@link DataPresentation}.
     * @param  fileFieldName The name of the file field.
     * @return               A ResponseEntity with the HTTP status code 204 (No
     *                       Content) on successful deletion. If no
     *                       {@link DataPresentation} or no {@link File} is
     *                       found with the provided ID and fileFieldName, the
     *                       response will have the HTTP status code 404 (Not
     *                       Found).
     */
    @SwaggerNoContentApiResponse("Delete a file")
    @SwaggerBasicApiResponse
    @SwaggerNotFoundApiResponse
    @DeleteMapping("{id}/{fileFieldName}")
    @Logging
    default ResponseEntity<String> deleteFile(
        @PathVariable("id") final I id,
        @PathVariable("fileFieldName") final String fileFieldName) {

        getService().deleteFile(id, fileFieldName);
        return ResponseEntity.noContent().build();
    }
}
