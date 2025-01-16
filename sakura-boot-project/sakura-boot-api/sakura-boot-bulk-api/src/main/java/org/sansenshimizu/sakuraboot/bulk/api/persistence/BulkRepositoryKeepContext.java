/*
 * Copyright (C) 2023-2024 Malcolm Rozé.
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

package org.sansenshimizu.sakuraboot.bulk.api.persistence;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * The base interface for all Spring Data repositories with bulk operations.
 * The persistence context will not be cleared after the flush by default.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a repository for your {@link DataPresentation} that inherits from
 * {@link BulkRepositoryKeepContext}, follow these steps:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourEntityRepository
 *     extends BulkRepositoryKeepContext&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @since      0.1.2
 */
public interface BulkRepositoryKeepContext<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends BulkRepository<E, I> {

    @Override
    default boolean clearContextAfterFlush() {

        return false;
    }
}
