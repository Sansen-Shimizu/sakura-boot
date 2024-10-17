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

package org.sansenshimizu.sakuraboot.mapper.relationship.one;

import java.io.Serializable;

import org.hibernate.proxy.HibernateProxy;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.BasicMapper1Relationship;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;

/**
 * Abstract class for defining a mapper. Handled one relationship of type One to
 * One and Many to One.
 * <p>
 * To create a mapper that extends
 * {@link AbstractBasicMapper1RelationshipAnyToOne}, follow these steps:
 * </p>
 * <p>
 * Create a new class with MapStruct:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Mapper(config = BasicMapper.class)
 * public abstract class YourMapper
 *     extends
 *     AbstractBasicMapper1RelationshipAnyToOne&lt;YourEntity, YourDto,
 *         YourRelationalEntity, YourRelationalDto,
 *         YourRelationalEntityIdType&gt; {
 *
 *     private YourRelationalRepository repository;
 *
 *     private YourRelationalMapper mapper;
 *
 *     &#064;Override
 *     public YourRelationalRepository getRepository() {
 *
 *         return repository;
 *     }
 *
 *     &#064;Autowired
 *     protected void setRepository(final YourRelationalRepository repository) {
 *
 *         this.repository = repository;
 *     }
 *
 *     &#064;Override
 *     public Class&lt;YourRelationalEntityIdType&gt; getRelationalIdType() {
 *
 *         return YourRelationalEntityIdType.class;
 *     }
 *
 *     &#064;Override
 *     public YourRelationalMapper getMapper() {
 *
 *         return mapper;
 *     }
 *
 *     &#064;Autowired
 *     protected void setMapper(final YourRelationalMapper mapper) {
 *
 *         this.mapper = mapper;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E>  The entity type extending
 *              {@link DataPresentation1RelationshipAnyToOne}.
 * @param  <D>  The DTO type extending
 *              {@link DataPresentation1RelationshipAnyToOne}.
 * @param  <R>  The relational entity of type {@link DataPresentation}.
 * @param  <D2> The DTO of the relational entity of type extending
 *              {@link DataPresentation}.
 * @param  <I>  The ID for the relational entity of type Comparable and
 *              Serializable.
 * @author      Malcolm Rozé
 * @see         BasicMapper1Relationship
 * @since       0.1.0
 */
@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AbstractBasicMapper1RelationshipAnyToOne<
    E extends DataPresentation1RelationshipAnyToOne<?, R>,
    D extends DataPresentation1RelationshipAnyToOne<?, D2>,
    R extends DataPresentation<I>, D2 extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    implements BasicMapper1Relationship<E, D, R, D2, I> {

    @Override
    public abstract BasicRepository<R, I> getRepository();

    /**
     * Converts a DTO object to its corresponding entity object. This method
     * handles mapping relationship.
     *
     * @param  dto The {@link DataPresentation1RelationshipAnyToOne} object to
     *             convert.
     * @return     The {@link DataPresentation1RelationshipAnyToOne} object
     *             representing the DTO.
     */
    @Override
    @Nullable
    @Mapping(
        target = "relationship",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    public abstract E toEntity(@Nullable D dto);

    /**
     * Converts an entity object to its corresponding DTO object. This method
     * handles mapping relationship.
     *
     * @param  entity The {@link DataPresentation1RelationshipAnyToOne} object
     *                to convert.
     * @return        The {@link DataPresentation1RelationshipAnyToOne} object
     *                representing the entity.
     */
    @Override
    @Nullable
    @Mapping(
        target = "relationship",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "relationshipId",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithId.class)
    public abstract D toDto(@Nullable E entity);

    /**
     * Get the relationship of the provided DTO.
     *
     * @param  dto The provided DTO.
     * @return     The relationship.
     */
    @Nullable
    @RelationshipFromDto
    protected R relationshipFromDto(final D dto) {

        if (dto instanceof final BasicDto1RelationshipAnyToOne<?, ?,
            ?> dtoRelationship) {

            final D2 entity = dto.getRelationship();

            if (entity != null) {

                return relationshipToEntity(entity);
            }
            final Object id = dtoRelationship.getRelationshipId();

            if (getRelationalIdType().isInstance(id)) {

                final I castId = getRelationalIdType().cast(id);

                if (getRepository().existsById(castId)) {

                    return idToEntity(castId);
                } else {

                    throw new NotFoundException("relationalEntity", castId);
                }
            }
        }
        return relationshipToEntity(dto.getRelationship());
    }

    /**
     * Method to map a relationship from an entity to a DTO by representing the
     * relationship with the ID.
     *
     * @param  entity The entity to map.
     * @return        The ID of the related entity or null.
     */
    @Nullable
    @RelationshipFromEntityWithId
    protected I mapDtoRelationshipWithId(final E entity) {

        final R relationalEntity = entity.getRelationship();

        if (relationalEntity != null) {

            return relationalEntity.getId();
        }
        return null;
    }

    /**
     * Method to map a relationship from an entity to a DTO by representing the
     * relationship with the actual entity.
     *
     * @param  entity The entity to map.
     * @return        The related entity or null.
     */
    @Nullable
    @RelationshipFromEntityWithEntity
    protected D2 mapDtoRelationshipWithEntity(final E entity) {

        if (useRelationObjectToMapToDto()) {

            return relationshipToDto(entity.getRelationship());
        }
        return null;
    }

    /**
     * Method to map a relationship entity to DTO.
     *
     * @param  relationalEntity the entity to map to DTO.
     * @return                  The relational DTO.
     */
    @Nullable
    protected D2 relationshipToDto(@Nullable final R relationalEntity) {

        final Class<D2> relationalDtoType = getRelationalDtoType();
        final BasicMapper<R, D2> mapper = getMapper();

        if (mapper == null && relationalDtoType != null) {

            final Object relation;

            if (relationalEntity instanceof final HibernateProxy proxy) {

                relation
                    = proxy.getHibernateLazyInitializer().getImplementation();
            } else {

                relation = relationalEntity;
            }
            return relationalDtoType.cast(relation);
        }

        if (mapper != null) {

            return mapper.toDto(relationalEntity);
        }
        return null;
    }

    /**
     * Method to map a relationship DTO to an entity.
     *
     * @param  relationalDto the DTO to map to an entity.
     * @return               The relational entity.
     */
    @Nullable
    protected R relationshipToEntity(@Nullable final D2 relationalDto) {

        final Class<R> relationalEntityType = getRelationalEntityType();
        final BasicMapper<R, D2> mapper = getMapper();

        if (mapper == null && relationalEntityType != null) {

            return relationalEntityType.cast(relationalDto);
        }

        if (mapper != null) {

            return mapper.toEntity(relationalDto);
        }
        return null;
    }
}
