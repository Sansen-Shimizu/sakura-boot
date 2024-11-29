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

package org.sansenshimizu.sakuraboot.test.integration.repository.basic;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;
import org.sansenshimizu.sakuraboot.test.SuperIT;
import org.sansenshimizu.sakuraboot.test.SuperITUtil;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for repository integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link BasicRepositoryIT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicRepositoryIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;DataJpaTest
 * public class YourIT //
 *     implements BasicRepositoryIT&lt;YourEntity, YourIdType&gt; {
 *
 *     private final YourUtil util = new YourUtil();
 *
 *     private final YourRepository repository;
 *
 *     &#064;Autowired
 *     YourIT(final YourRepository repository) {
 *
 *         this.repository = repository;
 *     }
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourRepository getRepository() {
 *
 *         return repository;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperIT
 * @see        SuperITUtil
 * @since      0.1.0
 */
@Transactional
public interface BasicRepositoryIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperIT<E, I> {

    /**
     * Return a repository class that extends {@link SuperRepository}.
     *
     * @return A repository class use in integration test.
     */
    SuperRepository<E, I> getRepository();

    /**
     * Create and save a new entity for testing purpose.
     *
     * @return The saved entity.
     */
    default E createAndSaveEntity() {

        return getRepository().save(getUtil().getEntityWithoutId());
    }

    @Test
    @DisplayName("GIVEN a valid entity,"
        + " WHEN saving,"
        + " THEN the repository should save and return the entity")
    default void testSaveEntity() {

        // GIVEN
        final E entityWithoutId = getUtil().getEntityWithoutId();

        // WHEN
        final DataPresentation<I> savedEntity
            = getRepository().save(entityWithoutId);

        // THEN
        assertThat(savedEntity.getId()).isNotNull();

        ReflectionUtils.doWithFields(getUtil().getEntityClass(),
            (final Field field) -> {

                if (field.trySetAccessible()) {

                    final Object relation
                        = ReflectionUtils.getField(field, savedEntity);

                    if (relation instanceof final DataPresentation<?> data) {

                        assertThat(data.getId()).isNotNull();
                    }
                }
            }, RelationshipUtils::isRelationshipExcludeMappedBy);
    }

    @Test
    @DisplayName("GIVEN a pageable request,"
        + " WHEN finding all,"
        + " THEN the repository should return a page of entities")
    default void testFindAllEntityWithPageable() {

        // GIVEN
        final E savedEntity = createAndSaveEntity();
        createAndSaveEntity();
        final Pageable pageable = Pageable.ofSize(1);

        // WHEN
        final Page<E> foundPage = getRepository().findAll(pageable);

        // THEN
        assertThat(foundPage).isNotEmpty().hasSize(1).contains(savedEntity);
    }

    @Test
    @DisplayName("GIVEN no pageable request,"
        + " WHEN finding all,"
        + " THEN the repository should return a list of entities")
    default void testFindAllEntity() {

        // GIVEN
        final E savedEntity = createAndSaveEntity();
        final E otherSavedEntity = createAndSaveEntity();

        // WHEN
        final List<E> foundPage = getRepository().findAll();

        // THEN
        assertThat(foundPage).isNotEmpty()
            .hasSize(2)
            .contains(savedEntity, otherSavedEntity);
    }

    @Test
    @DisplayName("GIVEN a valid ID,"
        + " WHEN finding by ID,"
        + " THEN the repository should return the corresponding entity")
    default void testFindEntityById() {

        // GIVEN
        final E savedEntity = createAndSaveEntity();

        // WHEN
        final Optional<E> foundEntity = getRepository()
            .findById(Objects.requireNonNull(savedEntity.getId()));

        // THEN
        assertThat(foundEntity).isNotEmpty().contains(savedEntity);
    }

    @Test
    @DisplayName("GIVEN a valid ID and entity,"
        + " WHEN updating by ID,"
        + " THEN the repository should update and return the entity")
    default void testUpdateEntityById() {

        // GIVEN
        final E saveEntity = createAndSaveEntity();
        final I id = saveEntity.getId();
        final E otherCreatedEntity
            = getUtil().getDifferentEntityWithId(saveEntity);

        // WHEN
        final E updatedEntity = getRepository().save(otherCreatedEntity);

        // THEN
        assertThat(updatedEntity.getId()).isEqualTo(id);
        assertThat(updatedEntity).isEqualTo(otherCreatedEntity);
    }

    @Test
    @DisplayName("GIVEN a valid ID,"
        + " WHEN deleting by ID,"
        + " THEN the repository should delete the entity")
    default void testDeleteEntityById() {

        // GIVEN
        final I savedEntityId
            = Objects.requireNonNull(createAndSaveEntity().getId());

        // WHEN
        getRepository().deleteById(savedEntityId);

        // THEN
        assertThat(getRepository().findById(savedEntityId)).isEmpty();
    }
}
