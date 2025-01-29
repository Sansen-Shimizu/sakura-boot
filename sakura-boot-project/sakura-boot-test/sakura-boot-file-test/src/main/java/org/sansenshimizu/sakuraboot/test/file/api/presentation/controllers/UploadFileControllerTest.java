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

package org.sansenshimizu.sakuraboot.test.file.api.presentation.controllers;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.business.services.UploadFileService;
import org.sansenshimizu.sakuraboot.file.api.presentation.controllers.UploadFileController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The base test interface for all download file controllers. This interface
 * provides common tests for testing {@link UploadFileController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link UploadFileControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link UploadFileControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements UploadFileControllerTest&lt;YourEntity, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourController controller;
 *
 *     &#064;Mock
 *     private YourService service;
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourController getController() {
 *
 *         return controller;
 *     }
 *
 *     &#064;Override
 *     public YourService getService() {
 *
 *         return service;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type use in the service layer.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        UploadFileController
 * @see        SuperControllerTest
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface UploadFileControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperControllerTest<E, I>, SuperFileTest<E, I> {

    /**
     * A test file name.
     */
    String FILE_NAME = "testFile.jpg";

    /**
     * Get the {@link UploadFileController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link UploadFileController}.
     */
    @Override
    UploadFileController<E, I> getController();

    /**
     * Get the {@link UploadFileService} for test. Need to be {@link Mock}.
     *
     * @return A {@link UploadFileService}.
     */
    @Override
    UploadFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID, fileFieldName, and file,"
        + " WHEN uploading a file,"
        + " THEN the controller should return a valid response with "
        + "a text message")
    default void testUploadFile() {

        // GIVEN
        final I validId = getValidId();
        final MultipartFile multipartFile = mock();
        given(multipartFile.getOriginalFilename()).willReturn(FILE_NAME);

        for (final String fileFieldName: getFileFieldNames()) {

            // WHEN
            final ResponseEntity<?> response = getController()
                .uploadFile(validId, fileFieldName, multipartFile);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody())
                .isEqualTo(FILE_NAME + " uploaded successfully.");
            verify(getService(), times(1)).uploadFile(validId, fileFieldName,
                multipartFile);
        }
    }
}
