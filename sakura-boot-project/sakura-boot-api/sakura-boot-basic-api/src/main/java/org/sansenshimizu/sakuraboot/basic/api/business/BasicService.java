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

package org.sansenshimizu.sakuraboot.basic.api.business;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.DeleteByIdService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.PatchByIdService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.SaveService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.UpdateByIdService;

/**
 * The base service interface for CRUD operations.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link BasicService}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService //
 *     extends BasicService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link BasicService}, follow these
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
 *     implements BasicService&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends BasicService.
 *     private final YourRepository repository;
 *
 *     private final ObjectMapper objectMapper;
 *
 *     public YourService(
 *         final YourRepository repository, final ObjectMapper objectMapper) {
 *
 *         this.repository = repository;
 *         this.objectMapper = objectMapper;
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
 *
 *     public ObjectMapper getObjectMapper() {
 *
 *         return objectMapper;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SaveService
 * @see        FindAllService
 * @see        FindByIdService
 * @see        UpdateByIdService
 * @see        PatchByIdService
 * @see        DeleteByIdService
 * @since      0.1.0
 */
public interface BasicService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SaveService<E, I>, FindAllService<E, I>, FindByIdService<E, I>,
    UpdateByIdService<E, I>, PatchByIdService<E, I>, DeleteByIdService<E, I> {}
