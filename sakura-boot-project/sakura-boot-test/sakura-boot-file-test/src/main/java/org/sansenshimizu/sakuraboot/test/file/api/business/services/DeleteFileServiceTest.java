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
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.file.api.business.services.DeleteFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.FileRepository;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link DeleteFileService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link DeleteFileServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteFileServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements DeleteFileServiceTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        DeleteFileService
 * @see        SuperServiceTest
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface DeleteFileServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I>, SuperFileTest<E, I> {

    /**
     * Get the {@link DeleteFileService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DeleteFileService}.
     */
    DeleteFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN deleting a file,"
        + " THEN the service should delete it.")
    default void testDeleteFile() {

        // GIVEN
        final E entityWithId;

        if (getRepository() instanceof FileRepository<?, ?>) {

            entityWithId = getUtil().getEntity();
            given(getRepository().existsById(any())).willReturn(true);
        } else {

            entityWithId = SerializationUtils.clone(getUtil().getEntity());
            given(getRepository().findById(any()))
                .willReturn(Optional.of(entityWithId));
        }

        for (final String fileFieldName: getFileFieldNames()) {

            // WHEN
            getService().deleteFile(getValidId(), fileFieldName);

            // THEN
            if (getRepository() instanceof final FileRepository<?,
                ?> fileRepository) {

                @SuppressWarnings("unchecked")
                final FileRepository<E, I> castRepository
                    = (FileRepository<E, I>) fileRepository;
                verify(castRepository, times(1)).updateFileById(getValidId(),
                    fileFieldName, getUtil().getEntityClass(), null);
            } else {

                verify(getRepository(), times(1)).save(entityWithId);
            }
        }
    }

    @Test
    @DisplayName("GIVEN an invalid ID,"
        + " WHEN deleting a file,"
        + " THEN the service should throw NotFoundException")
    default void testDeleteFileWithInvalidId() {

        // GIVEN
        final I invalidId = getInvalidId();

        if (getRepository() instanceof FileRepository<?, ?>) {

            given(getRepository().existsById(any())).willReturn(false);
        } else {

            given(getRepository().findById(any())).willReturn(Optional.empty());
        }
        final DeleteFileService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.deleteFile(invalidId, getFileFieldNames().get(0)))

            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found with ID : " + invalidId);
    }

    @Test
    @DisplayName("GIVEN an invalid file field name,"
        + " WHEN deleting a file,"
        + " THEN the service should throw NotFoundException")
    default void testDeletingFileWithInvalidFileFieldName() {

        // GIVEN
        final I id = Objects.requireNonNull(getUtil().getEntity().getId());
        final String invalidFileFieldName = getInvalidFileFieldName();
        final DeleteFileService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.deleteFile(id, invalidFileFieldName))

            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(invalidFileFieldName + " not found");
    }
}
