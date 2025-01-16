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

package org.sansenshimizu.sakuraboot.bulk.api.business.services;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.SaveWithRelationship;
import org.sansenshimizu.sakuraboot.bulk.api.persistence.BulkRepository;
import org.sansenshimizu.sakuraboot.cache.api.annotations.PutCache;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;

/**
 * The service interface for saveAll operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link SaveAllService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService //
 *     extends SaveAllService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link SaveAllService}, follow
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
 *     implements SaveAllService&lt;YourEntity, YourIdType&gt; {
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
 * @see        SaveAllService#saveAll(Collection)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SaveAllService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    @Override
    BasicRepository<E, I> getRepository();

    /**
     * Saves all new {@link DataPresentation} in the underlying data storage.
     *
     * @param  datas               All the {@link DataPresentation} to save.
     * @return                     All the saved {@link DataPresentation}.
     * @throws BadRequestException If one of the {@link DataPresentation}
     *                             already has n ID assigned (i.e., already
     *                             exists in the data storage).
     */
    @Transactional
    @PutCache(refreshEntityCache = true)
    @Mapping
    @SaveWithRelationship
    @Logging
    default List<DataPresentation<I>> saveAll(
        final Collection<DataPresentation<I>> datas) {

        if (datas.stream().anyMatch(data -> data.getId() != null)) {

            throw new BadRequestException(
                "Can't save an entity when already have an ID : "
                    + getEntityClass().getSimpleName());
        }

        final Collection<E> entities;

        try {

            entities = datas.stream()
                .map(data -> getEntityClass().cast(data))
                .toList();
        } catch (final ClassCastException e) {

            throw new BadRequestException(
                "Can't save when the provided object is not an entity : "
                    + getEntityClass().getSimpleName(),
                e);
        }

        final List<E> result;

        if (getRepository() instanceof final BulkRepository<?,
            ?> bulkRepository) {

            @SuppressWarnings("unchecked")
            final BulkRepository<E, I> castBulkRepository
                = (BulkRepository<E, I>) bulkRepository;
            result = castBulkRepository.bulkInsert(entities);
        } else {

            result = getRepository().saveAll(entities);
        }
        @SuppressWarnings("unchecked")
        final List<DataPresentation<I>> castResult
            = (List<DataPresentation<I>>) result;
        return castResult;
    }
}
