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

package org.sansenshimizu.sakuraboot.mapper.api.relationship.one;

import java.io.Serializable;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;

/**
 * Parent interface for defining a mapper with relationship capabilities,
 * including DTO in a relationship.
 *
 * @param  <E>  The entity type extending {@link DataPresentation}.
 * @param  <D>  The DTO type extending {@link DataPresentation}.
 * @param  <R>  The relational entity of type {@link DataPresentation}.
 * @param  <D2> The DTO of the relational entity of type extending
 *              {@link DataPresentation}.
 * @param  <I>  The ID for the relational entity of type Comparable and
 *              Serializable.
 * @author      Malcolm Rozé
 * @see         BasicMapper
 * @since       0.1.0
 */
@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BasicMapper1Relationship<E extends DataPresentation<?>,
    D extends DataPresentation<?>, R extends DataPresentation<I>,
    D2 extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicMapper<E, D> {

    /**
     * The repository use by the relational entity.
     *
     * @return a {@link SuperRepository}.
     */
    @SuppressWarnings("EmptyMethod")
    SuperRepository<R, I> getRepository();

    /**
     * The mapper use by the relational entity.
     * If the relational entity has no DTO, return {@code null}.
     * Return {@code null} by default.
     *
     * @return a {@link BasicMapper}.
     */
    @Nullable
    default BasicMapper<R, D2> getMapper() {

        return null;
    }

    /**
     * Return the type of the relational entity.
     * Use if the relational entity has no DTO.
     *
     * @return The type of the relational entity.
     */
    @Nullable
    default Class<R> getRelationalEntityType() {

        return null;
    }

    /**
     * Return the type of the relational entity DTO.
     * Use if the relational entity has no DTO.
     *
     * @return The type of the relational entity DTO.
     */
    @Nullable
    default Class<D2> getRelationalDtoType() {

        return null;
    }

    /**
     * Return the type of the relational entity ID.
     *
     * @return The type of the relational entity ID.
     */
    Class<I> getRelationalIdType();

    /**
     * Retrieve an entity by the given id. Return a proxy of the entity.
     *
     * @param  entityId The id of the entity.
     * @return          The entity retrieve by the id.
     */
    @Nullable
    default R idToEntity(@Nullable final I entityId) {

        if (entityId == null) {

            return null;
        }
        return getRepository().getReferenceById(entityId);
    }

    /**
     * This method is used to change the mapping relationship from an entity to
     * a DTO.
     * If the DTO must have the relationship represent by the ID this method
     * must return {@code false}.
     * If the Dto must have the relationship represent by the actual entity this
     * method must return {@code true}.
     * Default {@code false}.
     * It is recommended to keep the default {@code false} when using the
     * {@code cache} module because hibernate proxy can't be put in the
     * cache and will be initialized, making more SQL request and slowing down
     * the application.
     *
     * @return {@code false} if the ID represents the relationship,
     *         {@code true} otherwise.
     */
    default boolean useRelationObjectToMapToDto() {

        return false;
    }
}
