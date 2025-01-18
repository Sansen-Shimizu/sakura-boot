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
 * The interface for patch all functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link PatchAllFT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link PatchAllFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements PatchAllFT&lt;YourEntity, YourIdType&gt; {
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
public interface PatchAllFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperFT<E, I> {

    @Test
    @DisplayName("GIVEN valid entities,"
        + " WHEN patching all,"
        + " THEN the controller should patch all and return a valid response "
        + "with the patched entities")
    default void testPatchAll() throws Exception {

        // GIVEN
        final List<E> saveEntities
            = List.of(createAndSaveEntity(), createAndSaveEntity());
        final List<DataPresentation<I>> datasWithId;

        if (getUtil() instanceof final MapperFTUtil<?, I, ?> mapperUtil) {

            datasWithId = List.of(
                mapperUtil.getPartialDataWithId(saveEntities.get(0).getId()),
                mapperUtil.getPartialDataWithId(saveEntities.get(1).getId()));
        } else {

            datasWithId = List.of(
                getUtil().getPartialEntityWithId(saveEntities.get(0).getId()),
                getUtil().getPartialEntityWithId(saveEntities.get(1).getId()));
        }

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(datasWithId)
            .when()
            .patch()
            .then();

        // THEN
        assertResponse(response, HttpStatus.OK, ContentType.JSON, Map.of(),
            datasWithId, false);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                for (final DataPresentation<I> expectedData: datasWithId) {

                    final I expectedId
                        = Objects.requireNonNull(expectedData.getId());
                    assertThat(
                        cachingUtil.getCacheManager().getCache(cacheName))
                        .isNotNull()
                        .extracting(cache -> cache.get(expectedId,
                            expectedData.getClass()))
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
        + " WHEN patching all,"
        + " THEN the controller shouldn't patch and return an error response")
    default void testPatchAllWithNoEntityAndContentType() {

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().patch().then();

        // THEN
        assertErrorResponse(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            MEDIA_TYPE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN no entity,"
        + " WHEN patch all,"
        + " THEN the controller shouldn't patch and return an error response")
    default void testPatchAllWithNoEntity() {

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .contentType(ContentType.JSON)
            .when()
            .patch()
            .then();

        // THEN
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
            REQUIRED_BODY_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN an empty entity,"
        + " WHEN patching all,"
        + " THEN the controller should patch and return a valid response with "
        + "the patched object")
    default void testPatchAllWithEmptyEntity() throws Exception {

        // GIVEN
        final E savedEntity = createAndSaveEntity();
        final I savedEntityId = Objects.requireNonNull(savedEntity.getId());
        final String idString;

        if (savedEntityId instanceof final Number number) {

            idString = number.toString();
        } else {

            idString = "\"" + savedEntityId + "\"";
        }

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body("[{\"id\" : " + idString + "}]")
            .when()
            .patch()
            .then();

        // THEN
        final DataPresentation<I> expectedData
            = BeanCreatorHelper.getEmptyBean(getUtil().getEntityClass());
        ReflectionTestUtils.setField(expectedData, "id", savedEntityId);
        assertResponse(response, HttpStatus.OK, ContentType.JSON, Map.of(),
            List.of(expectedData), false);
    }
}