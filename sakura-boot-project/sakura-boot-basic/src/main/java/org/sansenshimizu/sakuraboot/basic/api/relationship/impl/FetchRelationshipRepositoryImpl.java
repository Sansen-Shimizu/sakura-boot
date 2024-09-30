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

package org.sansenshimizu.sakuraboot.basic.api.relationship.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NoResultException;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;

/**
 * The implementation of {@link FetchRelationshipRepository}.
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        FetchRelationshipRepository
 * @since      0.1.0
 */
@Transactional(readOnly = true)
public class FetchRelationshipRepositoryImpl<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    implements FetchRelationshipRepository<E, I> {

    /**
     * The entity manager.
     */
    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public List<E> findAllEagerRelationship(
        final List<I> parentIds, final Class<E> entityType) {

        if (parentIds.isEmpty()) {

            return List.of();
        }
        final StringBuilder jpql = new StringBuilder();
        final List<Class<?>> visitedEntity = new ArrayList<>();
        final List<Pair<Class<?>, List<Field>>> manyRelationshipClass
            = appendJoinFetchClauses(jpql, entityType, visitedEntity);

        @SuppressWarnings("unchecked")
        final List<E> result = (List<E>) Objects.requireNonNull(
            buildAndExecuteQuery(jpql.toString(), parentIds, entityType));

        for (final Map.Entry<Class<?>,
            List<Field>> classEntry: manyRelationshipClass) {

            final List<?> ids = result.stream()
                .flatMap(entity -> getNestedIds(entity, classEntry.getValue())
                    .stream())
                .toList();
            findAllEagerRelationshipForMany(ids, classEntry.getKey(),
                visitedEntity);
        }
        return result;
    }

    @Override
    public Optional<E> findByIdEagerRelationship(
        final I id, final Class<E> entityType) {

        final StringBuilder jpql = new StringBuilder();
        final List<Class<?>> visitedEntity = new ArrayList<>();
        final List<Pair<Class<?>, List<Field>>> manyRelationshipClass
            = appendJoinFetchClauses(jpql, entityType, visitedEntity);

        @SuppressWarnings("unchecked")
        final E result
            = (E) buildAndExecuteQuery(jpql.toString(), id, entityType);

        for (final Map.Entry<Class<?>,
            List<Field>> classEntry: manyRelationshipClass) {

            if (result != null) {

                final List<?> ids = getNestedIds(result, classEntry.getValue());
                findAllEagerRelationshipForMany(ids, classEntry.getKey(),
                    visitedEntity);
            }
        }
        return Optional.ofNullable(result);
    }

    private void findAllEagerRelationshipForMany(
        final List<?> parentIds, final Class<?> entityType,
        final List<Class<?>> visitedEntity) {

        if (parentIds.isEmpty()) {

            return;
        }
        final StringBuilder jpql = new StringBuilder();
        final List<Pair<Class<?>, List<Field>>> manyRelationshipClass
            = appendJoinFetchClauses(jpql, entityType, visitedEntity);

        @SuppressWarnings("unchecked")
        final List<DataPresentation<?>> result
            = (List<DataPresentation<?>>) Objects.requireNonNull(
                buildAndExecuteQuery(jpql.toString(), parentIds, entityType));

        for (final Map.Entry<Class<?>,
            List<Field>> classEntry: manyRelationshipClass) {

            final List<?> ids = result.stream()
                .flatMap(entity -> getNestedIds(entity, classEntry.getValue())
                    .stream())
                .toList();
            findAllEagerRelationshipForMany(ids, classEntry.getKey(),
                visitedEntity);
        }
    }

    private static List<?> getNestedIds(
        final Object rootObject, final List<Field> fields) {

        final List<Object> entities
            = fields.stream()
                .reduce(List.of(rootObject),
                    (currentObjects, field) -> currentObjects.stream()
                        .flatMap(currentObject -> getNestedField(field,
                            currentObject).stream())
                        .toList(),
                    (a, nestedEntities) -> nestedEntities);

        return entities.stream()
            .map(entity -> ((DataPresentation<?>) entity).getId())
            .toList();
    }

    private static List<Object> getNestedField(
        final Field field, @Nullable final Object currentObject) {

        if (currentObject == null) {

            return List.of();
        }
        field.setAccessible(true);
        final Object newObject = ReflectionUtils.getField(field, currentObject);

        if (newObject instanceof final Collection<?> collection) {

            return new ArrayList<>(collection);
        } else {

            return List.of(newObject);
        }
    }

    @Nullable
    private Object buildAndExecuteQuery(
        final String joinFetchClauses, final Object idParameter,
        final Class<?> entityType) {

        final StringBuilder jpql = new StringBuilder("SELECT e FROM "
            + entityManager.getMetamodel().entity(entityType).getName()
            + " e");
        jpql.append(joinFetchClauses);

        if (idParameter instanceof Collection<?>) {

            jpql.append(" WHERE e.id IN :ids");
            final Query query = entityManager.createQuery(jpql.toString());
            query.setParameter("ids", idParameter);
            return query.getResultList();
        } else {

            jpql.append(" WHERE e.id = :id");
            final Query query = entityManager.createQuery(jpql.toString());
            query.setParameter("id", idParameter);

            // return query.getSingleResultOrNull(); TODO Change to this when
            // next version of JPA is available
            try {

                return query.getSingleResult();
            } catch (final NoResultException e) {

                return null;
            }
        }
    }

