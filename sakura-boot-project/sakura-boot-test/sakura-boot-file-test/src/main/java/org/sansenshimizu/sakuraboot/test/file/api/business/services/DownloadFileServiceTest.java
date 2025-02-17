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

package org.sansenshimizu.sakuraboot.test.file.api.business.services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.core.io.Resource;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.file.api.business.services.DownloadFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.file.api.persistence.FileRepository;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link DownloadFileService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link DownloadFileServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DownloadFileServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements DownloadFileServiceTest&lt;YourEntity, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourService service;
 *
 *     &#064;Mock
 *     private YourRepository repository;
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourService getService() {
 *
 *         return service;
 *     }
 *
 *     &#064;Override
 *     public YourRepository getRepository() {
 *
 *         return repository;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        DownloadFileService
 * @see        SuperServiceTest
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface DownloadFileServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I>, SuperFileTest<E, I> {

    /**
     * Get the {@link DownloadFileService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DownloadFileService}.
     */
    DownloadFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN downloading a file,"
        + " THEN the service should return the corresponding file")
    default void testDownloadFile() throws Exception {

        // GIVEN
        final E entityWithId = getUtil().getEntity();

        for (final String fileFieldName: getFileFieldNames()) {

            final Field fileField = ReflectionUtils
                .getField(getUtil().getEntityClass(), fileFieldName);
            final File file = (File) fileField.get(entityWithId);

            if (getRepository() instanceof final FileRepository<?,
                ?> fileRepository) {

                given(getRepository().existsById(any())).willReturn(true);
                given(fileRepository.findFileById(any(), any(), any()))
                    .willReturn(Optional.of(file));
            } else {

                given(getRepository().findById(any()))
                    .willReturn(Optional.of(entityWithId));
            }

            // WHEN
            final Resource foundFile
                = getService().downloadFile(getValidId(), fileFieldName);

            // THEN
            assertThat(foundFile.getContentAsByteArray())
                .isEqualTo(file.getBytes());
            assertThat(foundFile.getFilename()).isEqualTo(file.getFilename());
        }
    }

    @Test
    @DisplayName("GIVEN an invalid ID,"
        + " WHEN downloading a file,"
        + " THEN the service should throw NotFoundException")
    default void testDownloadFileWithInvalidId() {

        // GIVEN
        final I invalidId = getInvalidId();

        if (getRepository() instanceof FileRepository<?, ?>) {

            given(getRepository().existsById(any())).willReturn(false);
        } else {

            given(getRepository().findById(any())).willReturn(Optional.empty());
        }
        final DownloadFileService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.downloadFile(invalidId,
            getFileFieldNames().get(0)))

            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found with ID : " + invalidId);
    }

    @Test
    @DisplayName("GIVEN an invalid file field name,"
        + " WHEN downloading a file,"
        + " THEN the service should throw NotFoundException")
    default void testDownloadFileWithInvalidFileFieldName() {

        // GIVEN
        final I id = Objects.requireNonNull(getUtil().getEntity().getId());
        final String invalidFileFieldName = getInvalidFileFieldName();
        final DownloadFileService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.downloadFile(id, invalidFileFieldName))

            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(invalidFileFieldName + " not found");
    }
}
