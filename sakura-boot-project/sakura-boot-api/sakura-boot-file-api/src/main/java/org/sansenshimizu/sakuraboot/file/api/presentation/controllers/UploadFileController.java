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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.file.api.business.services.UploadFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerNotFoundApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerOkApiResponse;

/**
 * The controller interface for uploadFile operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link UploadFileController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends UploadFileController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link UploadFileController},
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
 *     implements UploadFileController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends UploadFileController.
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
 * @see        UploadFileController#getService()
 * @see        UploadFileController#uploadFile(Comparable, String,
 *             MultipartFile)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface UploadFileController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperController<E, I> {

    @Override
    UploadFileService<E, I> getService();

    /**
     * Upload a {@link File} by the specified ID and fileFieldName.
     *
     * @param  id            The unique identifier of the
     *                       {@link DataPresentation}.
     * @param  fileFieldName The name of the file field.
     * @param  file          The {@link MultipartFile} to upload
     * @return               A ResponseEntity containing a message and the HTTP
     *                       status code
     *                       200 (OK) on success. If no {@link DataPresentation}
     *                       or no
     *                       {@link File} is found with the provided ID and
     *                       fileFieldName, the
     *                       response will have the HTTP status code 404 (Not
     *                       Found).
     */
    @SwaggerOkApiResponse("Upload a file")
    @SwaggerBasicApiResponse
    @SwaggerNotFoundApiResponse
    @PutMapping(
        value = "{id}/{fileFieldName}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Logging
    default ResponseEntity<String> uploadFile(
        @PathVariable("id") final I id,
        @PathVariable("fileFieldName") final String fileFieldName,
        @RequestParam("file") final MultipartFile file) {

        getService().uploadFile(id, fileFieldName, file);
        return ResponseEntity.status(HttpStatus.OK)
            .body(file.getOriginalFilename() + " uploaded successfully.");
    }
}
