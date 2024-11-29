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

package org.sansenshimizu.sakuraboot.util;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;

/**
 * Utility class for relationship in entity or DTO.
 *
 * @author Malcolm Rozé
 * @since  0.1.1
 */
@UtilityClass
public class RelationshipUtils {

    /**
     * Message to show when GlobalSpecification is null when working with DTO.
     */
    private final String GLOBAL_SPECIFICATION_NULL_MESSAGE
        = "GlobalSpecification can't be null when working with DTO";

    /**
     * Util method to check if a field is a relationship in an entity.
     *
     * @param  field The field to check.
     * @return       True if the field is a relationship, false otherwise.
     */
    public boolean isRelationship(final AnnotatedElement field) {

        return isAnyToOneRelationship(field) || isAnyToManyRelationship(field);
    }

    /**
     * Util method to check if a field is an any to one relationship in an
     * entity.
     *
     * @param  field The field to check.
     * @return       True if the field is a relationship, false otherwise.
     */
    public boolean isAnyToOneRelationship(final AnnotatedElement field) {

        return field.isAnnotationPresent(OneToOne.class)
            || field.isAnnotationPresent(ManyToOne.class);
    }

    /**
     * Util method to check if a field is an any to many relationship in an
     * entity.
     *
     * @param  field The field to check.
     * @return       True if the field is a relationship, false otherwise.
     */
    public boolean isAnyToManyRelationship(final AnnotatedElement field) {

        return field.isAnnotationPresent(OneToMany.class)
            || field.isAnnotationPresent(ManyToMany.class);
    }

    /**
     * Util method to get the entity field from a DTO field.
     *
     * @param  field         The field of the DTO.
     * @param  dtoClass      The DTO class.
     * @param  entityPackage The entity package.
     * @param  dtoPackage    The DTO package.
     * @return               The field of the entity.
     */
    @Nullable
    public AnnotatedElement getEntityFieldFromDto(
        final Member field, final Class<?> dtoClass, final String entityPackage,
        final String dtoPackage) {

        final Class<?> entityClass;

        try {

            entityClass = Class.forName(dtoClass.getName()
                .replace(dtoPackage, entityPackage)
                .replace("Dto", ""));
        } catch (final ClassNotFoundException e) {

            throw new RuntimeException(
                "The DTO class must follow the naming convention."
                    + " (EntityName+Dto)",
                e);
        }

        final AnnotatedElement entityField;

        try {

            entityField = entityClass.getDeclaredField(field.getName());
        } catch (final NoSuchFieldException e) {

            return null;
        }
        return entityField;
    }

    /**
     * Util method to check if a field is a relationship in an entity. Exclude
     * the field with mappedBy attribute in the mapping annotation.
     *
     * @param  field The field to check.
     * @return       True if the field is a relationship, false otherwise.
     */
    public boolean isRelationshipExcludeMappedBy(final AnnotatedElement field) {

        return isAnyToOneRelationshipExcludeMappedBy(field)
            || isAnyToManyRelationshipExcludeMappedBy(field);
    }

    private boolean isAnyToOneRelationshipExcludeMappedBy(
        final AnnotatedElement field) {

        return (field.isAnnotationPresent(OneToOne.class)
            && "".equals(field.getAnnotation(OneToOne.class).mappedBy()))
            || field.isAnnotationPresent(ManyToOne.class);
    }

    private boolean isAnyToManyRelationshipExcludeMappedBy(
        final AnnotatedElement field) {

        return (field.isAnnotationPresent(OneToMany.class)
            && "".equals(field.getAnnotation(OneToMany.class).mappedBy()))
            || (field.isAnnotationPresent(ManyToMany.class)
                && "".equals(field.getAnnotation(ManyToMany.class).mappedBy()));
    }

    /**
     * Util method to check if a field is not a relation with mappedBy attribute
     * in the mapping annotation. Help to not include this field in other
     * methods.
     *
     * @param  field The field to check.
     * @return       True if the field is not a relation with mappedBy
     *               attribute,
     *               false otherwise.
     */
    public boolean isNotRelationWithMappedBy(final AnnotatedElement field) {

        return isNotOneToOneWithMappedBy(field)
            && isNotOneToManyWithMappedBy(field)
            && isNotManyToManyWithMappedBy(field);
    }

