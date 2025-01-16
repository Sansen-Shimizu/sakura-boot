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
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.UpdateAllService;
import org.sansenshimizu.sakuraboot.bulk.api.persistence.BulkRepository;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link UpdateAllService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link UpdateAllServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link UpdateAllServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements UpdateAllServiceTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        UpdateAllService
 * @see        SuperServiceTest
 * @since      0.1.2
 */
public interface UpdateAllServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link UpdateAllService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link UpdateAllService}.
     */
    UpdateAllService<E, I> getService();

    /**
     * Get the {@link BasicRepository} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicRepository}.
     */
    @SuppressWarnings("EmptyMethod")
    BasicRepository<E, I> getRepository();

    @Test
    @DisplayName("GIVEN valid entities,"
        + " WHEN updating all,"
        + " THEN the service should update all and return the entities")
    default void testUpdateAll() {

        // GIVEN
        final List<E> entitiesWithId
            = List.of(getUtil().getEntity(), getUtil().getDifferentEntity());
        @SuppressWarnings("unchecked")
        final List<DataPresentation<I>> castEntitiesWithId
            = (List<DataPresentation<I>>) entitiesWithId;
        given(getRepository().existsByIds(any(), any())).willReturn(true);

        if (getRepository() instanceof final BulkRepository<?,
            ?> bulkRepository) {

            @SuppressWarnings("unchecked")
            final BulkRepository<E, I> castBulkRepository
                = (BulkRepository<E, I>) bulkRepository;
            given(castBulkRepository.bulkUpdate(any()))
                .willReturn(entitiesWithId);
        } else {

            given(getRepository().saveAll(any())).willReturn(entitiesWithId);
        }

        // WHEN
        final List<DataPresentation<I>> updatedEntities
            = getService().updateAll(castEntitiesWithId);

        // THEN
        assertThat(updatedEntities).containsAll(entitiesWithId);
    }

    @Test
    @DisplayName("GIVEN a DTO with null ID,"
        + " WHEN updating all,"
        + " THEN the service should throw BadRequestException")
    default void testUpdateAllWithNullId() {

        // GIVEN
        final E entityWithoutId = getUtil().getEntityWithoutId();
        final UpdateAllService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.updateAll(List.of(entityWithoutId)))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't update an entity when they don't have an ID : ");
    }

    @Test
    @DisplayName("GIVEN an ID that doesn't exist,"
        + " WHEN updating All,"
        + " THEN the service should throw NotFoundException")
    default void testUpdateAllWithIdNotExist() {

        // GIVEN
        given(getRepository().existsByIds(any(), any())).willReturn(false);
        final UpdateAllService<E, I> serviceTmp = getService();
        final E entityWithId = getUtil().getDifferentEntity();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.updateAll(List.of(entityWithId)))
            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("GIVEN an entity with a different type,"
        + " WHEN updating all,"
        + " THEN the service should throw BadRequestException")
    default void testUpdateAllWithDifferentType() {

        // GIVEN
        final I validId = getValidId();
        final DataPresentation<I> entityWithDifferentType
            = new DataPresentationForTest<>(validId);
        final UpdateAllService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.updateAll(List.of(entityWithDifferentType)))

            // THEN
            .isExactlyInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't update when the provided object is not an entity : ");
    }
}
