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

package org.sansenshimizu.sakuraboot.basic.api.relationship;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;

/**
 * The base interface for all Spring Data repositories with relationship.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a repository for your {@link DataPresentation} that inherits from
 * {@link FetchRelationshipRepository}, follow these steps:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourEntityRepository
 *     extends BasicRepository&lt;YourEntity, YourIdType&gt;,
 *     FetchRelationshipRepository&lt;YourEntity, YourIdType&gt; {}
 *
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicRepository
 * @since      0.1.0
 */
public interface FetchRelationshipRepository<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> {

    /**
     * Returns a list of entities with the same ID provided in the list
     * parameter.
     * Perform a LEFT JOIN FETCH on the relationship.
     *
     * @param  parentIds  the list of entity's ID that will be used to find all
     *                    the entities.
     * @param  entityType The type of the entity.
     * @return            a list of entities.
     */
    List<E> findAllEagerRelationship(List<I> parentIds, Class<E> entityType);

    /**
     * Retrieves an entity by its id.
     * Perform a LEFT JOIN FETCH on the relationship.
     *
     * @param  id         The ID of the entity to retrieve.
     * @param  entityType The type of the entity.
     * @return            the entity with the given id or Optional#empty() if
     *                    none found.
     */
    Optional<E> findByIdEagerRelationship(I id, Class<E> entityType);
}
