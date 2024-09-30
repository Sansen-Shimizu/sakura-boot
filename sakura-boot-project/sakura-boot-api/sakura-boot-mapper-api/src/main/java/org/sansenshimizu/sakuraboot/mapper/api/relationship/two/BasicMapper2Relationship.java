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

package org.sansenshimizu.sakuraboot.mapper.api.relationship.two;

import java.io.Serializable;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.BasicMapper1Relationship;

/**
 * Parent interface for defining a mapper with two relationship capabilities,
 * including DTO in a relationship.
 *
 * @param  <E>  The entity type extending {@link DataPresentation}.
 * @param  <D>  The DTO type extending {@link DataPresentation}.
 * @param  <R>  The relational entity of type {@link DataPresentation}.
 * @param  <D2> The DTO of the relational entity of type extending
 *              {@link DataPresentation}.
 * @param  <I>  The ID for the relational entity of type Comparable and
 *              Serializable.
 * @param  <R2> The second relational entity of type {@link DataPresentation}.
 * @param  <D3> The DTO for the second relational entity of type extending
 *              {@link DataPresentation}.
 * @param  <I2> The ID for the second relational entity of type Comparable and
 *              Serializable.
 * @author      Malcolm Rozé
 * @see         BasicMapper1Relationship
 * @since       0.1.0
 */
@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BasicMapper2Relationship<E extends DataPresentation<?>,
    D extends DataPresentation<?>, R extends DataPresentation<I>,
    D2 extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    R2 extends DataPresentation<I2>, D3 extends DataPresentation<I2>,
    I2 extends Comparable<? super I2> & Serializable>
    extends BasicMapper1Relationship<E, D, R, D2, I> {

    /**
     * The repository use by the second relational entity.
     *
     * @return a {@link SuperRepository}.
     */
    @SuppressWarnings("EmptyMethod")
    SuperRepository<R2, I2> getSecondRepository();

    /**
     * The mapper use by the second relational entity.
     * If the second relational entity has no DTO, return
     * {@code null}.
     * Return {@code null} by default.
     *
     * @return a {@link BasicMapper}.
     */
    @Nullable
    default BasicMapper<R2, D3> getSecondMapper() {

        return null;
    }

    /**
     * Return the type of the second relational entity.
     * Use if the second relational entity has no DTO.
     *
     * @return The type of the second relational entity.
     */
    @Nullable
    default Class<R2> getSecondRelationalEntityType() {

        return null;
    }

    /**
     * Return the type of the second relational entity DTO.
     * Use if the second relational entity has no DTO.
     *
     * @return The type of the second relational entity DTO.
     */
    @Nullable
    default Class<D3> getSecondRelationalDtoType() {

        return null;
    }

    /**
     * Return the type of the second relational entity ID.
     *
     * @return The type of the second relational entity ID.
     */
    Class<I2> getSecondRelationalIdType();

    /**
     * Retrieve an entity by the given id. Return a proxy of the entity.
     *
     * @param  entityId The id of the entity.
     * @return          The entity retrieve by the id.
     */
    @Nullable
    default R2 idToSecondEntity(@Nullable final I2 entityId) {

        if (entityId == null) {

            return null;
        }
        return getSecondRepository().getReferenceById(entityId);
    }
}
