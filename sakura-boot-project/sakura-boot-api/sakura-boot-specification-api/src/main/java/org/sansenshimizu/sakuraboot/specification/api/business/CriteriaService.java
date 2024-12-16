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

package org.sansenshimizu.sakuraboot.specification.api.business;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.DeleteByIdService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.PatchByIdService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.SaveService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.UpdateByIdService;
import org.sansenshimizu.sakuraboot.specification.api.business.services.FindAllByCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;

/**
 * The {@link CriteriaService} interface provides the contract for performing
 * CRUD operations with filtering and pagination capabilities using
 * criteria-based queries.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface with filtering support that inherits from
 * {@link CriteriaService}, follow these steps:
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
 * {@link CriteriaService}, follow these steps:
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
 *     public Class&lt;YourEntity&gt; getEntityClassToMap() {
 *
 *         return YourEntity.class;
 *     }
 *
 *     public ObjectMapper getObjectMapper() {
 *
 *         return objectMapper;
 *     }
 *
 *     &#064;Override
 *     public void createSpecification(final YourFilter filter) {
 *
 *         specificationBuilder.createSpecificationForField(filter.getId(),
 *             YourEntity_.id);
 *         specificationBuilder.createSpecificationForField(
 *             filter.getOtherField(), YourEntity_.otherField);
 *         // Same thing for all your field in your entity.
 *         specificationBuilder.createSpecificationForField(
 *             YourEntity_.relationalField, JoinType.LEFT,
 *             filter.getRelationalField().getField(),
 *             YourRelationalEntity_.field);
 *         // For relational filtering.
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
 * @see        SaveService
 * @see        FindAllByCriteriaService
 * @see        FindByIdService
 * @see        UpdateByIdService
 * @see        PatchByIdService
 * @see        DeleteByIdService
 * @since      0.1.0
 */
public interface CriteriaService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>>
    extends SaveService<E, I>, FindAllByCriteriaService<E, I, F>,
    FindByIdService<E, I>, UpdateByIdService<E, I>, PatchByIdService<E, I>,
    DeleteByIdService<E, I> {}