    private boolean isNotOneToOneWithMappedBy(final AnnotatedElement field) {

        return !(field.isAnnotationPresent(OneToOne.class)
            && !"".equals(field.getAnnotation(OneToOne.class).mappedBy()));
    }

    private boolean isNotOneToManyWithMappedBy(final AnnotatedElement field) {

        return !(field.isAnnotationPresent(OneToMany.class)
            && !"".equals(field.getAnnotation(OneToMany.class).mappedBy()));
    }

    private boolean isNotManyToManyWithMappedBy(final AnnotatedElement field) {

        return !(field.isAnnotationPresent(ManyToMany.class)
            && !"".equals(field.getAnnotation(ManyToMany.class).mappedBy()));
    }

    /**
     * Util method to get the class of all the relationship fields in an entity.
     * Exclude the field with mappedBy attribute in the mapping annotation.
     *
     * @param  clazz The entity class.
     * @return       The class of all the relationship fields.
     */
    public List<Class<?>> getRelationClass(final Class<?> clazz) {

        return getRelationClass(clazz, new ArrayList<>());
    }

    private List<Class<?>> getRelationClass(
        final Class<?> clazz, final List<Class<?>> relationClass) {

        final List<Field> fieldsAnyToOne
            = FieldUtils.getFieldsListWithAnnotation(clazz, OneToOne.class)
                .stream()
                .filter(field -> ""
                    .equals(field.getAnnotation(OneToOne.class).mappedBy()))
                .collect(Collectors.toList());
        fieldsAnyToOne.addAll(
            FieldUtils.getFieldsListWithAnnotation(clazz, ManyToOne.class));

        final List<Field> fieldsAnyToMany
            = FieldUtils.getFieldsListWithAnnotation(clazz, OneToMany.class)
                .stream()
                .filter(field -> ""
                    .equals(field.getAnnotation(OneToMany.class).mappedBy()))
                .collect(Collectors.toList());
        fieldsAnyToMany.addAll(
            FieldUtils.getFieldsListWithAnnotation(clazz, ManyToMany.class)
                .stream()
                .filter(field -> ""
                    .equals(field.getAnnotation(ManyToMany.class).mappedBy()))
                .toList());

        final List<Class<?>> newRelationClass = fieldsAnyToOne.stream()
            .map(Field::getType)
            .collect(Collectors.toList());
        newRelationClass.addAll(fieldsAnyToMany.stream()
            .map(
                field -> (Class<?>) ((ParameterizedType) field.getGenericType())
                    .getActualTypeArguments()[0])
            .toList());

        for (final Class<?> newClass: newRelationClass) {

            relationClass.add(newClass);
            getRelationClass(newClass, relationClass);
        }
        return relationClass;
    }

    /**
     * Util method to get the class of all the relationship fields in an entity
     * with mappedBy attribute in the mapping annotation.
     *
     * @param  clazz The entity class.
     * @return       The class of all the relationship fields.
     */
    public List<Class<?>> getMappedByRelationClass(final Class<?> clazz) {

        return getMappedByRelationClass(clazz, new ArrayList<>());
    }

    private List<Class<?>> getMappedByRelationClass(
        final Class<?> clazz, final List<Class<?>> relationClass) {

        final List<Field> fieldsAnyToOne
            = FieldUtils.getFieldsListWithAnnotation(clazz, OneToOne.class)
                .stream()
                .filter(field -> !""
                    .equals(field.getAnnotation(OneToOne.class).mappedBy()))
                .toList();

        final List<Field> fieldsAnyToMany
            = FieldUtils.getFieldsListWithAnnotation(clazz, OneToMany.class)
                .stream()
                .filter(field -> !""
                    .equals(field.getAnnotation(OneToMany.class).mappedBy()))
                .collect(Collectors.toList());
        fieldsAnyToMany.addAll(
            FieldUtils.getFieldsListWithAnnotation(clazz, ManyToMany.class)
                .stream()
                .filter(field -> !""
                    .equals(field.getAnnotation(ManyToMany.class).mappedBy()))
                .toList());

        final List<Class<?>> newRelationClass = fieldsAnyToOne.stream()
            .map(Field::getType)
            .collect(Collectors.toList());
        newRelationClass.addAll(fieldsAnyToMany.stream()
            .map(
                field -> (Class<?>) ((ParameterizedType) field.getGenericType())
                    .getActualTypeArguments()[0])
            .toList());

        for (final Class<?> newClass: newRelationClass) {

            relationClass.add(newClass);
            getMappedByRelationClass(newClass, relationClass);
        }
        return relationClass;
    }

