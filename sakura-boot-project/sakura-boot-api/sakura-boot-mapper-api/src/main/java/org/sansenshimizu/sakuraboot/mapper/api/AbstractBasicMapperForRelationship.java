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

package org.sansenshimizu.sakuraboot.mapper.api;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

/**
 * Parent interface for defining a mapper that handles relationships.
 * <p>
 * To create a mapper that implements
 * {@link AbstractBasicMapperForRelationship}, follow these steps:
 * </p>
 * <p>
 * Create a new class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourMapper
 *     extends AbstractBasicMapperForRelationship&lt;YourEntity, YourDto&gt; {
 *
 *     &#064;Override
 *     public YourEntity toEntity(final YourDto dto) {
 *
 *         // Use your prefer mapper to map or manually map using the entity
 *         // constructor.
 *         return mapper.toEntity(dto);
 *     }
 *
 *     &#064;Override
 *     public YourDto toDto(final YourEntity entity) {
 *
 *         // Use your prefer mapper to map or manually map using the dto
 *         // constructor.
 *         return mapper.toDto(entity);
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * Or with MapStruct:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Mapper(config = BasicMapper.class)
 * public abstract class AbstractYourMapper
 *     extends AbstractBasicMapperForRelationship&lt;YourEntity, YourDto&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <D> The DTO type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @since      0.1.1
 */
