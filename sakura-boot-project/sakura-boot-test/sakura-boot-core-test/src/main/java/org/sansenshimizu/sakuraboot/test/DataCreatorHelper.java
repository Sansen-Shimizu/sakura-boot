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

package org.sansenshimizu.sakuraboot.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToOneAndAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;

/**
 * The data creator helper used to create all kinds of {@link DataPresentation}
 * with every field set to random values
 * using reflection.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
public final class DataCreatorHelper {

    /**
     * The Bean holder map with the class name as key.
     */
    private static final Map<String, DataHolder<?, ?>> DATA_HOLDERS
        = new ConcurrentHashMap<>();

    private DataCreatorHelper() {

        throw new UnsupportedOperationException(
            "This class is not meant to be instantiated.");
    }

    /**
     * Update id of the given {@link DataPresentation}.
     *
     * @param  <D>  the type of the data.
     * @param  <I>  the type of the id.
     * @param  ids  the {@link EntityIds} used to update the id.
     * @param  data the data to update.
     * @return      the updated data.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D
        updateId(@Nullable final EntityIds ids, final D data) {

        return updateId(ids, data, new ArrayList<>());
    }

    private static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D updateId(
            @Nullable final EntityIds ids, final D data,
            final List<DataPresentation<?>> visited) {

        visited.add(data);

        if (ids == null) {

            ReflectionTestUtils.setField(data, "id", null);
        } else {

            ReflectionTestUtils.setField(data, "id", ids.id());
        }

        if (data instanceof final DataPresentation1RelationshipAnyToOne<?,
            ?> dataRelationship) {

            final DataPresentation<?> relationalEntity
                = dataRelationship.getRelationship();

            if (relationalEntity != null
                && !visited.contains(relationalEntity)) {

                if (ids == null) {

                    updateId(null, relationalEntity, visited);
                } else {

                    updateId(ids.relationalId(), relationalEntity, visited);
                }
            }

            if (dataRelationship instanceof final BasicDto1RelationshipAnyToOne<
                ?, ?, ?> dto) {

                final Object value;

                if (ids == null || ids.relationalId() == null) {

                    value = null;
                } else {

                    value = ids.relationalId().id();
                }
                ReflectionTestUtils.setField(dto, "relationshipId", value);
            }
        }

        final String relationshipsIdFieldName = "relationshipsId";

        if (data instanceof final DataPresentation1RelationshipAnyToMany<?,
            ?> dataRelationship) {

            final Set<? extends DataPresentation<?>> relationalEntities
                = dataRelationship.getRelationships();

            if (relationalEntities != null) {

                if (ids != null && ids.relationalIds().isEmpty()) {

                    relationalEntities.stream()
                        .filter(entity -> entity != null
                            && !visited.contains(entity))
                        .forEach((final DataPresentation<?> entity) -> {

                            ReflectionTestUtils.setField(entity, "id", null);
                            updateId(new EntityIds(), entity, visited);
                        });
                } else {

                    if (ids != null
                        && !relationalEntities.isEmpty()
                        && relationalEntities.size()
                            != ids.relationalIds().size()) {

                        throw new IllegalArgumentException(
                            """
                            The number of relational entities is not equal to
                            the number of relational ids.
                            """);
                    }

                    if (ids == null) {

                        relationalEntities.stream()
                            .filter(entity -> entity != null
                                && !visited.contains(entity))
                            .forEach(entity -> updateId(null, entity, visited));
                    } else {

                        final Iterator<? extends DataPresentation<?>> it
                            = relationalEntities.iterator();
                        ids.relationalIds()
                            .stream()
                            .map(id -> Pair.of(id, it.next()))
                            .filter(pair -> pair.getRight() != null
                                && !visited.contains(pair.getRight()))
                            .forEach(pair -> updateId(pair.getLeft(),
                                pair.getRight(), visited));
                    }
                }
            }

            /*@formatter:off*/
            if (dataRelationship
                instanceof final BasicDto1RelationshipAnyToMany<?, ?, ?> dto) {
                /*@formatter:on*/

                final Object value;
                final Set<?> relationalEntitiesId = dto.getRelationshipsId();

                if (ids == null && relationalEntitiesId != null) {

                    value = Stream.generate(() -> null)
                        .limit(relationalEntitiesId.size())
                        .collect(Collectors.toSet());
                } else {

                    if (ids != null) {

                        value = ids.relationalIds()
                            .stream()
                            .map(EntityIds::id)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toUnmodifiableSet());
                    } else {

                        value = null;
                    }
                }
                ReflectionTestUtils.setField(dto, relationshipsIdFieldName,
                    value);
            }
        }

        if (data instanceof final DataPresentation2RelationshipAnyToOne<?, ?,
            ?> dataRelationship) {

            final DataPresentation<?> relationalEntity
                = dataRelationship.getSecondRelationship();

            if (relationalEntity != null
                && !visited.contains(relationalEntity)) {

                if (ids == null) {

                    updateId(null, relationalEntity, visited);
                } else {

                    updateId(ids.secondRelationalId(), relationalEntity,
                        visited);
                }
            }

            if (dataRelationship instanceof final BasicDto2RelationshipAnyToOne<
                ?, ?, ?, ?, ?> dto) {

                final Object value;

                if (ids != null) {

                    final EntityIds entityIds = ids.secondRelationalId();

                    if (entityIds != null) {

                        value = entityIds.id();
                    } else {

                        value = null;
                    }
                } else {

                    value = null;
                }
                ReflectionTestUtils.setField(dto, "secondRelationshipId",
                    value);
            }
        }

        if (data instanceof final DataPresentation2RelationshipAnyToMany<?, ?,
            ?> dataRelationship) {

            final Set<? extends DataPresentation<?>> secondRelationalEntities
                = dataRelationship.getSecondRelationships();

            if (secondRelationalEntities != null) {

                if (ids != null && ids.secondRelationalIds().isEmpty()) {

                    secondRelationalEntities.stream()
                        .filter(entity -> entity != null
                            && !visited.contains(entity))
                        .forEach((final DataPresentation<?> entity) -> {

                            ReflectionTestUtils.setField(entity, "id", null);
                            updateId(new EntityIds(), entity, visited);
                        });
                } else {

                    if (ids != null
                        && !secondRelationalEntities.isEmpty()
                        && secondRelationalEntities.size()
                            != ids.secondRelationalIds().size()) {

                        throw new IllegalArgumentException(
                            """
                            The number of second relational entities is not
                            equal to the number of second relational ids.
                            """);
                    }

                    if (ids == null) {

                        secondRelationalEntities.stream()
                            .filter(entity -> entity != null
                                && !visited.contains(entity))
                            .forEach(entity -> updateId(null, entity, visited));
                    } else {

                        final Iterator<? extends DataPresentation<?>> it
                            = secondRelationalEntities.iterator();
                        ids.secondRelationalIds()
                            .stream()
                            .map(id -> Pair.of(id, it.next()))
                            .filter(pair -> pair.getRight() != null
                                && !visited.contains(pair.getRight()))
                            .forEach(pair -> updateId(pair.getLeft(),
                                pair.getRight(), visited));
                    }
                }
            }

            /*@formatter:off*/
            if (dataRelationship
                instanceof final BasicDto2RelationshipAnyToMany<?, ?, ?, ?, ?>
                dto) {
                /*@formatter:on*/

                final Object value;
                final Set<?> secondRelationalEntitiesId
                    = dto.getSecondRelationshipsId();

                if (ids == null && secondRelationalEntitiesId != null) {

                    value = Stream.generate(() -> null)
                        .limit(secondRelationalEntitiesId.size())
                        .collect(Collectors.toSet());
                } else {

                    if (ids != null) {

                        value = ids.secondRelationalIds()
                            .stream()
                            .map(EntityIds::id)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toUnmodifiableSet());
                    } else {

                        value = null;
                    }
                }
                ReflectionTestUtils.setField(dto, "secondRelationshipsId",
                    value);
            }
        }

        /*@formatter:off*/
        if (data instanceof final
            BasicDto2RelationshipAnyToOneAndAnyToMany<?, ?, ?, ?, ?> dto) {
            /*@formatter:on*/

            final Object value;
            final Set<?> secondRelationalEntitiesId = dto.getRelationshipsId();

            if (ids == null && secondRelationalEntitiesId != null) {

                value = Stream.generate(() -> null)
                    .limit(secondRelationalEntitiesId.size())
                    .collect(Collectors.toSet());
            } else {

                if (ids != null) {

                    value = ids.relationalIds()
                        .stream()
                        .map(EntityIds::id)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet());
                } else {

                    value = null;
                }
            }
            ReflectionTestUtils.setField(dto, relationshipsIdFieldName, value);
        }
        return data;
    }

    /**
     * Get a data without id for the given data class.
     * Remove also the id from relationships.
     *
     * @param  <D>       The type of the data.
     * @param  <I>       The type of the id.
     * @param  dataClass the data class of the data.
     * @return           the data without id.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D
        getDataWithoutId(final Class<D> dataClass) {

        final D data
            = SerializationUtils.clone(getDataHolder(dataClass).data());
        return updateId(null, data);
    }

    /**
     * Get a data with the given id for the given data class.
     *
     * @param  <D>       The type of the data.
     * @param  <I>       The type of the id.
     * @param  dataClass the data class of the data.
     * @param  id        the id of the data.
     * @return           the data.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D
        getData(final Class<D> dataClass, @Nullable final I id) {

        final D data = getDataHolder(dataClass).data();
        ReflectionTestUtils.setField(data, "id", id);
        return data;
    }

    /**
     * Get a second data with the given id for the given data class.
     *
     * @param  <D>       The type of the data.
     * @param  <I>       The type of the id.
     * @param  dataClass the data class of the data.
     * @param  id        the id of the data.
     * @return           the second data.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D
        getSecondData(final Class<D> dataClass, @Nullable final I id) {

        final D data = getDataHolder(dataClass).secondData();
        ReflectionTestUtils.setField(data, "id", id);
        return data;
    }

    /**
     * Get a different data with the given id for the given data class.
     *
     * @param  <D>       The type of the data.
     * @param  <I>       The type of the id.
     * @param  dataClass the data class of the data.
     * @param  id        the id of the data.
     * @return           the different data.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D
        getDifferentData(final Class<D> dataClass, @Nullable final I id) {

        final D data = getDataHolder(dataClass).differentData();
        ReflectionTestUtils.setField(data, "id", id);
        return data;
    }

    /**
     * Get a partial data with the given id for the given data class.
     *
     * @param  <D>       The type of the data.
     * @param  <I>       The type of the id.
     * @param  dataClass the data class of the data.
     * @param  id        the id of the data.
     * @return           the partial data.
     */
    public static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> D
        getPartialData(final Class<D> dataClass, @Nullable final I id) {

        final D data = getDataHolder(dataClass).partialData();
        ReflectionTestUtils.setField(data, "id", id);
        return data;
    }

    private static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> DataHolder<D, I>
        getDataHolder(final Class<D> dataClass) {

        if (!DATA_HOLDERS.containsKey(dataClass.getName())) {

            createDatas(dataClass);
        }

        @SuppressWarnings("unchecked")
        final DataHolder<D, I> dataHolder
            = (DataHolder<D, I>) DATA_HOLDERS.get(dataClass.getName());
        return dataHolder;
    }

    private static <
        D extends DataPresentation<I>,
        I extends Comparable<? super I> & Serializable> void
        createDatas(final Class<D> dataClass) {

        final D differentData;

        try {

            differentData
                = BeanCreatorHelper.createBeanFromConstructor(dataClass);
        } catch (final InstantiationException e) {

            throw new RuntimeException(e);
        }
        final D partialData;

        try {

            partialData = BeanCreatorHelper
                .createBeanFromConstructorWithNullFields(dataClass);
        } catch (final InstantiationException e) {

            throw new RuntimeException(e);
        }

        final BeanCreatorHelper.BeanHolder<D> beans
            = BeanCreatorHelper.getBeanHolder(dataClass);
        final D data = beans.bean();
        final D secondData = beans.secondBean();

        DATA_HOLDERS.put(dataClass.getName(),
            new DataHolder<>(data, secondData, differentData, partialData));
    }

    /**
     * Retrieves the identifiers from the given {@link DataPresentation} entity.
     *
     * @param  entity the DataPresentation entity from which to retrieve the
     *                identifiers.
     * @return        an {@link EntityIds} object containing the identifiers of
     *                the entity and its related entities.
     */
    public static EntityIds getIdsFromEntity(final DataPresentation<?> entity) {

        return getIdsFromEntity(entity, new ArrayList<>());
    }

    private static EntityIds getIdsFromEntity(
        final DataPresentation<?> entity,
        final List<DataPresentation<?>> visited) {

        visited.add(entity);
        final Object id = entity.getId();
        EntityIds relationalId = null;

        if (entity instanceof final DataPresentation1RelationshipAnyToOne<?,
            ?> entity1Relationship) {

            final DataPresentation<?> relationalEntity
                = entity1Relationship.getRelationship();

            if (relationalEntity != null
                && !visited.contains(relationalEntity)) {

                relationalId = getIdsFromEntity(relationalEntity, visited);
                /*@formatter:off*/
            } else if (entity1Relationship
                instanceof final BasicDto1RelationshipAnyToOne<?, ?, ?> dto) {
                /*@formatter:on*/

                relationalId = new EntityIds(dto.getRelationshipId(), null,
                    Set.of(), null, Set.of());
            }
        }
        final Set<EntityIds> relationalIds = new HashSet<>();

        if (entity instanceof final DataPresentation1RelationshipAnyToMany<?,
            ?> entity1Relationship) {

            final Set<? extends DataPresentation<?>> relationalEntities
                = entity1Relationship.getRelationships();

            /*@formatter:off*/
            if ((relationalEntities == null || relationalEntities.isEmpty())
                && entity1Relationship
                instanceof final BasicDto1RelationshipAnyToMany<?, ?, ?> dto) {
                /*@formatter:on*/

                final Set<?> relationalEntitiesId = dto.getRelationshipsId();

                if (relationalEntitiesId != null) {

                    relationalEntitiesId.stream()
                        .map(entityId -> new EntityIds(entityId, null, Set.of(),
                            null, Set.of()))
                        .forEach(relationalIds::add);
                }
            } else {

                if (relationalEntities != null) {

                    relationalEntities.stream()
                        .filter(relationalEntity -> relationalEntity != null
                            && !visited.contains(relationalEntity))
                        .map(relationalEntity -> getIdsFromEntity(
                            relationalEntity, visited))
                        .forEach(relationalIds::add);
                }
            }
        }
        EntityIds secondRelationalId = null;

        if (entity instanceof final DataPresentation2RelationshipAnyToOne<?, ?,
            ?> entity2Relationship) {

            final DataPresentation<?> relationalEntity
                = entity2Relationship.getSecondRelationship();

            if (relationalEntity != null
                && !visited.contains(relationalEntity)) {

                secondRelationalId
                    = getIdsFromEntity(relationalEntity, visited);
                /*@formatter:off*/
            } else if (entity2Relationship
                instanceof final BasicDto2RelationshipAnyToOne<?, ?, ?, ?, ?>
                dto) {
                /*@formatter:on*/

                secondRelationalId
                    = new EntityIds(dto.getSecondRelationshipId(), null,
                        Set.of(), null, Set.of());
            }
        }
        final Set<EntityIds> secondRelationalIds = new HashSet<>();

        if (entity instanceof final DataPresentation2RelationshipAnyToMany<?, ?,
            ?> entity2Relationship) {

            final Set<? extends DataPresentation<?>> secondRelationalEntities
                = entity2Relationship.getSecondRelationships();

            /*@formatter:off*/
            if ((secondRelationalEntities == null
                || secondRelationalEntities.isEmpty())
                && entity2Relationship
                instanceof final BasicDto2RelationshipAnyToMany<?, ?, ?, ?, ?>
                dto) {
                /*@formatter:on*/

                final Set<?> secondRelationalEntitiesId
                    = dto.getSecondRelationshipsId();

                if (secondRelationalEntitiesId != null) {

                    secondRelationalEntitiesId.stream()
                        .map(entityId -> new EntityIds(entityId, null, Set.of(),
                            null, Set.of()))
                        .forEach(secondRelationalIds::add);
                }
            } else {

                if (secondRelationalEntities != null) {

                    secondRelationalEntities.stream()
                        .filter(relationalEntity -> relationalEntity != null
                            && !visited.contains(relationalEntity))
                        .map(relationalEntity -> getIdsFromEntity(
                            relationalEntity, visited))
                        .forEach(secondRelationalIds::add);
                }
            }
        }
        return new EntityIds(id, relationalId,
            Collections.unmodifiableSet(relationalIds), secondRelationalId,
            Collections.unmodifiableSet(secondRelationalIds));
    }

    /**
     * A {@link DataPresentation} holder with two identical data, a different
     * data and a partial data.
     *
     * @param data          the first data.
     * @param secondData    the second data.
     * @param differentData the different data.
     * @param partialData   the partial.
     * @param <D>           the data type.
     * @param <I>           the id type.
     */
    public record DataHolder<D extends DataPresentation<
        I>, I extends Comparable<? super I> & Serializable>(
            D data, D secondData, D differentData, D partialData) {}

    /**
     * A record to hold the identifiers of different entities used in tests.
     * This record is used to encapsulate the
     * identifiers of the main entity, a related entity, and a second related
     * entity.
     *
     * @param id                  The main entity identifier.
     * @param relationalId        The related entity identifier.
     * @param relationalIds       The related entity identifiers.
     * @param secondRelationalId  The second related entity identifier.
     * @param secondRelationalIds The second related entity identifiers.
     */
    public record EntityIds(
        @Nullable Object id, @Nullable EntityIds relationalId,
        Set<EntityIds> relationalIds, @Nullable EntityIds secondRelationalId,
        Set<EntityIds> secondRelationalIds) {

        /**
         * Constructor for EntityIds class. Initializes all fields to their
         * default values.
         */
        public EntityIds() {

            this(null, null, Set.of(), null, Set.of());
        }

        /**
         * Constructor for EntityIds class. Initializes all fields using the
         * given EntityIds object.
         *
         * @param entityIds The EntityIds object to copy.
         */
        public EntityIds(final EntityIds entityIds) {

            this(entityIds.id(), entityIds.relationalId(),
                entityIds.relationalIds(), entityIds.secondRelationalId(),
                entityIds.secondRelationalIds());
        }

        /**
         * Constructor for EntityIds class. Initializes all fields using the
         * given {@link DataPresentation} object.
         *
         * @param entity The DataPresentation entity from which to retrieve the
         *               identifiers.
         */
        public EntityIds(final DataPresentation<?> entity) {

            this(getIdsFromEntity(entity));
        }
    }
}
