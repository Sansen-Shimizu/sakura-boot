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
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.BasicMapper2Relationship;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.one.AbstractBasicMapper1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToMany;

/**
 * Abstract class for defining a mapper. Handled two relationships of type One
 * to Many and Many to Many.
 * <p>
 * To create a mapper that extends
 * {@link AbstractBasicMapper2RelationshipAnyToMany}, follow these steps:
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
 *     AbstractBasicMapper2RelationshipAnyToMany&lt;YourEntity, YourDto,
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
 *              {@link DataPresentation2RelationshipAnyToMany}.
 * @param  <D>  The DTO type extending
 *              {@link DataPresentation2RelationshipAnyToMany}.
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
 * @see         AbstractBasicMapper1RelationshipAnyToMany
 * @see         BasicMapper2Relationship
 * @since       0.1.0
 */
@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AbstractBasicMapper2RelationshipAnyToMany<
    E extends DataPresentation2RelationshipAnyToMany<?, R, R2>,
    D extends DataPresentation2RelationshipAnyToMany<?, D2, D3>,
    R extends DataPresentation<I>, D2 extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    R2 extends DataPresentation<I2>, D3 extends DataPresentation<I2>,
    I2 extends Comparable<? super I2> & Serializable>
    extends AbstractBasicMapper1RelationshipAnyToMany<E, D, R, D2, I>
    implements BasicMapper2Relationship<E, D, R, D2, I, R2, D3, I2> {

    @Override
    public abstract BasicRepository<R2, I2> getSecondRepository();

    @Override
    @Nullable
    @Mapping(
        target = "relationships",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    @Mapping(
        target = "secondRelationships",
        source = "dto",
        qualifiedBy = SecondRelationshipFromDto.class)
    public abstract E toEntity(@Nullable D dto);

    @Override
    @Nullable
    @Mapping(
        target = "relationships",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "relationshipsId",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithId.class)
    @Mapping(
        target = "secondRelationships",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "secondRelationshipsId",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithId.class)
    public abstract D toDto(@Nullable E entity);

    @Nullable
    private
        Set<R2> secondRelationshipFromDto(@Nullable final Set<D3> entities) {

        if (entities != null) {

            return secondRelationshipToEntities(entities);
        } else {

            return null;
        }
    }

    /**
     * Get the second relationship of the provided DTO.
     *
     * @param  dto The provided DTO.
     * @return     The second relationship.
     */
    @SecondRelationshipFromDto
    @Nullable
    protected Set<R2> secondRelationshipFromDto(final D dto) {

        if (dto instanceof final BasicDto2RelationshipAnyToMany<?, ?, ?, ?,
            ?> dtoRelationship) {

            final Set<D3> entities = dto.getSecondRelationships();

            if ((entities != null && !entities.isEmpty())
                || useRelationObjectToMapToDto()) {

                return secondRelationshipFromDto(entities);
            }
            final Set<?> ids = dtoRelationship.getSecondRelationshipsId();

            if (ids != null
                && ids.stream()
                    .allMatch(getSecondRelationalIdType()::isInstance)) {

                final Set<I2> castIds = ids.stream()
                    .map(getSecondRelationalIdType()::cast)
                    .collect(Collectors.toUnmodifiableSet());

                if (getSecondRepository().existsByIds(castIds,
                    (long) ids.size())) {

                    final Set<R2> mapEntities = new HashSet<>();
                    castIds
                        .forEach(id -> mapEntities.add(idToSecondEntity(id)));
                    return Collections.unmodifiableSet(mapEntities);
                } else {

                    throw new NotFoundException(
                        "one of secondRelationalEntities");
                }
            }
        }
        return secondRelationshipFromDto(dto.getSecondRelationships());
    }

    /**
     * Method to map a second relationship from an entity to a DTO by
     * representing the relationship with the ID.
     *
     * @param  entity The entity to map.
     * @return        The ID of the related entity or null.
     */
    @SecondRelationshipFromEntityWithId
    protected Set<I2> mapDtoSecondRelationshipWithId(final E entity) {

        final Set<R2> secondRelationalEntities
            = entity.getSecondRelationships();

        if (secondRelationalEntities != null
            && !secondRelationalEntities.isEmpty()) {

            return secondRelationalEntities.stream()
                .filter(relationship -> relationship.getId() != null)
                .map(DataPresentation::getId)
                .collect(Collectors.toUnmodifiableSet());
        }
        return Collections.emptySet();
    }

    /**
     * Method to map a second relationship from an entity to a DTO by
     * representing the relationship with the actual entity.
     *
     * @param  entity The entity to map.
     * @return        The related entity or null.
     */
    @SecondRelationshipFromEntityWithEntity
    protected Set<D3> mapDtoSecondRelationshipWithEntity(final E entity) {

        final Set<R2> secondRelationalEntities
            = entity.getSecondRelationships();

        if (secondRelationalEntities != null
            && useRelationObjectToMapToDto()
            && !secondRelationalEntities.isEmpty()) {

            return secondRelationshipToDtos(secondRelationalEntities);
        }
        return Collections.emptySet();
    }

    /**
     * Method to map the second relationship entities to DTOs.
     *
     * @param  relationalEntities the entities to map to DTOs.
     * @return                    The second relational DTOs.
     */
    protected
        Set<D3> secondRelationshipToDtos(final Set<R2> relationalEntities) {

        return relationalEntities.stream().map((final R2 relationalEntity) -> {

            if (getSecondMapper() == null
                && getSecondRelationalDtoType() != null) {

                return getSecondRelationalDtoType().cast(relationalEntity);
            }
            return getSecondMapper().toDto(relationalEntity);
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Method to map the second relationship DTOs to entities.
     *
     * @param  relationalDtos the DTOs to map to entities.
     * @return                The second relational entities.
     */
    protected
        Set<R2> secondRelationshipToEntities(final Set<D3> relationalDtos) {

        return relationalDtos.stream().map((final D3 relationalDto) -> {

            if (getSecondMapper() == null
                && getSecondRelationalEntityType() != null) {

                return getSecondRelationalEntityType().cast(relationalDto);
            }
            return getSecondMapper().toEntity(relationalDto);
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }
}
