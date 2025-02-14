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

package org.sansenshimizu.sakuraboot.file.api.persistence;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;

/**
 * The base interface for all Spring Data repositories with files.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a repository for your {@link DataPresentation} that inherits from
 * {@link FileRepository}, follow these steps:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourEntityRepository
 *     extends BasicRepository&lt;YourEntity, YourIdType&gt;,
 *     FileRepository&lt;YourEntity, YourIdType&gt; {}
 *
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicRepository
 * @since      0.1.2
 */
public interface FileRepository<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> {

    /**
     * Returns the specified file of the entity with the given id.
     *
     * @param  id            The ID that will be used to find the entity.
     * @param  fileFieldName The name of the file field.
     * @param  entityType    The type of the entity.
     * @return               a {@link File}.
     */
    Optional<File>
        findFileById(I id, String fileFieldName, Class<E> entityType);

    /**
     * Update the specified file of the entity with the given id.
     *
     * @param id            The ID that will be used to find the entity.
     * @param fileFieldName The name of the file field.
     * @param entityType    The type of the entity.
     * @param file          The new {@link File}.
     */
    void updateFileById(
        I id, String fileFieldName, Class<E> entityType, @Nullable File file);
}
