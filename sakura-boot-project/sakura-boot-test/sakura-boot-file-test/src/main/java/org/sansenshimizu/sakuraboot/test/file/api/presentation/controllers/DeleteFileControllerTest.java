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

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.business.services.DeleteFileService;
import org.sansenshimizu.sakuraboot.file.api.presentation.controllers.DeleteFileController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The base test interface for all download file controllers. This interface
 * provides common tests for testing {@link DeleteFileController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link DeleteFileControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteFileControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements DeleteFileControllerTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        DeleteFileController
 * @see        SuperControllerTest
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface DeleteFileControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperControllerTest<E, I>, SuperFileTest<E, I> {

    /**
     * Get the {@link DeleteFileController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DeleteFileController}.
     */
    @Override
    DeleteFileController<E, I> getController();

    /**
     * Get the {@link DeleteFileService} for test. Need to be {@link Mock}.
     *
     * @return A {@link DeleteFileService}.
     */
    @Override
    DeleteFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN deleting a file,"
        + " THEN the controller should return no content")
    default void testDeleteFile() {

        // GIVEN
        final I validId = getValidId();

        for (final String fileFieldName: getFileFieldNames()) {

            // WHEN
            final ResponseEntity<?> response
                = getController().deleteFile(validId, fileFieldName);

            // THEN
            assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(response.getBody()).isNull();
            verify(getService(), times(1)).deleteFile(validId, fileFieldName);
        }
    }
}
