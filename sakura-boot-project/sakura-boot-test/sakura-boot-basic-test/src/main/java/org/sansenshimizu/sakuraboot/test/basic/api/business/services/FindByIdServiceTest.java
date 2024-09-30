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
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link FindByIdService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link FindByIdServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindByIdServiceTest} class:
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
 * @see        FindByIdService
 * @see        SuperServiceTest
 * @since      0.1.0
 */
public interface FindByIdServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link FindByIdService} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link FindByIdService}.
     */
    FindByIdService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID,"
        + " WHEN finding by ID,"
        + " THEN the service should return the corresponding entity")
    default void testFindById() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        given(getRepository().findById(any()))
            .willReturn(Optional.of(entityWithId));

        // WHEN
        final DataPresentation<I> foundEntity
            = getService().findById(getValidId());

        // THEN
        assertThat(foundEntity).isEqualTo(entityWithId);
    }

    @Test
    @DisplayName("GIVEN an invalid ID,"
        + " WHEN finding by ID,"
        + " THEN the service should throw NotFoundException")
    default void testFindByIdWithInvalidId() {

        // GIVEN
        final I invalidId = getInvalidId();
        given(getRepository().findById(any())).willReturn(Optional.empty());
        final FindByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.findById(invalidId))

            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found with ID : " + invalidId);
    }
}
