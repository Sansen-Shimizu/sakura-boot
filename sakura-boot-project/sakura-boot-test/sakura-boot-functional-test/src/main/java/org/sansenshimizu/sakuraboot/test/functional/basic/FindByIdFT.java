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

package org.sansenshimizu.sakuraboot.test.functional.basic;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.functional.BasicFT;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.mapper.MapperFTUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for find by id functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link FindByIdFT},
 * follow
 * these steps:
 * </p>
 * <p>
 * Implements the {@link FindByIdFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements FindByIdFT&lt;YourEntity, YourIdType&gt; {
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
 * @see        BasicFT
 * @since      0.1.0
 */
public interface FindByIdFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicFT<E, I> {

    @Test
    @DisplayName("GIVEN a valid ID,"
        + " WHEN finding by ID,"
        + " THEN the controller should return a valid response with the "
        + "corresponding entity")
    default void testFindById() throws Exception {

        // GIVEN
        final E savedEntity = createAndSaveEntity();

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().get("/" + savedEntity.getId()).then();

        // THEN
        final DataPresentation<I> expectedData;

        if (getUtil() instanceof final MapperFTUtil<?, I, ?> mapperUtil) {

            expectedData = mapperUtil.toDto(savedEntity);
        } else {

            expectedData = savedEntity;
        }
        assertResponse(response, HttpStatus.OK, ContentType.JSON, Map.of(),
            expectedData, true);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                assertThat(cachingUtil.getCacheManager().getCache(cacheName))
                    .isNotNull()
                    .extracting(cache -> cache.get(
                        Objects.requireNonNull(expectedData.getId()),
                        expectedData.getClass()))
                    .isEqualTo(expectedData);
            }
        }
    }

    @Test
    @DisplayName("GIVEN no ID,"
        + " WHEN finding by ID,"
        + " THEN the controller should return an error response")
    default void testFindByIdWithNoId() {

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().get(ENCODE_SPACE).then();
        // Need to use %20 because rest-assured remove the space

        // THEN
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
            ID_MISSING_ERROR_MESSAGE);
    }
}
