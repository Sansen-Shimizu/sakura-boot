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
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.DeleteAllService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.PatchAllService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.SaveAllService;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.UpdateAllService;

/**
 * The base service interface for bulk operations.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link BasicBulkService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService //
 *     extends BasicBulkService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link BasicBulkService}, follow
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
 *     implements BasicBulkService&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends BasicBulkService.
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
 * @see        SaveAllService
 * @see        FindAllService
 * @see        UpdateAllService
 * @see        PatchAllService
 * @see        DeleteAllService
 * @since      0.1.2
 */
public interface BasicBulkService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SaveAllService<E, I>, UpdateAllService<E, I>, PatchAllService<E, I>,
    DeleteAllService<E, I> {}
