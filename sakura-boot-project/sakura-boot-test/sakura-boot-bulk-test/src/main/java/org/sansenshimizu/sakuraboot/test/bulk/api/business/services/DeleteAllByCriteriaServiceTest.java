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

package org.sansenshimizu.sakuraboot.test.bulk.api.business.services;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.data.jpa.domain.Specification;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.DeleteAllByCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.specification.api.business.SuperCriteriaServiceTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The base test interface for all criteria services. This interface provides
 * common tests for testing {@link DeleteAllByCriteriaService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link DeleteAllByCriteriaServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteAllByCriteriaServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements DeleteAllByCriteriaServiceTest&lt;YourEntity, YourIdType, //
 *         YourFilter&gt; {
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
 * @see        DeleteAllByCriteriaService
 * @see        SuperCriteriaServiceTest
 * @since      0.1.2
 */
public interface DeleteAllByCriteriaServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperCriteriaServiceTest<E, I, F> {

    /**
     * Get the {@link DeleteAllByCriteriaService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DeleteAllByCriteriaService}.
     */
    DeleteAllByCriteriaService<E, I, F> getService();

    @Test
    @DisplayName("GIVEN a null filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the service should delete all the entities")
    default void testDeleteAllByCriteriaWithNullFilter() {

        // WHEN
        getService().deleteAllByCriteria(null);

        // THEN
        verify(getRepository(), times(1))
            .delete(ArgumentMatchers.<Specification<E>>any());
    }

    @Test
    @DisplayName("GIVEN a filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the service should delete all the entities that match the "
        + "criteria")
    default void testDeleteAllByCriteriaWithFilter() {

        // GIVEN
        final F filter = mock(getExpectedFilterClass());

        // WHEN
        getService().deleteAllByCriteria(filter);

        // THEN
        verify(getRepository(), times(1))
            .delete(ArgumentMatchers.<Specification<E>>any());
    }
}
