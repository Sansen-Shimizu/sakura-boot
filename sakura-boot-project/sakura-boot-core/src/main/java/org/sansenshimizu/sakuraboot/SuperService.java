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

package org.sansenshimizu.sakuraboot;

import java.io.Serializable;

import org.springframework.transaction.annotation.Transactional;

/**
 * The base service interface.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link SuperService}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService //
 *     extends SuperService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link SuperService}, follow these
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
 *     implements SuperService&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends SuperService.
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
 *
 *     public Class&lt;YourEntity&gt; getEntityClass() {
 *
 *         return YourEntity.class;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@Transactional(readOnly = true)
public interface SuperService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> {

    /**
     * Give the repository use by the service.
     *
     * @return A repository.
     */
    @SuppressWarnings("EmptyMethod")
    SuperRepository<E, I> getRepository();

    /**
     * Retrieves the class of the DataPresentation associated with this service.
     *
     * @return The class of the DataPresentation.
     */
    Class<E> getEntityClass();
}
