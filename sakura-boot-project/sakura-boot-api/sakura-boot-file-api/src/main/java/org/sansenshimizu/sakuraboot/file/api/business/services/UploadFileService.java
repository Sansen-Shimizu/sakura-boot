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

package org.sansenshimizu.sakuraboot.file.api.business.services;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.file.api.persistence.FileRepository;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * The service interface for uploadFile operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link UploadFileService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends UploadFileService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link UploadFileService}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService //
 *     implements UploadFileService&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends BasicService.
 *     private final YourRepository repository;
 *
 *     public YourService(final YourRepository repository) {
 *
 *         this.repository = repository;
 *     }
 *
 *     public YourRepository getRepository() {
 *
 *         return this.repository;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperService
 * @see        UploadFileService#uploadFile(Comparable, String, MultipartFile)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface UploadFileService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    /**
     * Uploads a {@link File} with the specified ID and fileFieldName in the
     * underlying data storage.
     *
     * @param  id                The ID of the {@link DataPresentation} to
     *                           update.
     * @param  fileFieldName     The name of the file field.
     * @param  file              The {@link MultipartFile} to upload.
     * @throws NotFoundException If no {@link File} with the specified name
     *                           exists in the data storage.
     * @throws NotFoundException If no {@link DataPresentation} with the
     *                           specified ID exists in the data storage.
     */
    @Logging
    @Transactional
    default void uploadFile(
        final I id, final String fileFieldName, final MultipartFile file) {

        final File newFile;

        try {

            final String originalFilename = file.getOriginalFilename();

            if (originalFilename == null) {

                throw new BadRequestException(
                    "The original filename of the multipartFile is null.");
            }
            newFile = File.builder()
                .filename(originalFilename)
                .contentType(file.getContentType())
                .bytes(file.getBytes())
                .build();
        } catch (final IOException e) {

            throw new RuntimeException("Error while reading the multipartFile.",
                e);
        }

        if (getRepository() instanceof final FileRepository<?,
            ?> fileRepository) {

            if (!ReflectionUtils.isFieldExists(getEntityClass(),
                fileFieldName)) {

                throw new NotFoundException(fileFieldName);
            }

            if (!getRepository().existsById(id)) {

                throw new NotFoundException(getEntityClass(), id);
            }

            @SuppressWarnings("unchecked")
            final FileRepository<E, I> castRepository
                = (FileRepository<E, I>) fileRepository;
            castRepository.updateFileById(id, fileFieldName, getEntityClass(),
                newFile);
            return;
        }

        final Field fileField
            = ReflectionUtils.getField(getEntityClass(), fileFieldName);
        final E entity = getRepository().findById(id)
            .orElseThrow(() -> new NotFoundException(getEntityClass(), id));

        try {

            fileField.set(entity, newFile);
        } catch (final IllegalAccessException e) {

            throw new RuntimeException("Error while uploading the file.", e);
        }
        getRepository().save(entity);
    }
}