    /**
     * Util method to get the class of a relationship field.
     * The entity class will be returned if the field is for a DTO.
     * If the field is a collection, the class of the element in the collection
     * is returned.
     *
     * @param  field               The relationship field.
     * @param  globalSpecification The {@link GlobalSpecification} help to
     *                             get the entity class from the DTO class.
     * @return                     The class of the relationship field.
     */
    public Class<?> getRelationalType(
        final Field field, final GlobalSpecification globalSpecification) {

        final Class<?> relationalType;

        /*@formatter:off*/
        if (field.getGenericType()
            instanceof final ParameterizedType parameterizedType
            /*@formatter:on*/
            && parameterizedType
                .getActualTypeArguments()[0] instanceof final Class<?> clazz) {

            relationalType = clazz;
        } else {

            relationalType = field.getType();
        }

        if (relationalType.getSimpleName().contains("Dto")) {

            try {

                return Class.forName(relationalType.getName()
                    .replace(globalSpecification.dtoPackage(),
                        globalSpecification.entityPackage())
                    .replace("Dto", ""));
            } catch (final ClassNotFoundException e) {

                throw new RuntimeException("The DTO class must follow the "
                    + "naming convention. (EntityName+Dto)", e);
            }
        }
        return relationalType;
    }

    private boolean isAnyToOneRelationshipFromDto(
        final Member field, final Class<?> dtoClass, final String entityPackage,
        final String dtoPackage) {

        final AnnotatedElement entityField
            = getEntityFieldFromDto(field, dtoClass, entityPackage, dtoPackage);

        if (entityField == null) {

            return false;
        }
        return isAnyToOneRelationship(entityField);
    }

    private boolean isAnyToManyRelationshipFromDto(
        final Member field, final Class<?> dtoClass, final String entityPackage,
        final String dtoPackage) {

        final AnnotatedElement entityField
            = getEntityFieldFromDto(field, dtoClass, entityPackage, dtoPackage);

        if (entityField == null) {

            return false;
        }
        return isAnyToManyRelationship(entityField);
    }

