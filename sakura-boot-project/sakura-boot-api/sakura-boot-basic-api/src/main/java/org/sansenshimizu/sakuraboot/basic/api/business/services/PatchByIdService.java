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

package org.sansenshimizu.sakuraboot.basic.api.business.services;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;
import org.sansenshimizu.sakuraboot.cache.api.annotations.PutCache;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;

/**
 * The service interface for patchById operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a service interface that inherits from {@link PatchByIdService},
 * follow these steps:
 * </p>
 * <p>
 * Create a new service interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourService
 *     extends PatchByIdService&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a service class that implements {@link PatchByIdService}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new service class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Service
 * public class YourService
 *     implements PatchByIdService&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends BasicService.
 *     private final YourRepository repository;
 *
 *     private final ObjectMapper objectMapper;
 *
 *     public YourService(
 *         final YourRepository repository, final ObjectMapper objectMapper) {
 *
 *         this.repository = repository;
 *         this.objectMapper = objectMapper;
 *     }
 *
 *     public YourRepository getRepository() {
 *
 *         return this.repository;
 *     }
 *
 *     public Class&lt;YourEntity&gt; getEntityClass() {
 *
 *         return YourEntity.class;
 *     }
 *
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
 * @see        SuperService
 * @see        PatchByIdService#getObjectMapper()
 * @see        PatchByIdService#patchById(DataPresentation, Comparable)
 * @since      0.1.0
 */
public interface PatchByIdService<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperService<E, I> {

    @Override
    BasicRepository<E, I> getRepository();

    /**
     * Give an {@link ObjectMapper} use to partial update entity.
     *
     * @return An ObjectMapper.
     */
    ObjectMapper getObjectMapper();

    /**
     * Partially updates an existing {@link DataPresentation} with the provided
     * data from a {@link DataPresentation} in the underlying data storage,
     * using an {@link ObjectMapper}.
     *
     * @param  data                The partial {@link DataPresentation}
     *                             containing the fields to update.
     * @param  id                  The ID of the {@link DataPresentation} to be
     *                             updated.
     * @return                     The updated {@link DataPresentation}.
     * @throws BadRequestException If the provided {@link DataPresentation} does
     *                             not have an ID or the specified ID doesn't
     *                             match the {@link DataPresentation}'s ID.
     * @throws NotFoundException   If no {@link DataPresentation} with the
     *                             specified ID exists in the data storage.
     */
    @Transactional
    @PutCache(key = "#id", refreshEntityCache = true)
    @Mapping
    @Logging
    default DataPresentation<I> patchById(
        final DataPresentation<I> data, @Nullable final I id) {

        final I dataId = getId(data, id);

        E entity;

        if (getRepository() instanceof final FetchRelationshipRepository<?,
            ?> repository) {

            @SuppressWarnings("unchecked")
            final FetchRelationshipRepository<E, I> fetchRepository
                = (FetchRelationshipRepository<E, I>) repository;
            entity = fetchRepository
                .findByIdEagerRelationship(dataId, getEntityClass())
                .orElseThrow(
                    () -> new NotFoundException(getEntityClass(), dataId));
        } else {

            entity = getRepository().findById(dataId)
                .orElseThrow(
                    () -> new NotFoundException(getEntityClass(), dataId));
        }

        final Map<String, Object> entityMap
            = getObjectMapper().convertValue(data, new TypeReference<>() {});

        final Map<String, Object> objectMapNonNullValues = entityMap.entrySet()
            .stream()
            .filter(entityEntry -> Objects.nonNull(entityEntry.getValue()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        // MUST remove the null value manually because
        // setSerializationInclusion(Include.NON_NULL)
        // don't work with the @NotNull field.

        try {

            entity
                = getObjectMapper().updateValue(entity, objectMapNonNullValues);
        } catch (final JsonMappingException e) {

            throw new BadRequestException(
                "Cannot partial update : " + getEntityClass().getSimpleName(),
                e);
        }

        return getRepository().save(entity);
    }

    private I getId(final DataPresentation<I> data, @Nullable final I id) {

        final I dataId = data.getId();

        if (dataId == null) {

            throw new BadRequestException(
                "Can't partially update an entity when they don't have an ID : "
                    + getEntityClass().getSimpleName());
        }

        if (id != null && !dataId.equals(id)) {

            throw new BadRequestException(
                "Can't partially update an entity when different ID are"
                    + " provided : "
                    + getEntityClass().getSimpleName());
        }
        return dataId;
    }
}
