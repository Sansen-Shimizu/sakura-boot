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

package org.sansenshimizu.sakuraboot.test.functional.bulk;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.functional.SuperFT;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.mapper.MapperFTUtil;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for delete all by criteria functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from
 * {@link DeleteAllByCriteriaFT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteAllByCriteriaFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements //
 *     DeleteAllByCriteriaFT&lt;YourEntity, YourIdType&gt; {
 *
 *     private final YourUtil util;
 *
 *     private final ApplicationContext applicationContext;
 *
 *     private final ObjectMapper objectMapper;
 *
 *     &#064;LocalServerPort
 *     private int port;
 *
 *     &#064;Autowired
 *     YourFT(
 *         final YourUtil util, final ApplicationContext applicationContext,
 *         final ObjectMapper objectMapper) {
 *
 *         this.util = util;
 *         this.applicationContext = applicationContext;
 *         this.objectMapper = objectMapper;
 *     }
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public ApplicationContext getApplicationContext() {
 *
 *         return applicationContext;
 *     }
 *
 *     &#064;Override
 *     public ObjectMapper getObjectMapper() {
 *
 *         return objectMapper;
 *     }
 *
 *     &#064;Override
 *     public int getPort() {
 *
 *         return port;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperFT
 * @since      0.1.2
 */
public interface DeleteAllByCriteriaFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperFT<E, I> {

    @Test
    @DisplayName("GIVEN a null filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the controller should return a no-content response")
    default void testDeleteAllByCriteriaWithNullFilter() {

        // GIVEN
        final I id = Objects.requireNonNull(createAndSaveEntity().getId());
        final I secondId
            = Objects.requireNonNull(createAndSaveEntity().getId());

        final Repositories repositories
            = new Repositories(getApplicationContext());
        RelationshipUtils.getMappedByRelationClass(getUtil().getEntityClass())
            .reversed()
            .stream()
            .map(clazz -> repositories.getRepositoryFor(clazz).orElse(null))
            .filter(CrudRepository.class::isInstance)
            .map(CrudRepository.class::cast)
            .forEach(CrudRepository<E, I>::deleteAll);

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().delete().then();

        // THEN
        response.assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .assertThat()
            .body(Matchers.emptyString());
        assertThat(getRepository().findAll()).isEmpty();

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                assertThat(cachingUtil.getCacheManager().getCache(cacheName))
                    .isNotNull()
                    .extracting(cache -> cache.get(id))
                    .isNull();
                assertThat(cachingUtil.getCacheManager().getCache(cacheName))
                    .isNotNull()
                    .extracting(cache -> cache.get(secondId))
                    .isNull();
                assertThat(
                    cachingUtil.getCacheManager().getCache(cacheName + "All"))
                    .isNotNull()
                    .extracting(cache -> cache.get("all"))
                    .isNull();
            }
        }
    }

    @Test
    @DisplayName("GIVEN a filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the controller should return a no-content response")
    default void testDeleteAllByCriteriaWithFilter() {

        // GIVEN
        final E saveEntity = createAndSaveEntity();
        final E secondSaveEntity = createAndSaveEntity();
        final I id = Objects.requireNonNull(saveEntity.getId());
        final I secondId = Objects.requireNonNull(secondSaveEntity.getId());

        final DataPresentation<I> cacheData;
        final DataPresentation<I> secondCacheData;

        if (getUtil() instanceof final MapperFTUtil<E, I, ?> mapperUtil) {

            cacheData = mapperUtil.getData();
            secondCacheData = mapperUtil.getData();
        } else {

            cacheData = getUtil().getEntity();
            secondCacheData = getUtil().getEntity();
        }

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                final Cache cache = Objects.requireNonNull(
                    cachingUtil.getCacheManager().getCache(cacheName));
                cache.put(id, cacheData);
                cache.put(secondId, secondCacheData);
                Objects
                    .requireNonNull(cachingUtil.getCacheManager()
                        .getCache(cacheName + "All"))
                    .put("all",
                        new PageImpl<>(List.of(cacheData, secondCacheData),
                            Pageable.ofSize(2), 2));
            }
        }

        final Map<String, String> paramFilter = new TreeMap<>();
        paramFilter.put("id.equal", Objects.requireNonNull(id).toString());

        final Repositories repositories
            = new Repositories(getApplicationContext());
        RelationshipUtils.getMappedByRelationClass(getUtil().getEntityClass())
            .reversed()
            .stream()
            .map(clazz -> repositories.getRepositoryFor(clazz).orElse(null))
            .filter(CrudRepository.class::isInstance)
            .map(CrudRepository.class::cast)
            .forEach(CrudRepository<E, I>::deleteAll);

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().params(paramFilter).when().delete().then();

        // THEN
        response.assertThat()
            .statusCode(HttpStatus.NO_CONTENT.value())
            .assertThat()
            .body(Matchers.emptyString());
        assertThat(getRepository().findAll()).hasSize(1);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                assertThat(cachingUtil.getCacheManager().getCache(cacheName))
                    .isNotNull()
                    .extracting(cache -> cache.get(id))
                    .isNull();
                assertThat(cachingUtil.getCacheManager().getCache(cacheName))
                    .isNotNull()
                    .extracting(cache -> cache.get(secondId))
                    .isNull();
                assertThat(
                    cachingUtil.getCacheManager().getCache(cacheName + "All"))
                    .isNotNull()
                    .extracting(cache -> cache.get("all"))
                    .isNull();
            }
        }
    }
}
