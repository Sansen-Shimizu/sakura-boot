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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.PatchAllService;
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
 * tests for testing {@link PatchAllService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link PatchAllServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link PatchAllServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements PatchAllServiceTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        PatchAllService
 * @see        SuperServiceTest
 * @since      0.1.2
 */
public interface PatchAllServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link PatchAllService} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link PatchAllService}.
     */
    PatchAllService<E, I> getService();

    /**
     * Get the {@link BasicRepository} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicRepository}.
     */
    @SuppressWarnings("EmptyMethod")
    BasicRepository<E, I> getRepository();

    /**
     * Get the {@link ObjectMapper} for test. Need to be {@link Mock}.
     *
     * @return An {@link ObjectMapper}.
     */
    ObjectMapper getObjectMapper();

    private void mockFindAllForPatch(final List<E> entitiesWithId) {

        if (getRepository() instanceof final FetchRelationshipRepository<?,
            ?> repository) {

            @SuppressWarnings("unchecked")
            final FetchRelationshipRepository<E, I> castRepository
                = (FetchRelationshipRepository<E, I>) repository;
            given(castRepository.findAllEagerRelationship(any(), any()))
                .willReturn(entitiesWithId);
        } else {

            given(getRepository().findAllById(any()))
                .willReturn(entitiesWithId);
        }
    }

    @Test
    @DisplayName("GIVEN valid partial entities,"
        + " WHEN patching all,"
        + " THEN the service should patch all and return the patched entities")
    default void testPatchAll() {

        // GIVEN
        final List<E> entitiesWithId
            = List.of(getUtil().getEntity(), getUtil().getDifferentEntity());
        final DataPresentation<I> partialEntity = getUtil().getPartialEntity();
        final DataPresentation<I> secondPartialEntity
            = getUtil().getPartialEntity();
        ReflectionTestUtils.setField(secondPartialEntity, "id", getInvalidId());
        final List<DataPresentation<I>> partialEntities
            = List.of(partialEntity, secondPartialEntity);
        final Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("id", getValidId());
        entityMap.put("nullField", null);

        mockFindAllForPatch(entitiesWithId);
        given(getObjectMapper().convertValue(any(),
            ArgumentMatchers.<TypeReference<Map<String, Object>>>any()))
            .willReturn(entityMap);

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
        final List<DataPresentation<I>> patchedEntities
            = getService().patchAll(partialEntities);

        // THEN
        assertThat(patchedEntities).containsAll(entitiesWithId);
    }

    @Test
    @DisplayName("GIVEN a partial entity with null ID,"
        + " WHEN patching all,"
        + " THEN the service should throw BadRequestException")
    default void testPatchAllWithNullId() {

        // GIVEN
        final E partialEntity
            = SerializationUtils.clone(getUtil().getPartialEntity());
        ReflectionTestUtils.setField(partialEntity, "id", null);
        final PatchAllService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.patchAll(List.of(partialEntity)))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't partially update an entity when they "
                    + "don't have an ID : ");
    }

    @Test
    @DisplayName("GIVEN an ID that doesn't exist,"
        + " WHEN patching all,"
        + " THEN the service should throw NotFoundException")
    default void testPatchAllWithIdNotExist() {

        // GIVEN
        final I invalidId = getInvalidId();
        mockFindAllForPatch(List.of());
        final E partialEntity
            = SerializationUtils.clone(getUtil().getPartialEntity());
        ReflectionTestUtils.setField(partialEntity, "id", invalidId);
        final PatchAllService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.patchAll(List.of(partialEntity)))
            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("GIVEN a valid ID and an invalid partial entity,"
        + " WHEN patching all,"
        + " THEN the service should throw BadRequestException")
    default void testPatchAllWithMapperException() throws JsonMappingException {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final E invalidPartialEntity = getUtil().getPartialEntity();
        ReflectionTestUtils.setField(invalidPartialEntity, "id", getValidId());

        mockFindAllForPatch(List.of(entityWithId));
        given(getObjectMapper().convertValue(any(),
            ArgumentMatchers.<TypeReference<Map<String, Object>>>any()))
            .willReturn(new HashMap<>());
        given(getObjectMapper().updateValue(any(), any()))
            .willThrow(JsonMappingException.class);
        final PatchAllService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.patchAll(List.of(invalidPartialEntity)))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith("Cannot partial update : ");
    }
}
