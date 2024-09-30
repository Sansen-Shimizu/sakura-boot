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

package org.sansenshimizu.sakuraboot.test.functional.specification;

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
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;
import org.sansenshimizu.sakuraboot.test.functional.BasicFT;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.mapper.MapperFTUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for find all by criteria functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from
 * {@link FindAllByCriteriaFT},
 * follow
 * these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllByCriteriaFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements FindAllByCriteriaFT&lt;YourEntity, YourIdType&gt; {
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
 *     YourIT(
 *     final YourUtil util,final ApplicationContext applicationContext,
 *     final ObjectMapper objectMapper){
 *
 *     this.util=util;
 *     this.applicationContext=applicationContext;
 *     this.objectMapper=objectMapper;
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
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        BasicFT
 * @since      0.1.0
 */
public interface FindAllByCriteriaFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends BasicFT<E, I> {

    /**
     * The string that represents a page.
     */
    String PAGE_STRING = "page";

    /**
     * The string that represents a size.
     */
    String SIZE_STRING = "size";

    /**
     * Gets the filter class.
     *
     * @return the filter class
     */
    default Class<F> getFilterClass() {

        return BeanCreatorHelper.findBeanClassFromInterface(getClass(),
            FindAllByCriteriaFT.class.getTypeName(), 2);
    }

    @Test
    @DisplayName("GIVEN a null filter and a pageable request,"
        + " WHEN finding all by criteria,"
        + " THEN the controller should return a valid response with the page")
    default void testFindByCriteriaWithNullFilter() throws Exception {

        // GIVEN
        final E savedEntity = createAndSaveEntity();
        final E otherSavedEntity = createAndSaveEntity();
        final Pageable pageable = Pageable.ofSize(2);
        final Map<String, String> paramPageable = new TreeMap<>();
        paramPageable.put(PAGE_STRING, "" + pageable.getPageNumber());
        paramPageable.put(SIZE_STRING, "" + pageable.getPageSize());

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
        final Page<DataPresentation<I>> expectedPage = new PageImpl<>(
            List.of(expectedData, otherExpectedData), pageable, 2);
        assertResponse(response, HttpStatus.OK, ContentType.JSON, Map.of(),
            expectedPage, paramPageable);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            final String filterString = getFilterClass().getSimpleName() + "{}";

            for (final String cacheName: cachingUtil.getCacheNames()) {

                assertThat(
                    cachingUtil.getCacheManager().getCache(cacheName + "All"))
                    .isNotNull()
                    .extracting(
                        cache -> cache.get(filterString + pageable, Page.class))
                    .extracting(Slice<DataPresentation<I>>::getContent)
                    .asInstanceOf(InstanceOfAssertFactories.LIST)
                    .contains(expectedData, otherExpectedData);
            }
        }
    }

    @Test
    @DisplayName("GIVEN a filter and a pageable request,"
        + " WHEN finding all by criteria,"
        + " THEN the controller should return a valid response with the page "
        + "that match the criteria")
    default void testFindByCriteriaWithFilter() throws Exception {

        // GIVEN
        final E savedEntity = createAndSaveEntity();
        final E otherSavedEntity = createAndSaveEntity();
        final Pageable pageable = Pageable.ofSize(2);
        final Map<String, String> paramFilterAndPageable = new TreeMap<>();
        paramFilterAndPageable.put("id.equal",
            Objects.requireNonNull(savedEntity.getId()).toString());
        paramFilterAndPageable.put(PAGE_STRING, "" + pageable.getPageNumber());
        paramFilterAndPageable.put(SIZE_STRING, "" + pageable.getPageSize());

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .params(paramFilterAndPageable)
            .when()
            .get()
            .then();

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
            = new PageImpl<>(List.of(expectedData), pageable, 1);
        assertResponse(response, HttpStatus.OK, ContentType.JSON, Map.of(),
            expectedPage, paramFilterAndPageable);

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            final String filterString = getFilterClass().getSimpleName()
                + "{id="
                + getFilterClass().getDeclaredField("id")
                    .getType()
                    .getSimpleName()
                + "{equal="
                + expectedData.getId()
                + "}}";

            for (final String cacheName: cachingUtil.getCacheNames()) {

                assertThat(
                    cachingUtil.getCacheManager().getCache(cacheName + "All"))
                    .isNotNull()
                    .extracting(
                        cache -> cache.get(filterString + pageable, Page.class))
                    .extracting(Slice<DataPresentation<I>>::getContent)
                    .asInstanceOf(InstanceOfAssertFactories.LIST)
                    .contains(expectedData)
                    .doesNotContain(otherExpectedData);
            }
        }
    }
}
