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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.DeleteByIdService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link DeleteByIdService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link DeleteByIdServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteByIdServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements DeleteByIdServiceTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        DeleteByIdService
 * @see        SuperServiceTest
 * @since      0.1.0
 */
public interface DeleteByIdServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link DeleteByIdService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DeleteByIdService}.
     */
    DeleteByIdService<E, I> getService();

    /**
     * Get the {@link BasicRepository} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicRepository}.
     */
    @SuppressWarnings("EmptyMethod")
    BasicRepository<E, I> getRepository();

    @Test
    @DisplayName("GIVEN a valid ID,"
        + " WHEN deleting by ID,"
        + " THEN the service should delete the entity")
    default void testDeleteById() {

        // GIVEN
        given(getRepository().existsById(any())).willReturn(true);

        // WHEN
        getService().deleteById(getValidId());

        // THEN
        verify(getRepository(), times(1)).deleteById(getValidId());
    }

    @Test
    @DisplayName("GIVEN an invalid ID,"
        + " WHEN deleting by ID,"
        + " THEN the service should throw NotFoundException")
    default void testDeleteByIdWithInvalidId() {

        // GIVEN
        final I invalidId = getInvalidId();
        given(getRepository().existsById(any())).willReturn(false);
        final DeleteByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.deleteById(invalidId))

            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found with ID : " + invalidId);
    }
}
