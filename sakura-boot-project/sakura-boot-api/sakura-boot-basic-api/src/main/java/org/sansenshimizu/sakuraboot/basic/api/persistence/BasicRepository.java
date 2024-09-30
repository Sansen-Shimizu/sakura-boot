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

package org.sansenshimizu.sakuraboot.basic.api.persistence;

import java.io.Serializable;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;

/**
 * The base interface for all Spring Data repositories.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a repository for your {@link DataPresentation} that inherits from
 * {@link BasicRepository}, follow these steps:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourEntityRepository
 *     extends BasicRepository&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperRepository
 * @since      0.1.0
 */
@NoRepositoryBean
public interface BasicRepository<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperRepository<E, I> {

    /**
     * Returns whether all entities with the given ids exist.
     *
     * @param  ids   Mustn't be null.
     * @param  count The expected number of entities.
     * @return       true if all entities with the given ids exist, false
     *               otherwise.
     */
    @Query("SELECT COUNT(e) = :count FROM #{#entityName} e WHERE e.id IN :ids")
    boolean existsByIds(@Param("ids") Set<I> ids, @Param("count") Long count);

    /**
     * Returns a page of entities ID meeting the paging restriction provided in
     * the {@link Pageable} object.
     *
     * @param  pageable the pageable to request a paged result, can be
     *                  {@link Pageable#unpaged()}, mustn't be null.
     * @return          a page of entity ID.
     */
    @Query(
        value = "SELECT e.id FROM #{#entityName} e",
        countQuery = "SELECT count(e) FROM #{#entityName} e")
    Page<I> findAllIds(Pageable pageable);
}
