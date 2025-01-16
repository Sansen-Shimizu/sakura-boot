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
import org.sansenshimizu.sakuraboot.cache.api.annotations.RemoveCache;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;

/**
 * The service interface for deleteById operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link DeleteByIdService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends DeleteByIdService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link DeleteByIdService}, follow
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
 *     implements DeleteByIdService&lt;YourEntity, YourIdType&gt; {
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
 * @see        DeleteByIdService#deleteById(Comparable)
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface DeleteByIdService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    @Override
    BasicRepository<E, I> getRepository();

    /**
     * Deletes a {@link DataPresentation} with the specified ID from the
     * underlying data storage.
     *
     * @param  id                The ID of the {@link DataPresentation} to
     *                           delete.
     * @throws NotFoundException If no {@link DataPresentation} with the
     *                           specified ID exists in the data
     *                           storage.
     */
    @Transactional
    @RemoveCache(key = "#id", refreshEntityCache = true)
    @Logging
    default void deleteById(final I id) {

        if (!getRepository().existsById(id)) {

            throw new NotFoundException(getEntityClass(), id);
        }
        getRepository().deleteById(id);
    }
}
