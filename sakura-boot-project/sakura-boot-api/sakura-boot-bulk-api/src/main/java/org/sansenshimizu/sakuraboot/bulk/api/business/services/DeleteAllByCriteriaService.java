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

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.cache.api.annotations.RemoveCache;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.specification.api.business.SuperCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;

/**
 * The service interface for deleteAll operation with filtering.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from
 * {@link DeleteAllByCriteriaService}, follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends DeleteAllByCriteriaService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link DeleteAllByCriteriaService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService
 *     implements DeleteAllByCriteriaService&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends BasicService.
 *     private final YourRepository repository;
 *
 *     private final SpecificationBuilder&lt;YourEntity&gt;//
 *     specificationBuilder;
 *
 *     public YourService(
 *         final YourRepository repository,
 *         final SpecificationBuilder&lt;YourEntity&gt; specificationBuilder) {
 *
 *         this.repository = repository;
 *         this.specificationBuilder = specificationBuilder;
 *     }
 *
 *     public YourRepository getRepository() {
 *
 *         return this.repository;
 *     }
 *
 *     &#064;Override
 *     public SpecificationBuilder&lt;YourEntity&gt; getSpecificationBuilder() {
 *
 *         return specificationBuilder;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        SuperService
 * @see        DeleteAllByCriteriaService#deleteAllByCriteria(FilterPresentation)
 * @since      0.1.2
 */
public interface DeleteAllByCriteriaService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperCriteriaService<E, I, F> {

    /**
     * Deletes all the {@link DataPresentation} from the underlying data storage
     * based on the provided filter.
     *
     * @param filter The {@link FilterPresentation} object to apply
     *               criteria-based filtering.
     */
    @Transactional
    @RemoveCache(refreshEntityCache = true)
    @Logging
    default void deleteAllByCriteria(@Nullable final F filter) {

        final Specification<E> specification = getSpecification(filter);
        getRepository().delete(specification);
    }
}
