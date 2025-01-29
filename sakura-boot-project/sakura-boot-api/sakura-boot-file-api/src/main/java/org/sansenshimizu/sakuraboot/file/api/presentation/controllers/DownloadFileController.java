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

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.file.api.business.services.DownloadFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNotFoundApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerOkApiResponse;

/**
 * The controller interface for downloadFile operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link DownloadFileController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends DownloadFileController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link DownloadFileController},
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
 *     implements DownloadFileController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends DownloadFileController.
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
 * @see        DownloadFileController#getService()
 * @see        DownloadFileController#downloadFile(Comparable, String)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface DownloadFileController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperController<E, I> {

    @Override
    DownloadFileService<E, I> getService();

    /**
     * Retrieve a {@link File} by the specified ID and fileFieldName.
     *
     * @param  id            The unique identifier of the
     *                       {@link DataPresentation}.
     * @param  fileFieldName The name of the file field.
     * @return               A ResponseEntity containing the retrieved
     *                       {@link File} and the HTTP status code
     *                       200 (OK) on success. If no
     *                       {@link DataPresentation} or no {@link File} is
     *                       found with the provided ID and fileFieldName, the
     *                       response will have the HTTP status code 404 (Not
     *                       Found).
     *                       If the field is not a file, the response will have
     *                       the HTTP status code 400 (Bad Request).
     */
    @SwaggerOkApiResponse("Get a file")
    @SwaggerBasicApiResponse
    @SwaggerNotFoundApiResponse
    @GetMapping(
        value = "{id}/{fileFieldName}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Logging
    default ResponseEntity<Resource> downloadFile(
        @PathVariable("id") final I id,
        @PathVariable("fileFieldName") final String fileFieldName) {

        final Pair<Resource, String> file
            = getService().downloadFile(id, fileFieldName);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getValue() + "\"")
            .body(file.getKey());
    }
}
