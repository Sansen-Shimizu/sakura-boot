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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.lang.Nullable;
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.PatchByIdService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link PatchByIdService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link PatchByIdServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link PatchByIdServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements PatchByIdServiceTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        PatchByIdService
 * @see        SuperServiceTest
 * @since      0.1.0
 */
public interface PatchByIdServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperServiceTest<E, I> {

    /**
     * Get the {@link PatchByIdService} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link PatchByIdService}.
     */
    PatchByIdService<E, I> getService();

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

    private void mockFindByIdForPatch(@Nullable final E entityWithId) {

        if (getRepository() instanceof final FetchRelationshipRepository<?,
            ?> repository) {

            @SuppressWarnings("unchecked")
            final FetchRelationshipRepository<E, I> castRepository
                = (FetchRelationshipRepository<E, I>) repository;
            given(castRepository.findByIdEagerRelationship(any(), any()))
                .willReturn(Optional.ofNullable(entityWithId));
        } else {

            given(getRepository().findById(any()))
                .willReturn(Optional.ofNullable(entityWithId));
        }
    }

    @Test
    @DisplayName("GIVEN a valid ID and partial entity,"
        + " WHEN patching by ID,"
        + " THEN the service should patch and return the patched entity")
    default void testPatchById() throws JsonMappingException {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final E partialEntity = getUtil().getPartialEntity();
        final Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("id", getValidId());
        entityMap.put("nullField", null);

        mockFindByIdForPatch(entityWithId);
        given(getObjectMapper().convertValue(any(),
            ArgumentMatchers.<TypeReference<Map<String, Object>>>any()))
            .willReturn(entityMap);
        given(getObjectMapper().updateValue(any(), any()))
            .willReturn(entityWithId);
        given(getRepository().save(any())).willReturn(entityWithId);

        // WHEN
        final DataPresentation<I> patchedEntity
            = getService().patchById(partialEntity, getValidId());

        // THEN
        assertThat(patchedEntity).usingRecursiveComparison()
            .isEqualTo(entityWithId);
    }

    @Test
    @DisplayName("GIVEN a partial entity with null ID,"
        + " WHEN patching by ID,"
        + " THEN the service should throw BadRequestException")
    default void testPatchByIdWithNullId() {

        // GIVEN
        final I validId = getValidId();
        final E partialEntity
            = SerializationUtils.clone(getUtil().getPartialEntity());
        ReflectionTestUtils.setField(partialEntity, "id", null);
        final PatchByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.patchById(partialEntity, validId))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't partially update an entity when they "
                    + "don't have an ID : ");
    }

    @Test
    @DisplayName("GIVEN a different ID in the partial entity,"
        + " WHEN patching by ID,"
        + " THEN the service should throw BadRequestException")
    default void testPatchByIdWithDifferentId() {

        // GIVEN
        final I invalidId = getInvalidId();
        final E partialEntity = getUtil().getPartialEntity();
        final PatchByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.patchById(partialEntity, invalidId))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith(
                "Can't partially update an entity when different "
                    + "ID are provided : ");
    }

    @Test
    @DisplayName("GIVEN an ID that doesn't exist,"
        + " WHEN patching by ID,"
        + " THEN the service should throw NotFoundException")
    default void testPatchByIdWithIdNotExist() {

        // GIVEN
        final I invalidId = getInvalidId();
        mockFindByIdForPatch(null);
        final E partialEntity
            = SerializationUtils.clone(getUtil().getPartialEntity());
        ReflectionTestUtils.setField(partialEntity, "id", invalidId);
        final PatchByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(() -> serviceTmp.patchById(partialEntity, invalidId))
            // THEN
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("not found with ID : " + invalidId);
    }

    @Test
    @DisplayName("GIVEN a valid ID and an invalid partial entity,"
        + " WHEN patching by ID,"
        + " THEN the service should throw BadRequestException")
    default void testPatchByIdWithMapperException()
        throws JsonMappingException {

        // GIVEN
        final I validId = getValidId();
        final E entityWithId = getUtil().getEntity();
        final E invalidPartialEntity = getUtil().getPartialEntity();

        mockFindByIdForPatch(entityWithId);
        given(getObjectMapper().convertValue(any(),
            ArgumentMatchers.<TypeReference<Map<String, Object>>>any()))
            .willReturn(new HashMap<>());
        given(getObjectMapper().updateValue(any(), any()))
            .willThrow(JsonMappingException.class);
        final PatchByIdService<E, I> serviceTmp = getService();

        // WHEN
        assertThatThrownBy(
            () -> serviceTmp.patchById(invalidPartialEntity, validId))

            // THEN
            .isInstanceOf(BadRequestException.class)
            .hasMessageStartingWith("Cannot partial update : ");
    }
}
