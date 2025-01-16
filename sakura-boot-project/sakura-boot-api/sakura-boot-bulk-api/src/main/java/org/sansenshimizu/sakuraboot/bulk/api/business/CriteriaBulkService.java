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

package org.sansenshimizu.sakuraboot.bulk.api.business;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.DeleteAllByCriteriaService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.PatchAllService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.SaveAllService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.UpdateAllService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;

/**
 * The base service interface for bulk operations with filtering.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface with filtering support that inherits from
 * {@link CriteriaBulkService}, follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends CriteriaBulkService&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class with filtering support that implements
 * {@link CriteriaBulkService}, follow these steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService
 *     implements //
 *     CriteriaBulkService&lt;YourEntity, YourIdType, YourFilter&gt; {
 *
 *     // Or implements your interface that extends CriteriaBulkService.
 *     private final YourRepository repository;
 *
 *     private final ObjectMapper objectMapper;
 *
 *     private final SpecificationBuilder&lt;YourEntity&gt;//
 *     specificationBuilder;
 *
 *     public YourService(
 *         final YourRepository repository, final ObjectMapper objectMapper,
 *         final SpecificationBuilder&lt;YourEntity&gt; specificationBuilder) {
 *
 *         this.repository = repository;
 *         this.objectMapper = objectMapper;
 *         this.specificationBuilder = specificationBuilder;
 *     }
 *
 *     public YourRepository getRepository() {
 *
 *         return repository;
 *     }
 *
 *     public ObjectMapper getObjectMapper() {
 *
 *         return objectMapper;
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
 * @see        SaveAllService
 * @see        UpdateAllService
 * @see        PatchAllService
 * @see        DeleteAllByCriteriaService
 * @since      0.1.2
 */
public interface CriteriaBulkService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>>
    extends SaveAllService<E, I>, UpdateAllService<E, I>, PatchAllService<E, I>,
    DeleteAllByCriteriaService<E, I, F> {}
