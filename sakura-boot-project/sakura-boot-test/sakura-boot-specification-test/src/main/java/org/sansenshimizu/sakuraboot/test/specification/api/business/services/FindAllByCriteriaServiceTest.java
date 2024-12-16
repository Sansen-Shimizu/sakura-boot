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

package org.sansenshimizu.sakuraboot.test.specification.api.business.services;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.specification.api.business.CriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.specification.api.business.services.FindAllByCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.persistence.CriteriaRepository;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * The base test interface for all criteria services. This interface provides
 * common tests for testing {@link CriteriaService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link FindAllByCriteriaServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllByCriteriaServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements
 *     AbstractCriteriaServiceTest&lt;YourEntity, YourIdType, YourFilter&gt; {
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
 * @see        CriteriaService
 * @see        SuperServiceTest
 * @since      0.1.0
 */
public interface FindAllByCriteriaServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperServiceTest<E, I> {

    /**
     * Get the {@link CriteriaService} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link CriteriaService}.
     */
    @Override
    FindAllByCriteriaService<E, I, F> getService();

    /**
     * Get the {@link CriteriaRepository} for test. Need to be {@link Mock}.
     *
     * @return A {@link CriteriaRepository}.
     */
    @Override
    CriteriaRepository<E, I> getRepository();

    /**
     * Get the {@link SpecificationBuilder} for test. Need to be {@link Mock}.
     *
     * @return A {@link SpecificationBuilder}.
     */
    SpecificationBuilder<E> getSpecificationBuilder();

    /**
     * Get the expected class of the filter.
     *
     * @return The expected class of the filter.
     */
    default Class<F> getExpectedFilterClass() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            FindAllByCriteriaServiceTest.class.getTypeName(), 2);
    }

    @Test
    @DisplayName("GIVEN a null filter and pageable,"
        + " WHEN findAllByCriteria is called,"
        + " THEN should return all entities")
    default void testFindByCriteriaWithNullFilter() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final E otherEntityWithId = getUtil().getDifferentEntity();
        given(getRepository().findAll(ArgumentMatchers.<Specification<E>>any(),
            any(Pageable.class))).willReturn(
                new PageImpl<>(List.of(entityWithId, otherEntityWithId)));

        // WHEN
        final Page<DataPresentation<I>> result
            = getService().findAllByCriteria(null, Pageable.unpaged());

        // THEN
        assertThat(result).isEqualTo(
            new PageImpl<>(List.of(entityWithId, otherEntityWithId)));
    }

    @Test
    @DisplayName("GIVEN a valid filter and pageable,"
        + " WHEN findAllByCriteria is called,"
        + " THEN should return a Page of entity that matches the criteria")
    default void testFindByCriteriaWithFilter() {

        // GIVEN
        final F filter = mock(getExpectedFilterClass());
        final E entityWithId = getUtil().getEntity();
        given(getRepository().findAll(ArgumentMatchers.<Specification<E>>any(),
            any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(entityWithId)));

        // WHEN
        final Page<DataPresentation<I>> result
            = getService().findAllByCriteria(filter, Pageable.unpaged());

        // THEN
        assertThat(result).isEqualTo(new PageImpl<>(List.of(entityWithId)));
    }
}
