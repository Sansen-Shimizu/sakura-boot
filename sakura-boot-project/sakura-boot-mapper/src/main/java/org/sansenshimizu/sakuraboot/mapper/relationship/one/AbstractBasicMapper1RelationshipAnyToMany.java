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
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.proxy.HibernateProxy;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.BasicMapper1Relationship;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;

/**
 * Abstract class for defining a mapper. Handled one relationship of type One to
 * Many and Many to Many.
 * <p>
 * To create a mapper that extends
 * {@link AbstractBasicMapper1RelationshipAnyToMany}, follow these steps:
 * </p>
 * <p>
 * Create a new class with MapStruct:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Mapper(config = BasicMapper.class)
 * public interface YourMapper
 *     extends
 *     AbstractBasicMapper1RelationshipAnyToMany&lt;YourEntity, YourDto,
 *         YourRelationalEntity, YourRelationalDto,
 *         YourRelationalEntityIdType&gt; {
 *
 *     private YourRelationalRepository repository;
 *
 *     private YourRelationalMapper mapper;
 *
 *     &#064;Override
 *     public YourRelationalRepository getRepository() {
 *         return repository;
 *     }
 *
 *     &#064;Autowired
 *     protected void setRepository(final YourRelationalRepository repository) {
 *         this.repository = repository;
 *     }
 *
 *     &#064;Override
 *     public Class&lt;YourRelationalEntityIdType&gt; getRelationalIdType(){
 *         return YourRelationalEntityIdType.class;
 *     }
 *
 *     &#064;Override
 *     public YourRelationalMapper getMapper() {
 *         return mapper;
 *     }
 *
 *     &#064;Autowired
 *     protected void setMapper(final YourRelationalMapper mapper) {
 *         this.mapper = mapper;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E>  The entity type extending
 *              {@link DataPresentation1RelationshipAnyToMany}.
 * @param  <D>  The DTO type extending
 *              {@link DataPresentation1RelationshipAnyToMany}.
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
public abstract class AbstractBasicMapper1RelationshipAnyToMany<
    E extends DataPresentation1RelationshipAnyToMany<?, R>,
    D extends DataPresentation1RelationshipAnyToMany<?, D2>,
    R extends DataPresentation<I>, D2 extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    implements BasicMapper1Relationship<E, D, R, D2, I> {

    @Override
    public abstract BasicRepository<R, I> getRepository();

    /**
     * Converts a DTO object to its corresponding entity object. This method
     * handles mapping relationship.
     *
     * @param  dto The {@link DataPresentation1RelationshipAnyToMany} object to
     *             convert.
     * @return     The {@link DataPresentation1RelationshipAnyToMany} object
     *             representing the DTO.
     */
    @Override
    @Nullable
    @Mapping(
        target = "relationships",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    public abstract E toEntity(@Nullable D dto);

    /**
     * Converts an entity object to its corresponding DTO object. This method
     * handles mapping relationship.
     *
     * @param  entity The {@link DataPresentation1RelationshipAnyToMany} object
     *                to convert.
     * @return        The {@link DataPresentation1RelationshipAnyToMany} object
     *                representing the entity.
     */
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
    public abstract D toDto(@Nullable E entity);

    @Nullable
    private Set<R> relationshipFromDto(@Nullable final Set<D2> entities) {

        if (entities != null) {

            return relationshipToEntity(entities);
        } else {

            return null;
        }
    }

    /**
     * Get the relationship of the provided DTO.
     *
     * @param  dto The provided DTO.
     * @return     The relationship.
     */
    @RelationshipFromDto
    @Nullable
    protected Set<R> relationshipFromDto(final D dto) {

        if (dto instanceof final BasicDto1RelationshipAnyToMany<?, ?,
            ?> dtoRelationship) {

            final Set<D2> entities = dto.getRelationships();

            if ((entities != null && !entities.isEmpty())) {

                return relationshipFromDto(entities);
            }

            final Set<?> ids = dtoRelationship.getRelationshipsId();

            if (ids != null
                && ids.stream().allMatch(getRelationalIdType()::isInstance)) {

                final Set<I> castIds = ids.stream()
                    .map(getRelationalIdType()::cast)
                    .collect(Collectors.toUnmodifiableSet());

                if (getRepository().existsByIds(castIds, (long) ids.size())) {

                    final Set<R> mapEntities = new HashSet<>();
                    castIds.forEach(id -> mapEntities.add(idToEntity(id)));
                    return Collections.unmodifiableSet(mapEntities);
                } else {

                    throw new NotFoundException("one of relationalEntities");
                }
            }
        }
        return relationshipFromDto(dto.getRelationships());
    }

    /**
     * Method to map a relationship from an entity to a DTO by representing the
     * relationship with the ID.
     *
     * @param  entity The entity to map.
     * @return        The ID of the related entity or null.
     */
    @RelationshipFromEntityWithId
    protected Set<I> mapDtoRelationshipWithId(final E entity) {

        final Set<R> relationalEntities = entity.getRelationships();

        if (relationalEntities != null && !relationalEntities.isEmpty()) {

            return relationalEntities.stream()
                .filter(relationship -> relationship.getId() != null)
                .map(DataPresentation::getId)
                .collect(Collectors.toUnmodifiableSet());
        }
        return Collections.emptySet();
    }

    /**
     * Method to map a relationship from an entity to a DTO by representing the
     * relationship with the
     * actual entity.
     *
     * @param  entity The entity to map.
     * @return        The related entity or null.
     */
    @RelationshipFromEntityWithEntity
    protected Set<D2> mapDtoRelationshipWithEntity(final E entity) {

        final Set<R> relationalEntities = entity.getRelationships();

        if (relationalEntities != null
            && useRelationObjectToMapToDto()
            && !relationalEntities.isEmpty()) {

            return relationshipToDto(relationalEntities);
        }
        return Collections.emptySet();
    }

    /**
     * Method to map relationship entities to DTOs.
     *
     * @param  relationalEntities the entities to map to DTOs.
     * @return                    The relational DTOs.
     */
    protected Set<D2> relationshipToDto(final Set<R> relationalEntities) {

        return relationalEntities.stream().map((final R relationalEntity) -> {

            if (getMapper() == null && getRelationalDtoType() != null) {

                final Object relation;

                if (relationalEntity instanceof final HibernateProxy proxy) {

                    relation = proxy.getHibernateLazyInitializer()
                        .getImplementation();
                } else {

                    relation = relationalEntity;
                }
                return getRelationalDtoType().cast(relation);
            }
            return getMapper().toDto(relationalEntity);
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Method to map relationship DTOs to entities.
     *
     * @param  relationalDtos the DTOs to map to entities.
     * @return                The relational entities.
     */
    protected Set<R> relationshipToEntity(final Set<D2> relationalDtos) {

        return relationalDtos.stream().map((final D2 relationalDto) -> {

            if (getMapper() == null && getRelationalEntityType() != null) {

                return getRelationalEntityType().cast(relationalDto);
            }
            return getMapper().toEntity(relationalDto);
        }).filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
    }
}
