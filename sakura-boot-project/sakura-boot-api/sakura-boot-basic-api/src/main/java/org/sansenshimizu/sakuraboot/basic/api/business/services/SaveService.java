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

package org.sansenshimizu.sakuraboot.basic.api.business.services;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.SaveWithRelationship;
import org.sansenshimizu.sakuraboot.cache.api.annotations.PutCache;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;

/**
 * The service interface for save operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link SaveService}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService //
 *     extends SaveService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link SaveService}, follow these
 * steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService //
 *     implements SaveService&lt;YourEntity, YourIdType&gt; {
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
 * @see        SaveService#save(DataPresentation)
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SaveService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    @Override
    BasicRepository<E, I> getRepository();

    /**
     * Saves a new {@link DataPresentation} in the underlying data storage.
     *
     * @param  data                The {@link DataPresentation} to save.
     * @return                     The saved {@link DataPresentation}.
     * @throws BadRequestException If the {@link DataPresentation} already has
     *                             an ID assigned (i.e., already exists in the
     *                             data storage).
     */
    @Transactional
    @PutCache(refreshEntityCache = true)
    @Mapping
    @SaveWithRelationship
    @Logging
    default DataPresentation<I> save(final DataPresentation<I> data) {

        if (data.getId() != null) {

            throw new BadRequestException(
                "Can't save an entity when already have an ID : "
                    + getEntityClass().getSimpleName());
        }

        final E entity;

        try {

            entity = getEntityClass().cast(data);
        } catch (final ClassCastException e) {

            throw new BadRequestException(
                "Can't save when the provided object is not an entity : "
                    + getEntityClass().getSimpleName(),
                e);
        }

        return getRepository().save(entity);
    }
}
