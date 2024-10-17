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

package org.sansenshimizu.sakuraboot.mapper.relationship.two;

import java.io.Serializable;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.BasicMapper2Relationship;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.one.AbstractBasicMapper1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;

/**
 * Abstract class for defining a mapper. Handled two relationships of type One
 * to One and Many to One.
 * <p>
 * To create a mapper that extends
 * {@link AbstractBasicMapper2RelationshipAnyToOne}, follow these steps:
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
 *     AbstractBasicMapper2RelationshipAnyToOne&lt;YourEntity, YourDto,
 *         YourRelationalEntity, YourRelationalDto, YourRelationalEntityIdType,
 *         YourSecondRelationalEntity, YourSecondRelationalDto,
 *         YourSecondRelationalEntityIdType&gt; {
 *
 *     private YourRelationalRepository repository;
 *
 *     private YourSecondRelationalRepository secondRepository;
 *
 *     private YourRelationalMapper mapper;
 *
 *     private YourSecondRelationalMapper secondMapper;
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
 *     public YourSecondRelationalRepository getSecondRepository() {
 *
 *         return secondRepository;
 *     }
 *
 *     &#064;Autowired
 *     protected void setSecondRepository(
 *         final YourSecondRelationalRepository secondRepository) {
 *
 *         this.secondRepository = secondRepository;
 *     }
 *
 *     &#064;Override
 *     public Class&lt;YourRelationalEntityIdType&gt; getRelationalIdType() {
 *
 *         return YourRelationalEntityIdType.class;
 *     }
 *
 *     &#064;Override
 *     public Class&lt;YourSecondRelationalEntityIdType&gt; getSecond
 *         RelationalIdType() {
 *
 *         return YourSecondRelationalEntityIdType.class;
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
 *
 *     &#064;Override
 *     public YourSecondRelationalMapper getSecondMapper() {
 *
 *         return secondMapper;
 *     }
 *
 *     &#064;Autowired
 *     protected
 *         void setSecondMapper(final YourSecondRelationalMapper secondMapper) {
 *
 *         this.secondMapper = secondMapper;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E>  The entity type extending
 *              {@link DataPresentation2RelationshipAnyToOne}.
 * @param  <D>  The DTO type extending
 *              {@link DataPresentation2RelationshipAnyToOne}.
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
 * @see         AbstractBasicMapper1RelationshipAnyToOne
 * @see         BasicMapper2Relationship
 * @since       0.1.0
 */
@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AbstractBasicMapper2RelationshipAnyToOne<
    E extends DataPresentation2RelationshipAnyToOne<?, R, R2>,
    D extends DataPresentation2RelationshipAnyToOne<?, D2, D3>,
    R extends DataPresentation<I>, D2 extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    R2 extends DataPresentation<I2>, D3 extends DataPresentation<I2>,
    I2 extends Comparable<? super I2> & Serializable>
    extends AbstractBasicMapper1RelationshipAnyToOne<E, D, R, D2, I>
    implements BasicMapper2Relationship<E, D, R, D2, I, R2, D3, I2> {

    @Override
    public abstract BasicRepository<R2, I2> getSecondRepository();

    @Override
    @Nullable
    @Mapping(
        target = "relationship",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    @Mapping(
        target = "secondRelationship",
        source = "dto",
        qualifiedBy = SecondRelationshipFromDto.class)
    public abstract E toEntity(@Nullable D dto);

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
    @Mapping(
        target = "secondRelationship",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "secondRelationshipId",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithId.class)
    public abstract D toDto(@Nullable E entity);

    /**
     * Get the second relationship of the provided DTO.
     *
     * @param  dto The provided DTO.
     * @return     The second relationship.
     */
    @Nullable
    @SecondRelationshipFromDto
    protected R2 secondRelationshipFromDto(final D dto) {

        if (dto instanceof final BasicDto2RelationshipAnyToOne<?, ?, ?, ?,
            ?> dtoRelationship) {

            final D3 entity = dto.getSecondRelationship();

            if (entity != null) {

                return secondRelationshipToEntity(entity);
            }
            final Object id = dtoRelationship.getSecondRelationshipId();

            if (getSecondRelationalIdType().isInstance(id)) {

                final I2 castId = getSecondRelationalIdType().cast(id);

                if (getSecondRepository().existsById(castId)) {

                    return idToSecondEntity(castId);
                } else {

                    throw new NotFoundException("secondRelationalEntity",
                        castId);
                }
            }
        }
        return secondRelationshipToEntity(dto.getSecondRelationship());
    }

    /**
     * Method to map a second relationship from an entity to a DTO by
     * representing the relationship with the ID.
     *
     * @param  entity The entity to map.
     * @return        The ID of the related entity or null.
     */
    @Nullable
    @SecondRelationshipFromEntityWithId
    protected I2 mapDtoSecondRelationshipWithId(final E entity) {

        final R2 secondRelationalEntity = entity.getSecondRelationship();

        if (secondRelationalEntity != null) {

            return secondRelationalEntity.getId();
        }
        return null;
    }

    /**
     * Method to map a second relationship from an entity to a DTO by
     * representing the relationship with the actual entity.
     *
     * @param  entity The entity to map.
     * @return        The related entity or null.
     */
    @Nullable
    @SecondRelationshipFromEntityWithEntity
    protected D3 mapDtoSecondRelationshipWithEntity(final E entity) {

        if (useRelationObjectToMapToDto()) {

            return secondRelationshipToDto(entity.getSecondRelationship());
        }
        return null;
    }

    /**
     * Method to map the second relationship entity to DTO.
     *
     * @param  relationalEntity the entity to map to DTO.
     * @return                  The second relational DTO.
     */
    @Nullable
    protected D3 secondRelationshipToDto(@Nullable final R2 relationalEntity) {

        final Class<D3> secondRelationalDtoType = getSecondRelationalDtoType();
        final BasicMapper<R2, D3> secondMapper = getSecondMapper();

        if (secondMapper == null && secondRelationalDtoType != null) {

            return secondRelationalDtoType.cast(relationalEntity);
        }

        if (secondMapper != null) {

            return secondMapper.toDto(relationalEntity);
        }
        return null;
    }

    /**
     * Method to map the second relationship DTO to an entity.
     *
     * @param  relationalDto the DTO to map to an entity.
     * @return               The second relational entity.
     */
    @Nullable
    protected R2 secondRelationshipToEntity(@Nullable final D3 relationalDto) {

        final Class<R2> secondRelationalEntityType
            = getSecondRelationalEntityType();
        final BasicMapper<R2, D3> secondMapper = getSecondMapper();

        if (secondMapper == null && secondRelationalEntityType != null) {

            return secondRelationalEntityType.cast(relationalDto);
        }

        if (secondMapper != null) {

            return secondMapper.toEntity(relationalDto);
        }
        return null;
    }
}
