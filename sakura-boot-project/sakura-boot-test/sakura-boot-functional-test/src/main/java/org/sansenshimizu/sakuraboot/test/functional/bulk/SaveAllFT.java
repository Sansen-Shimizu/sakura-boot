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
import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;
import org.sansenshimizu.sakuraboot.test.functional.SuperFT;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.mapper.MapperFTUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for saveAll functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link SaveAllFT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SaveAllFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT implements SaveAllFT&lt;YourEntity, YourIdType&gt; {
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
public interface SaveAllFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperFT<E, I> {

    @Test
    @DisplayName("GIVEN valid entities,"
        + " WHEN saving all,"
        + " THEN the controller should save all and return a valid response "
        + "with the entities")
    default void testSaveAll() throws Exception {

        // GIVEN
        final List<DataPresentation<I>> datasWithoutId;

        if (getUtil() instanceof final MapperFTUtil<?, I, ?> mapperUtil) {

            datasWithoutId = List.of(mapperUtil.getDataWithoutId(),
                mapperUtil.getDataWithoutId());
        } else {

            datasWithoutId = List.of(getUtil().getEntityWithoutId(),
                getUtil().getEntityWithoutId());
        }

        // WHEN
        @SuppressWarnings("java:S1941")
        final ValidatableResponse response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(datasWithoutId)
            .when()
            .post("/bulk")
            .then();

        // THEN
        final List<E> all = getRepository().findAll();
        final List<DataPresentation<I>> expectedDatas
            = all.stream().map((final E entity) -> {

                final DataPresentation<I> expectedData = BeanCreatorHelper
                    .getEmptyBean(getUtil().getEntityClass());
                ReflectionTestUtils.setField(expectedData, "id",
                    entity.getId());
                return expectedData;
            }).toList();

        assertResponse(response, HttpStatus.CREATED, ContentType.JSON,
            Map.of("location",
                RestAssured.baseURI + ":" + RestAssured.port + getBasePath()),
            expectedDatas, false);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                for (final DataPresentation<I> expectedData: expectedDatas) {

                    final I expectedId
                        = Objects.requireNonNull(expectedData.getId());
                    assertThat(
                        cachingUtil.getCacheManager().getCache(cacheName))
                        .isNotNull()
                        .extracting(cache -> cache.get(expectedId,
                            datasWithoutId.get(0).getClass()))
                        .isNotNull()
                        .extracting(DataPresentation<I>::getId)
                        .isEqualTo(expectedId);
                }

                assertThat(
                    cachingUtil.getCacheManager().getCache(cacheName + "All"))
                    .isNotNull()
                    .extracting(cache -> cache.get("all"))
                    .isNull();
            }
        }
    }

    @Test
    @DisplayName("GIVEN no entity and content type,"
        + " WHEN saving all,"
        + " THEN the controller shouldn't save and return an error response")
    default void testSaveAllWithNoEntityAndContentType() {

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().post().then();

        // THEN
        assertErrorResponse(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            " media type is not supported. Supported media types are ");
    }

    @Test
    @DisplayName("GIVEN no entity,"
        + " WHEN saving all,"
        + " THEN the controller shouldn't save and return an error response")
    default void testSaveAllWithNoEntity() {

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .contentType(ContentType.JSON)
            .when()
            .post()
            .then();

        // THEN
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
            REQUIRED_BODY_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN an invalid entity,"
        + " WHEN saving all,"
        + " THEN the controller shouldn't save and return an error response")
    default void testSaveAllWithInvalidEntity() {

        final Optional<?> dataWithoutId
            = getUtil().createValidationErrorEntity(null);

        if (dataWithoutId.isPresent()) {

            // WHEN
            final ValidatableResponse response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(List.of(dataWithoutId.get()))
                .when()
                .post()
                .then();

            // THEN
            assertErrorResponse(response, HttpStatus.BAD_REQUEST,
                VALIDATION_ERROR_MESSAGE);
        }
    }
}
