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

package org.sansenshimizu.sakuraboot.specification.api.business.services;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.cache.api.annotations.Caching;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;
import org.sansenshimizu.sakuraboot.specification.api.business.SuperCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.relationship.annotations.FindAllByCriteriaWithRelationship;

/**
 * The {@link FindAllByCriteriaService} interface provides the contract for
 * performing CRUD operations with filtering
 * and pagination capabilities using criteria-based queries.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface with filtering support that inherits from
 * {@link FindAllByCriteriaService}, follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends CriteriaService&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class with filtering support that implements
 * {@link FindAllByCriteriaService}, follow these steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService
 *     implements CriteriaService&lt;YourEntity, YourIdType, YourFilter&gt; {
 *
 *     // Or implements your interface that extends CriteriaService.
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
 *     &#064;Override
 *     public YourRepository getRepository() {
 *
 *         return repository;
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
 * @see        FindAllByCriteriaService#findAllByCriteria(FilterPresentation,
 *             Pageable)
 * @see        SuperService
 * @since      0.1.0
 */
public interface FindAllByCriteriaService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperCriteriaService<E, I, F> {

    /**
     * Retrieves a page of {@link DataPresentation} from the underlying data
     * storage based on the provided {@link FilterPresentation} and pageable
     * criteria.
     *
     * @param  filter   The {@link FilterPresentation} object to apply
     *                  criteria-based filtering.
     * @param  pageable The {@link Pageable} object for pagination and sorting.
     * @return          A Page object containing the filtered and paginated
     *                  results.
     */
    @Caching(concatToCacheName = "All")
    @Mapping(mapFirstArgument = false)
    @FindAllByCriteriaWithRelationship
    @Logging
    default Page<DataPresentation<I>> findAllByCriteria(
        @Nullable final F filter, final Pageable pageable) {

        final Specification<E> specification = getSpecification(filter);

        @SuppressWarnings("unchecked")
        final Page<DataPresentation<I>> result
            = getRepository().findAll(specification, pageable)
                .map(DataPresentation.class::cast);
        return result;
    }
}