    /**
     * Util method to perform an action to all the relationship fields in an
     * entity or a DTO object.
     * Include all the objects inside a collection.
     *
     * @param data                The entity or DTO object.
     * @param consumer            The action to perform.
     * @param globalSpecification The {@link GlobalSpecification} help to get
     *                            the entity and dto package.
     */
    public void doWithRelationFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        @Nullable final GlobalSpecification globalSpecification) {

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            doWithAnyToOneFields(data, consumer,
                RelationshipUtils::isAnyToOneRelationship);
            doWithAnyToManyFields(data, consumer,
                RelationshipUtils::isAnyToManyRelationship);
            return;
        }

        if (globalSpecification == null) {

            throw new IllegalArgumentException(
                GLOBAL_SPECIFICATION_NULL_MESSAGE);
        }
        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        doWithAnyToOneFields(data, consumer,
            field -> isAnyToOneRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
        doWithAnyToManyFields(data, consumer,
            field -> isAnyToManyRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
    }

    /**
     * Util method to perform an action to all the relationship fields in an
     * entity or a DTO object.
     * Don't include all the objects inside a collection but perform an
     * action on the collection.
     *
     * @param data                The entity or DTO object.
     * @param consumer            The action to perform.
     * @param consumerCollection  The action to perform to a collection.
     * @param globalSpecification The {@link GlobalSpecification} help to get
     *                            the entity and dto package.
     */
    public void doWithRelationFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        final BiConsumer<Field, Collection<?>> consumerCollection,
        @Nullable final GlobalSpecification globalSpecification) {

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            doWithAnyToOneFields(data, consumer,
                RelationshipUtils::isAnyToOneRelationship);
            doWithAnyToManyFieldsCollection(data, consumerCollection,
                RelationshipUtils::isAnyToManyRelationship);
            return;
        }

        if (globalSpecification == null) {

            throw new IllegalArgumentException(
                GLOBAL_SPECIFICATION_NULL_MESSAGE);
        }

        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        doWithAnyToOneFields(data, consumer,
            field -> isAnyToOneRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
        doWithAnyToManyFieldsCollection(data, consumerCollection,
            field -> isAnyToManyRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
    }

    private void doWithAnyToOneFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            field.setAccessible(true);
            consumer.accept(field, field.get(data));
        }, fieldFilter);
    }

    private void doWithAnyToManyFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            field.setAccessible(true);

            if (field.get(data) instanceof final Collection<?> collection) {

                collection.forEach(element -> consumer.accept(field, element));
            }
        }, fieldFilter);
    }

    private void doWithAnyToManyFieldsCollection(
        final Object data,
        final BiConsumer<Field, Collection<?>> consumerCollection,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            field.setAccessible(true);

            if (field.get(data) instanceof final Collection<?> collection
                && !collection.isEmpty()) {

                consumerCollection.accept(field, collection);
            }
        }, fieldFilter);
    }

    /**
     * Util method to perform an action to all the ID of the relationship fields
     * in an entity or a DTO object. Include all the IDs inside a collection.
     *
     * @param data                The entity or DTO object.
     * @param consumer            The action to perform.
     * @param globalSpecification The {@link GlobalSpecification} help to get
     *                            the entity and dto package.
     */
    public void doWithIdRelationFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        @Nullable final GlobalSpecification globalSpecification) {

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            doWithIdAnyToOneFields(data, consumer,
                RelationshipUtils::isAnyToOneRelationship);
            doWithIdAnyToManyFields(data, consumer,
                RelationshipUtils::isAnyToManyRelationship);
            return;
        }

        if (globalSpecification == null) {

            throw new IllegalArgumentException(
                GLOBAL_SPECIFICATION_NULL_MESSAGE);
        }

        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        doWithIdAnyToOneFields(data, consumer,
            field -> isAnyToOneRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
        doWithIdAnyToManyFields(data, consumer,
            field -> isAnyToManyRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
    }

    /**
     * Util method to perform an action to all the ID of the relationship fields
     * in an entity or a DTO object. Don't include all the IDs inside a
     * collection but perform an action on the collection.
     *
     * @param data                The entity or DTO object.
     * @param consumer            The action to perform.
     * @param consumerCollection  The action to perform to a collection.
     * @param globalSpecification The {@link GlobalSpecification} help to get
     *                            the entity and dto package.
     */
    public void doWithIdRelationFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        final BiConsumer<Field, Collection<?>> consumerCollection,
        @Nullable final GlobalSpecification globalSpecification) {

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            doWithIdAnyToOneFields(data, consumer,
                RelationshipUtils::isAnyToOneRelationship);
            doWithIdAnyToManyFieldsCollection(data, consumerCollection,
                RelationshipUtils::isAnyToManyRelationship);
            return;
        }

        if (globalSpecification == null) {

            throw new IllegalArgumentException(
                GLOBAL_SPECIFICATION_NULL_MESSAGE);
        }

        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        doWithIdAnyToOneFields(data, consumer,
            field -> isAnyToOneRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
        doWithIdAnyToManyFieldsCollection(data, consumerCollection,
            field -> isAnyToManyRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
    }

    private void doWithIdAnyToOneFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            Field idField;

            try {

                idField
                    = data.getClass().getDeclaredField(field.getName() + "Id");
            } catch (final NoSuchFieldException e) {

                idField = field;
            }
            idField.setAccessible(true);

            final Object idFieldObject = idField.get(data);

            if (idFieldObject instanceof final DataPresentation<
                ?> relationship) {

                consumer.accept(idField, relationship.getId());
            } else {

                consumer.accept(idField, idFieldObject);
            }
        }, fieldFilter);
    }

    private void doWithIdAnyToManyFields(
        final Object data, final BiConsumer<Field, Object> consumer,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            Field idField;

            try {

                idField
                    = data.getClass().getDeclaredField(field.getName() + "Id");
            } catch (final NoSuchFieldException e) {

                idField = field;
            }
            idField.setAccessible(true);

            if (idField.get(data) instanceof final Collection<?> collection
                && !collection.isEmpty()) {

                collection.stream().map((final Object relationship) -> {

                    if (relationship instanceof final DataPresentation<
                        ?> dataPresentation) {

                        return dataPresentation.getId();
                    }
                    return relationship;
                })
                    .forEach(
                        idFieldObject -> consumer.accept(field, idFieldObject));
            }
        }, fieldFilter);
    }

    private void doWithIdAnyToManyFieldsCollection(
        final Object data,
        final BiConsumer<Field, Collection<?>> consumerCollection,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            Field idField;

            try {

                idField
                    = data.getClass().getDeclaredField(field.getName() + "Id");
            } catch (final NoSuchFieldException e) {

                idField = field;
            }
            idField.setAccessible(true);

            if (idField.get(data) instanceof final Collection<?> collection
                && !collection.isEmpty()) {

                consumerCollection.accept(field,
                    collection.stream().map((final Object relationship) -> {

                        if (relationship instanceof final DataPresentation<
                            ?> dataPresentation) {

                            return dataPresentation.getId();
                        }
                        return relationship;
                    }).toList());
            }
        }, fieldFilter);
    }

    /**
     * Util method to update the value to all the relationship fields in an
     * entity or a DTO object.
     * Include all the objects inside a collection.
     *
     * @param data                The entity or DTO object.
     * @param function            The function to update the value.
     * @param globalSpecification The {@link GlobalSpecification} help to get
     *                            the entity and dto package.
     */
    public void updateRelationFields(
        final Object data, final BiFunction<Field, Object, Object> function,
        @Nullable final GlobalSpecification globalSpecification) {

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            updateAnyToOneFields(data, function,
                RelationshipUtils::isAnyToOneRelationship);
            updateAnyToManyFields(data, function, HashSet::new,
                RelationshipUtils::isAnyToManyRelationship);
            return;
        }

        if (globalSpecification == null) {

            throw new IllegalArgumentException(
                GLOBAL_SPECIFICATION_NULL_MESSAGE);
        }

        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        updateAnyToOneFields(data, function,
            field -> isAnyToOneRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
        updateAnyToManyFields(data, function, HashSet::new,
            field -> isAnyToManyRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
    }

    /**
     * Util method to update the value to all the relationship fields in an
     * entity or a DTO object.
     * Don't include all the objects inside a collection but update the value of
     * the collection.
     *
     * @param data                The entity or DTO object.
     * @param function            The function to update the value.
     * @param functionCollection  The function to update the collection.
     * @param globalSpecification The {@link GlobalSpecification} help to get
     *                            the entity and dto package.
     */
    public void updateRelationFields(
        final Object data, final BiFunction<Field, Object, Object> function,
        final BiFunction<Field, Collection<?>,
            Collection<?>> functionCollection,
        @Nullable final GlobalSpecification globalSpecification) {

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            updateAnyToOneFields(data, function,
                RelationshipUtils::isAnyToOneRelationship);
            updateAnyToManyFieldsCollection(data, functionCollection,
                RelationshipUtils::isAnyToManyRelationship);
            return;
        }

        if (globalSpecification == null) {

            throw new IllegalArgumentException(
                GLOBAL_SPECIFICATION_NULL_MESSAGE);
        }

        final String entityPackage = globalSpecification.entityPackage();
        final String dtoPackage = globalSpecification.dtoPackage();

        updateAnyToOneFields(data, function,
            field -> isAnyToOneRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
        updateAnyToManyFieldsCollection(data, functionCollection,
            field -> isAnyToManyRelationshipFromDto(field, data.getClass(),
                entityPackage, dtoPackage));
    }

    private void updateAnyToOneFields(
        final Object data, final BiFunction<Field, Object, Object> function,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            field.setAccessible(true);
            field.set(data, function.apply(field, field.get(data)));
        }, fieldFilter);
    }

    private void updateAnyToManyFields(
        final Object data, final BiFunction<Field, Object, Object> function,
        final Supplier<Collection<Object>> collectionSupplier,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            field.setAccessible(true);

            if (field.get(data) instanceof final Collection<?> collection) {

                final Collection<?> newCollection = collection.stream()
                    .map(element -> function.apply(field, element))
                    .collect(Collectors.toCollection(collectionSupplier));
                field.set(data, newCollection);
            }
        }, fieldFilter);
    }

    private void updateAnyToManyFieldsCollection(
        final Object data,
        final BiFunction<Field, Collection<?>, Collection<?>> function,
        final ReflectionUtils.FieldFilter fieldFilter) {

        ReflectionUtils.doWithFields(data.getClass(), (final Field field) -> {

            field.setAccessible(true);

            if (field.get(data) instanceof final Collection<?> collection
                && !collection.isEmpty()) {

                field.set(data, function.apply(field, collection));
            }
        }, fieldFilter);
    }
}
