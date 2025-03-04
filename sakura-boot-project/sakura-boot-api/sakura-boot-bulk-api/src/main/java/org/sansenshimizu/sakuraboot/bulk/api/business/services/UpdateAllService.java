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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.bulk.api.persistence.BulkRepository;
import org.sansenshimizu.sakuraboot.cache.api.annotations.PutCache;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;

/**
 * The service interface for updateAll operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link UpdateAllService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends UpdateAllService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link UpdateAllService}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService
 *     implements UpdateAllService&lt;YourEntity, YourIdType&gt; {
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
 * @see        UpdateAllService#updateAll(Collection)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface UpdateAllService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    @Override
    BasicRepository<E, I> getRepository();

    /**
     * Updates all existing {@link DataPresentation} with the provided data from
     * all the {@link DataPresentation} in the underlying data storage.
     *
     * @param  datas               All the {@link DataPresentation} that will be
     *                             used to
     *                             update.
     * @return                     All the updated {@link DataPresentation}.
     * @throws BadRequestException If one of the provided
     *                             {@link DataPresentation} does not have an
     *                             ID.
     * @throws NotFoundException   If no {@link DataPresentation} with the
     *                             specified ID exists in the data storage.
     */
    @Transactional
    @PutCache(key = "#datas[i].id", refreshEntityCache = true)
    @Mapping
    @Logging
    default List<DataPresentation<I>> updateAll(
        final Collection<DataPresentation<I>> datas) {

        final List<E> entities;

        try {

            entities = datas.stream()
                .map(data -> getEntityClass().cast(data))
                .toList();
        } catch (final ClassCastException e) {

            throw new BadRequestException(
                "Can't update when the provided object is not an entity : "
                    + getEntityClass().getSimpleName(),
                e);
        }

        final Set<I> datasId = getIds(datas);

        if (!getRepository().existsByIds(datasId, (long) datas.size())) {

            throw new NotFoundException(getEntityClass().getSimpleName());
        }

        final List<E> result;

        if (getRepository() instanceof final BulkRepository<?,
            ?> bulkRepository) {

            @SuppressWarnings("unchecked")
            final BulkRepository<E, I> castBulkRepository
                = (BulkRepository<E, I>) bulkRepository;
            result = castBulkRepository.bulkUpdate(entities);
        } else {

            result = getRepository().saveAll(entities);
        }
        @SuppressWarnings("unchecked")
        final List<DataPresentation<I>> castResult
            = (List<DataPresentation<I>>) result;
        return castResult;
    }

    private Set<I> getIds(final Collection<DataPresentation<I>> datas) {

        if (datas.stream()
            .map(DataPresentation::getId)
            .anyMatch(Objects::isNull)) {

            throw new BadRequestException(
                "Can't update an entity when they don't have an ID : "
                    + getEntityClass().getSimpleName());
        }

        return datas.stream()
            .map(DataPresentation::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
    }
}