public abstract class AbstractBasicMapperForRelationship<
    E extends DataPresentation<?>, D extends DataPresentation<?>>
    implements BasicMapper<E, D> {

    /**
     * The application context.
     */
    private ApplicationContext context;

    /**
     * The {@link GlobalSpecification}.
     */
    private GlobalSpecification globalSpecification;

    /**
     * Inject the application context.
     *
     * @param context The application context.
     */
    @Autowired
    public void setContext(final ApplicationContext context) {

        this.context = context;
    }

    /**
     * Inject the {@link GlobalSpecification}.
     *
     * @param globalSpecification The {@link GlobalSpecification}.
     */
    @Autowired
    public void setGlobalSpecification(
        final GlobalSpecification globalSpecification) {

        this.globalSpecification = globalSpecification;
    }

    /**
     * This method is executed after the mapping from an entity to a DTO.
     * And map all the relationships from the entity to the DTO.
     *
     * @param entity The entity to map.
     * @param dto    The mapped DTO.
     */
    @AfterMapping
    protected void afterMappingEntityToDto(
        final E entity, @MappingTarget final D dto) {

        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        ReflectionUtils.doWithFields(dto.getClass(),
            field -> afterMappingEntityToDto(entity, dto, field),
            (final Field field) -> {

                final AnnotatedElement entityField
                    = RelationshipUtils.getEntityFieldFromDto(field,
                        dto.getClass(), entityPackage, dtoPackage);

                if (entityField == null) {

                    return false;
                }
                return RelationshipUtils.isRelationship(entityField);
            });
    }

    /**
     * This method is executed after the mapping from an entity to a DTO.
     * And map the relationship from the entity to the DTO.
     *
     * @param entity The entity to map.
     * @param dto    The mapped DTO.
     * @param field  The field of the relationship to map.
     */
    protected void afterMappingEntityToDto(
        final E entity, final D dto, final Field field)
        throws IllegalAccessException {

        final Object unProxyEntity = Hibernate.unproxy(entity);
        final Field idField;
        final Field sourceField;

        try {

            idField = dto.getClass().getDeclaredField(field.getName() + "Id");
            sourceField
                = unProxyEntity.getClass().getDeclaredField(field.getName());
        } catch (final NoSuchFieldException e) {

            return;
        }
        field.setAccessible(true);
        idField.setAccessible(true);
        sourceField.setAccessible(true);
        final Object sourceFieldObject = sourceField.get(unProxyEntity);

        if (useRelationObjectToMapToDto()) {

            final BasicMapper<DataPresentation<?>,
                DataPresentation<?>> relationalMapper
                    = getRelationalMapper(field);

            if (relationalMapper == null) {

                field.set(dto, sourceFieldObject);
                mapEntityToId(dto, sourceFieldObject, idField);
                return;
            }

            mapEntityToDtoAndId(dto, field, sourceField, sourceFieldObject,
                relationalMapper, idField);
            return;
        }

        mapEntityToId(dto, sourceFieldObject, idField);
        field.set(dto, null);
    }

    private static <D extends DataPresentation<?>> void mapEntityToId(
        final D dto, final Object sourceFieldObject, final Field idField)
        throws IllegalAccessException {

        if (sourceFieldObject instanceof final Collection<?> collection) {

            idField.set(dto,
                collection.stream()
                    .filter(DataPresentation.class::isInstance)
                    .map(DataPresentation.class::cast)
                    .map(relationship -> relationship.getId())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet()));
            return;
        }

        if (sourceFieldObject instanceof final DataPresentation<
            ?> relationship) {

            idField.set(dto, relationship.getId());
        }
    }

    private static <D extends DataPresentation<?>> void mapEntityToDtoAndId(
        final D dto, final Field field, final Field sourceField,
        final Object sourceFieldObject,
        final BasicMapper<DataPresentation<?>,
            DataPresentation<?>> relationalMapper,
        final Field idField)
        throws IllegalAccessException {

        if (sourceFieldObject instanceof final Collection<?> collection) {

            final Set<DataPresentation<?>> relationshipSet = new HashSet<>();
            final Set<Object> relationshipIdSet = new HashSet<>();
            collection.stream().map((final Object relationship) -> {

                if (relationship instanceof final DataPresentation<
                    ?> dataPresentation) {

                    return dataPresentation;
                } else {

                    // noinspection ReturnOfNull
                    return null;
                }
            })
                .filter(Objects::nonNull)
                .forEach((final DataPresentation<?> relationship) -> {

                    if (RelationshipUtils
                        .isNotRelationWithMappedBy(sourceField)) {

                        final DataPresentation<?> relationalDto
                            = relationalMapper.toDto(relationship);
                        relationshipSet.add(relationalDto);
                    }
                    final Object id = relationship.getId();

                    if (id != null) {

                        relationshipIdSet.add(id);
                    }
                });
            field.set(dto, relationshipSet);
            idField.set(dto, relationshipIdSet);
            return;
        }

        if (sourceFieldObject instanceof final DataPresentation<
            ?> relationship) {

            if (RelationshipUtils.isNotRelationWithMappedBy(sourceField)) {

                final DataPresentation<?> relationalDto
                    = relationalMapper.toDto(relationship);
                field.set(dto, relationalDto);
            }
            idField.set(dto, relationship.getId());
        }
    }

    /**
     * This method is executed after the mapping from a DTO to an entity.
     * And map all the relationships from the DTO to the entity.
     *
     * @param dto    The DTO to map.
     * @param entity The mapped entity.
     */
    @AfterMapping
    protected void afterMappingDtoToEntity(
        final D dto, @MappingTarget final E entity) {

        final Repositories repositories = new Repositories(context);

        ReflectionUtils.doWithFields(entity.getClass(),
            field -> afterMappingDtoToEntity(dto, entity, field, repositories),
            RelationshipUtils::isRelationship);
    }

    /**
     * This method is executed after the mapping from a DTO to an entity.
     * And map the relationship from the DTO to the entity.
     *
     * @param dto          The DTO to map.
     * @param entity       The mapped entity.
     * @param field        The field of the relationship to map.
     * @param repositories The list of spring repositories.
     */
    protected void afterMappingDtoToEntity(
        final D dto, final E entity, final Field field,
        final Repositories repositories)
        throws IllegalAccessException {

        final Field idField;
        final Field sourceField;

        try {

            idField = dto.getClass().getDeclaredField(field.getName() + "Id");
            sourceField = dto.getClass().getDeclaredField(field.getName());
        } catch (final NoSuchFieldException e) {

            return;
        }
        field.setAccessible(true);
        idField.setAccessible(true);
        sourceField.setAccessible(true);
        final Object sourceFieldObject = sourceField.get(dto);
        final Object idFieldObject = idField.get(dto);

        if (sourceFieldObject == null
            || !RelationshipUtils.isNotRelationWithMappedBy(field)) {

            final Class<?> relationalType = RelationshipUtils
                .getRelationalType(sourceField, globalSpecification);
            repositories.getRepositoryFor(relationalType)
                .filter(SuperRepository.class::isInstance)
                .map(SuperRepository.class::cast)
                .ifPresent(repository -> mapEntityFromId(entity, field,
                    repository, idFieldObject));
            return;
        }

        final BasicMapper<DataPresentation<?>,
            DataPresentation<?>> relationalMapper = getRelationalMapper(field);

        if (relationalMapper == null) {

            field.set(entity, sourceFieldObject);
            return;
        }
        mapDtoToEntity(entity, field, sourceFieldObject, relationalMapper);
    }

    private static <E extends DataPresentation<?>> void mapEntityFromId(
        final E entity, final Field field,
        @SuppressWarnings("rawtypes") final SuperRepository repository,
        @Nullable final Object idFieldObject) {

        if (idFieldObject instanceof final Collection<?> collection) {

            final Set<Object> entitySet = new HashSet<>();

            for (final Object id: collection) {

                @SuppressWarnings("unchecked")
                final boolean exists = repository.existsById(id);

                if (exists) {

                    @SuppressWarnings("unchecked")
                    final Object reference = repository.getReferenceById(id);
                    entitySet.add(reference);
                }
            }

            try {

                field.set(entity, entitySet);
            } catch (final IllegalAccessException e) {

                throw new RuntimeException(e);
            }
            return;
        }

        if (idFieldObject != null) {

            @SuppressWarnings("unchecked")
            final boolean exists = repository.existsById(idFieldObject);

            if (exists) {

                @SuppressWarnings("unchecked")
                final Object reference
                    = repository.getReferenceById(idFieldObject);

                try {

                    field.set(entity, reference);
                } catch (final IllegalAccessException e) {

                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static <E extends DataPresentation<?>> void mapDtoToEntity(
        final E entity, final Field field, final Object sourceFieldObject,
        final BasicMapper<DataPresentation<?>,
            DataPresentation<?>> relationalMapper)
        throws IllegalAccessException {

        if (sourceFieldObject instanceof final Collection<?> collection) {

            final Set<DataPresentation<?>> relationshipSet = new HashSet<>();
            collection.stream().map((final Object relationship) -> {

                if (relationship instanceof final DataPresentation<
                    ?> dataPresentation) {

                    return dataPresentation;
                } else {

                    // noinspection ReturnOfNull
                    return null;
                }
            })
                .filter(Objects::nonNull)
                .forEach((final DataPresentation<?> relationship) -> {

                    final DataPresentation<?> relationalEntity
                        = relationalMapper.toEntity(relationship);
                    relationshipSet.add(relationalEntity);
                });
            field.set(entity, relationshipSet);
            return;
        }

        if (sourceFieldObject instanceof final DataPresentation<
            ?> relationship) {

            final DataPresentation<?> relationalEntity
                = relationalMapper.toEntity(relationship);
            field.set(entity, relationalEntity);
        }
    }

    private @Nullable
        BasicMapper<DataPresentation<?>, DataPresentation<?>>
        getRelationalMapper(final Field field) {

        final Class<?> relationalType
            = RelationshipUtils.getRelationalType(field, globalSpecification);
        final String mapperName = relationalType.getName()
            .replace(globalSpecification.entityPackage(),
                globalSpecification.mapperPackage())
            + "Mapper";

        BasicMapper<DataPresentation<?>, DataPresentation<?>> mapper;

        try {

            @SuppressWarnings("unchecked")
            final BasicMapper<DataPresentation<?>,
                DataPresentation<?>> castMapper = (BasicMapper<
                    DataPresentation<?>, DataPresentation<?>>) context
                        .getBean(Class.forName(mapperName));
            mapper = castMapper;
        } catch (final NoSuchBeanDefinitionException
            | BeanNotOfRequiredTypeException | ClassNotFoundException e) {

            try {

                @SuppressWarnings("unchecked")
                final BasicMapper<DataPresentation<?>,
                    DataPresentation<?>> castMapper = (BasicMapper<
                        DataPresentation<?>, DataPresentation<?>>) context
                            .getBean(Class.forName(mapperName.replace(
                                globalSpecification.mapperPackage() + ".",
                                globalSpecification.mapperPackage()
                                    + ".Abstract")));
                mapper = castMapper;
            } catch (final NoSuchBeanDefinitionException
                | BeanNotOfRequiredTypeException | ClassNotFoundException ex) {

                mapper = null;
            }
        }
        return mapper;
    }
}
