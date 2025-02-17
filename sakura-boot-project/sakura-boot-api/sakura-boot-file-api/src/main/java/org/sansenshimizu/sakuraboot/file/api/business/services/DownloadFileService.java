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

import java.io.Serializable;
import java.lang.reflect.Field;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.file.api.persistence.FileRepository;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * The service interface for downloadFile operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link DownloadFileService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends DownloadFileService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link DownloadFileService}, follow
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
 *     implements DownloadFileService&lt;YourEntity, YourIdType&gt; {
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
 * @see        DownloadFileService#downloadFile(Comparable, String)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface DownloadFileService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    /**
     * Retrieves a {@link File} with the specified ID and fileFieldName from the
     * underlying data storage.
     * The {@link File} will be returned as a pair of a {@link Resource} and the
     * filename.
     *
     * @param  id                  The ID of the {@link DataPresentation} to
     *                             retrieve.
     * @param  fileFieldName       The name of the file field.
     * @return                     The retrieved {@link File} and the filename.
     * @throws NotFoundException   If no {@link File} with the
     *                             specified name exists in the data
     *                             storage.
     * @throws NotFoundException   If no {@link DataPresentation} with the
     *                             specified ID exists in the data
     *                             storage.
     * @throws BadRequestException If the field is not a file.
     */
    @Logging
    default Resource downloadFile(final I id, final String fileFieldName) {

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
            final File file = castRepository
                .findFileById(id, fileFieldName, getEntityClass())
                .orElseThrow(() -> new NotFoundException(fileFieldName));
            return getResultFromFile(file, fileFieldName);
        }

        final Field fileField
            = ReflectionUtils.getField(getEntityClass(), fileFieldName);
        final E entity = getRepository().findById(id)
            .orElseThrow(() -> new NotFoundException(getEntityClass(), id));

        try {

            if (fileField.get(entity) == null) {

                throw new NotFoundException(fileFieldName);
            }

            if (fileField.get(entity) instanceof final File file) {

                return getResultFromFile(file, fileFieldName);
            }
            throw new BadRequestException(
                "The field: " + fileFieldName + " is not a file.");
        } catch (final IllegalAccessException e) {

            throw new RuntimeException("Error while downloading the file.", e);
        }
    }

    private static Resource getResultFromFile(
        final File file, final String fieldFileName) {

        final byte[] bytes = file.getBytes();
        final ByteArrayResource resource;

        if (bytes == null) {

            throw new NotFoundException(fieldFileName);
        } else {

            resource = new ByteArrayResource(bytes) {

                @Override
                public String getFilename() {

                    return file.getFilename();
                }
            };
        }
        return resource;
    }
}
