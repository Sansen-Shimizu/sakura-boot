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
import java.lang.reflect.Field;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.business.services.DownloadFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.file.api.presentation.controllers.DownloadFileController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all download file controllers. This interface
 * provides common tests for testing {@link DownloadFileController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link DownloadFileControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DownloadFileControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements DownloadFileControllerTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        DownloadFileController
 * @see        SuperControllerTest
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface DownloadFileControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperControllerTest<E, I>, SuperFileTest<E, I> {

    /**
     * Get the {@link DownloadFileController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DownloadFileController}.
     */
    @Override
    DownloadFileController<E, I> getController();

    /**
     * Get the {@link DownloadFileService} for test. Need to be {@link Mock}.
     *
     * @return A {@link DownloadFileService}.
     */
    @Override
    DownloadFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN downloading a file,"
        + " THEN the controller should return a valid response with "
        + "the corresponding file")
    default void testDownloadFile() throws Exception {

        // GIVEN
        final I validId = getValidId();
        final E entityWithId = getUtil().getEntity();

        for (final String fileFieldName: getFileFieldNames()) {

            final Field fileField = ReflectionUtils
                .getField(getUtil().getEntityClass(), fileFieldName);
            final File file = (File) fileField.get(entityWithId);
            final Pair<Resource,
                String> expectedResult = Pair.of(
                    new ByteArrayResource(
                        Objects.requireNonNull(file.getBytes())),
                    file.getFilename());
            given(getService().downloadFile(any(), any()))
                .willReturn(expectedResult);

            // WHEN
            final ResponseEntity<?> response
                = getController().downloadFile(validId, fileFieldName);

            // THEN
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedResult.getKey());
        }
    }
}
