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

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.UpdateByIdService;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link UpdateByIdService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link UpdateByIdServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link UpdateByIdServiceTest} class:
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
 * @see        UpdateByIdService
 * @see        SuperServiceTest
 * @since      0.1.0
 */
public interface UpdateByIdServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link UpdateByIdService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link UpdateByIdService}.
     */
    UpdateByIdService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID and entity,"
        + " WHEN updating by ID,"
        + " THEN the service should update and return the entity")
    default void testUpdateById() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        given(getRepository().existsById(any())).willReturn(true);
        given(getRepository().save(any())).willReturn(entityWithId);

        // WHEN
        final DataPresentation<I> updatedEntity
            = getService().updateById(entityWithId, getValidId());

        // THEN
        assertThat(updatedEntity).isEqualTo(entityWithId);
    }

    @Test
    @DisplayName("GIVEN a null ID and entity with a valid ID,"
        + " WHEN updating by ID,"
        + " THEN the service should update and return the entity")
    default void testUpdateByIdWithParameterNullId() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        given(getRepository().existsById(any())).willReturn(true);
        given(getRepository().save(any())).willReturn(entityWithId);

        // WHEN
        final DataPresentation<I> updatedEntity
            = getService().updateById(entityWithId, null);

        // THEN
        assertThat(updatedEntity).isEqualTo(entityWithId);
    }

    @Test
    @DisplayName("GIVEN a DTO with null ID,"
        + " WHEN updating by ID,"
        + " THEN the service should throw BadRequestException")
    default void testUpdateByIdWithNullId() {

        // GIVEN
        final I validId = getValidId();
        final E entityWithoutId = getUtil().getEntityWithoutId();
        final UpdateByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.updateById(entityWithoutId, validId))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't update an entity when they don't have an ID : ");
    }

    @Test
    @DisplayName("GIVEN a different ID in the DTO,"
        + " WHEN updating by ID,"
        + " THEN the service should throw BadRequestException")
    default void testUpdateByIdWithDifferentId() {

        // GIVEN
        final I invalidId = getInvalidId();
        final E entityWithId = getUtil().getEntity();
        final UpdateByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.updateById(entityWithId, invalidId))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't update an entity when different ID are provided : ");
    }

    @Test
    @DisplayName("GIVEN an ID that doesn't exist,"
        + " WHEN updating by ID,"
        + " THEN the service should throw NotFoundException")
    default void testUpdateByIdWithIdNotExist() {

        // GIVEN
        final I invalidId = getInvalidId();
        given(getRepository().existsById(any())).willReturn(false);
        final UpdateByIdService<E, I> serviceTmp = getService();
        final E entityWithId = getUtil().getDifferentEntity();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.updateById(entityWithId, invalidId))
            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found with ID : " + invalidId);
    }

    @Test
    @DisplayName("GIVEN an entity with a different type,"
        + " WHEN updating by ID,"
        + " THEN the service should throw BadRequestException")
    default void testUpdateByIdWithDifferentType() {

        // GIVEN
        final I validId = getValidId();
        final DataPresentation<I> entityWithDifferentType
            = new DataPresentationForTest<>(validId);
        final UpdateByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.updateById(entityWithDifferentType, validId))

            // THEN
            .isExactlyInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't update when the provided object is not an entity : ");
    }
}
