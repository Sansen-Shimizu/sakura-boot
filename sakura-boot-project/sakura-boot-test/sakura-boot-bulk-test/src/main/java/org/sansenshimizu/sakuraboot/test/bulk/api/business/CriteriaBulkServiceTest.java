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
import org.sansenshimizu.sakuraboot.bulk.api.business.CriteriaBulkService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.DeleteAllByCriteriaServiceTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.PatchAllServiceTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.SaveAllServiceTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.business.services.UpdateAllServiceTest;

/**
 * The base test interface for all criteria bulk services. This interface
 * provides common tests for testing {@link CriteriaBulkService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link CriteriaBulkServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link CriteriaBulkServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements CriteriaBulkServiceTest&lt;YourEntity, YourIdType, YourFilter&gt; {
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
 *     &#064;Mock
 *     private SpecificationBuilder&lt;YourEntity&gt; specificationBuilder;
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
 * @see        CriteriaBulkService
 * @see        SaveAllServiceTest
 * @see        UpdateAllServiceTest
 * @see        PatchAllServiceTest
 * @see        DeleteAllByCriteriaServiceTest
 * @since      0.1.2
 */
public interface CriteriaBulkServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>>
    extends SaveAllServiceTest<E, I>, UpdateAllServiceTest<E, I>,
    PatchAllServiceTest<E, I>, DeleteAllByCriteriaServiceTest<E, I, F> {

    /**
     * Get the {@link CriteriaBulkService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link CriteriaBulkService}.
     */
    @Override
    CriteriaBulkService<E, I, F> getService();
}
