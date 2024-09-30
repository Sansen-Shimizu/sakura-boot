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

package org.sansenshimizu.sakuraboot.test.basic.api.business.services;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link FindAllService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link FindAllServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements BasicServiceTest&lt;YourEntity, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourService service;
 *
 *     &#064;Mock
 *     private YourRepository repository;
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
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        FindAllService
 * @see        SuperServiceTest
 * @since      0.1.0
 */
public interface FindAllServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link FindAllService} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link FindAllService}.
     */
    FindAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN a pageable request,"
        + " WHEN finding all,"
        + " THEN the service should return a page of entities")
    default void testFindAllWithPageable() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final Pageable pageable = Pageable.ofSize(1);
        given(getRepository().findAll(any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(entityWithId)));

        // WHEN
        final Page<DataPresentation<I>> foundPage
            = getService().findAll(pageable);

        // THEN
        assertThat(foundPage).isEqualTo(new PageImpl<>(List.of(entityWithId)));
    }

    @Test
    @DisplayName("GIVEN no pageable request,"
        + " WHEN finding all,"
        + " THEN the service should return a page of entities")
    default void testFindAll() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final E otherEntityWithId = getUtil().getDifferentEntity();
        given(getRepository().findAll(any(Pageable.class))).willReturn(
            new PageImpl<>(List.of(entityWithId, otherEntityWithId)));

        // WHEN
        final Page<DataPresentation<I>> foundPage
            = getService().findAll(Pageable.unpaged());

        // THEN
        assertThat(foundPage).isEqualTo(
            new PageImpl<>(List.of(entityWithId, otherEntityWithId)));
    }
}
