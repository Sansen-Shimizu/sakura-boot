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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.functional.SuperFT;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.mapper.MapperFTUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for find all functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link FindAllFT},
 * follow
 * these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT implements FindAllFT&lt;YourEntity, YourIdType&gt; {
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
 * @since      0.1.0
 */
public interface FindAllFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperFT<E, I> {

    @Test
    @DisplayName("GIVEN a pageable request,"
        + " WHEN finding all,"
        + " THEN the controller should return a valid response with the page")
    default void testFindAll() throws Exception {

        // GIVEN
        final E savedEntity = createAndSaveEntity();
        final E otherSavedEntity = createAndSaveEntity();
        final Pageable pageable = Pageable.ofSize(1);
        final Map<String, String> paramPageable = new TreeMap<>();
        paramPageable.put("page", "" + pageable.getPageNumber());
        paramPageable.put("size", "" + pageable.getPageSize());

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().params(paramPageable).when().get().then();

        // THEN
        final DataPresentation<I> expectedData;
        final DataPresentation<I> otherExpectedData;

        if (getUtil() instanceof final MapperFTUtil<?, I, ?> mapperUtil) {

            expectedData = mapperUtil.toDto(savedEntity);
            otherExpectedData = mapperUtil.toDto(otherSavedEntity);
        } else {

            expectedData = savedEntity;
            otherExpectedData = otherSavedEntity;
        }
        final Page<DataPresentation<I>> expectedPage
            = new PageImpl<>(List.of(Objects.requireNonNull(expectedData)),
                Pageable.ofSize(1), 2);
        assertResponse(response, HttpStatus.OK, ContentType.JSON, Map.of(),
            expectedPage, paramPageable);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            for (final String cacheName: cachingUtil.getCacheNames()) {

                assertThat(
                    cachingUtil.getCacheManager().getCache(cacheName + "All"))
                    .isNotNull()
                    .extracting(
                        cache -> cache.get(pageable.toString(), Page.class))
                    .extracting(Slice<DataPresentation<I>>::getContent)
                    .asInstanceOf(InstanceOfAssertFactories.LIST)
                    .contains(expectedData)
                    .doesNotContain(otherExpectedData);
            }
        }
    }
}
