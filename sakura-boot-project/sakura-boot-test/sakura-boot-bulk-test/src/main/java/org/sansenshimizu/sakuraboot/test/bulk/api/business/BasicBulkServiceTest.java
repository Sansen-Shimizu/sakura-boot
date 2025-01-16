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

package org.sansenshimizu.sakuraboot.test.bulk.api.business;

import java.io.Serializable;

import org.mockito.InjectMocks;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.BasicBulkService;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.DeleteAllServiceTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.PatchAllServiceTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.SaveAllServiceTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.UpdateAllServiceTest;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link BasicBulkService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link BasicBulkServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicBulkServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements BasicBulkServiceTest&lt;YourEntity, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourService service;
 *
 *     &#064;Mock
 *     private YourRepository repository;
 *
 *     &#064;Mock
 *     private ObjectMapper objectMapper;
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourService getService() {
 *
 *         return service;
 *     }
 *
 *     &#064;Override
 *     public YourRepository getRepository() {
 *
 *         return repository;
 *     }
 *
 *     &#064;Override
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
 * @see        BasicBulkService
 * @see        SaveAllServiceTest
 * @see        UpdateAllServiceTest
 * @see        PatchAllServiceTest
 * @see        DeleteAllServiceTest
 * @since      0.1.2
 */
public interface BasicBulkServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SaveAllServiceTest<E, I>, UpdateAllServiceTest<E, I>,
    PatchAllServiceTest<E, I>, DeleteAllServiceTest<E, I> {

    /**
     * Get the {@link BasicBulkService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link BasicBulkService}.
     */
    BasicBulkService<E, I> getService();
}