    private static List<Pair<Class<?>, List<Field>>> appendJoinFetchClauses(
        final StringBuilder jpql, final Class<?> entityClass,
        final List<Class<?>> visitedEntity) {

        visitedEntity.add(entityClass);

        return appendJoinFetchClausesAux(jpql, "", "e", 0, entityClass, false,
            new ArrayList<>(), new ArrayList<>(), visitedEntity);
    }

    private static List<Pair<Class<?>, List<Field>>> appendJoinFetchClausesAux(
        final StringBuilder jpql, final String currentRelationshipJpql,
        final String alias, final int relationshipLevel,
        final Class<?> entityClass, final boolean multipleManyRelationships,
        final List<Pair<Class<?>, List<Field>>> manyRelationships,
        final List<Field> actualVisitedFields,
        final List<Class<?>> visitedEntity) {

        boolean currentMultipleManyRelationships = multipleManyRelationships;

        for (int i = 0; i < entityClass.getDeclaredFields().length; i++) {

            final StringBuilder relationshipAlias = new StringBuilder();
            final StringBuilder relationshipJpql = new StringBuilder();

            final Field field = entityClass.getDeclaredFields()[i];

            if (isNotRelationship(field, visitedEntity)
                || isMultipleManyRelationship(field,
                    currentMultipleManyRelationships)
                || field.isAnnotationPresent(ManyToMany.class)) {

                if (!isNotRelationship(field, visitedEntity)) {

                    actualVisitedFields.add(field);
                    manyRelationships
                        .add(Pair.of(getManyRelationshipClass(field),
                            new ArrayList<>(actualVisitedFields)));
                    actualVisitedFields.remove(field);
                }
                continue;
            }
            actualVisitedFields.add(field);
            buildRelationshipJpql(relationshipJpql, relationshipAlias, alias,
                relationshipLevel, i, field);

            Class<?> fieldClass
                = handleAnyToOneRelationship(jpql, field, relationshipJpql);

            if (fieldClass == null) {

                fieldClass = handleAnyToManyRelationship(jpql, field,
                    relationshipJpql);
                currentMultipleManyRelationships = true;
            }

            if (fieldClass != null) {

                visitedEntity.add(fieldClass);
                appendJoinFetchClausesAux(jpql,
                    currentRelationshipJpql + relationshipJpql,
                    relationshipAlias.toString(), relationshipLevel + 1,
                    fieldClass, currentMultipleManyRelationships,
                    manyRelationships, actualVisitedFields, visitedEntity);
            }
            actualVisitedFields.remove(field);
        }
        return manyRelationships;
    }

    private static boolean isNotRelationship(
        final Field field, final List<Class<?>> visitedEntity) {

        final boolean alreadyVisited;

        if (field.getGenericType() instanceof final ParameterizedType paramType
            && paramType.getActualTypeArguments()[0] instanceof final Class<
                ?> fieldType) {

            alreadyVisited = visitedEntity.contains(fieldType);
        } else {

            alreadyVisited = visitedEntity.contains(field.getType());
        }

        final boolean isNotOneRelationship
            = !field.isAnnotationPresent(OneToOne.class)
                && !field.isAnnotationPresent(ManyToOne.class);
        final boolean isNotManyRelationship
            = !field.isAnnotationPresent(OneToMany.class)
                && !field.isAnnotationPresent(ManyToMany.class);

        return (isNotOneRelationship && isNotManyRelationship)
            || alreadyVisited;
    }

    private static boolean isMultipleManyRelationship(
        final Field field, final boolean currentMultipleManyRelationships) {

        return (field.isAnnotationPresent(OneToMany.class)
            || field.isAnnotationPresent(ManyToMany.class))
            && currentMultipleManyRelationships;
    }

    private static void buildRelationshipJpql(
        final StringBuilder relationshipJpql,
        final StringBuilder relationshipAlias, final String alias,
        final int relationshipLevel, final int i, final Field field) {

        relationshipAlias.append("r_");
        relationshipAlias.append(i);
        relationshipAlias.append("_");
        relationshipAlias.append(relationshipLevel);

        relationshipJpql.append(" LEFT JOIN FETCH ");
        relationshipJpql.append(alias);
        relationshipJpql.append(".");
        relationshipJpql.append(field.getName());
        relationshipJpql.append(" ");
        relationshipJpql.append(relationshipAlias);
    }

    @Nullable
    private static Class<?> handleAnyToOneRelationship(
        final StringBuilder jpql, final Field field,
        final StringBuilder relationshipJpql) {

        if (field.isAnnotationPresent(OneToOne.class)
            || field.isAnnotationPresent(ManyToOne.class)) {

            jpql.append(relationshipJpql);
            return field.getType();
        }
        return null;
    }

    @Nullable
    private static Class<?> handleAnyToManyRelationship(
        final StringBuilder jpql, final Field field,
        final StringBuilder relationshipJpql) {

        final Class<?> fieldClass = getManyRelationshipClass(field);
        jpql.append(relationshipJpql);
        return fieldClass;
    }

    @Nullable
    private static Class<?> getManyRelationshipClass(final Field field) {

        if (field.getGenericType() instanceof final ParameterizedType paramType
            && paramType.getActualTypeArguments()[0] instanceof final Class<
                ?> fieldType) {

            return fieldType;
        } else {

            return null;
        }
    }
}
