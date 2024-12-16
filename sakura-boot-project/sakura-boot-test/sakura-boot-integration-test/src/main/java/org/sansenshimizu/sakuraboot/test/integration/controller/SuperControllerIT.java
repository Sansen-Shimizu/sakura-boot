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

package org.sansenshimizu.sakuraboot.test.integration.controller;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.atteo.evo.inflector.English;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.test.SuperDataITUtil;
import org.sansenshimizu.sakuraboot.test.SuperIT;
import org.sansenshimizu.sakuraboot.test.SuperITUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The super interface for controller integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link SuperControllerIT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     SuperControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
 *
 *     private final YourUtil util = new YourUtil();
 *
 *     private final MockMvc mockMvc;
 *
 *     private final ObjectMapper objectMapper;
 *
 *     &#064;MockBean
 *     private YourService service;
 *
 *     &#064;Autowired
 *     YourIT(final MockMvc mockMvc, final ObjectMapper objectMapper) {
 *
 *         this.mockMvc = mockMvc;
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
 *     public MockMvc getMockMvc() {
 *
 *         return mockMvc;
 *     }
 *
 *     &#064;Override
 *     public ObjectMapper getObjectMapper() {
 *
 *         return objectMapper;
 *     }
 *
 *     &#064;Override
 *     public YourService getService() {
 *
 *         return service;
 *     }
 *
 *     &#064;Override
 *     public String getBasePath() {
 *
 *         return "api/yourPath";
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type use in the service layer.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <D> The {@link DataPresentation} type use to map the given JSON to an
 *             entity or DTO.
 * @author     Malcolm Rozé
 * @see        SuperIT
 * @see        SuperITUtil
 * @since      0.1.0
 */
public interface SuperControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends SuperIT<E, I> {

    /**
     * The base URI use by the server in this test.
     */
    String BASE_URI = "http://localhost";

    /**
     * The string for the content path.
     */
    String CONTENT_PATH = "$.content";

    /**
     * The string for the embedded path.
     */
    String EMBEDDED_PATH = "$._embedded.";

    /**
     * The string for the links.
     */
    String LINKS_STRING = "_links";

    /**
     * The string for the links path.
     */
    String LINKS_PATH = "$." + LINKS_STRING;

    /**
     * The string for the page path.
     */
    String PAGE_PATH = "$.page";

    /**
     * Pattern for camel case.
     */
    Pattern CAMEL_CASE_PATTERN = Pattern.compile(
        "([\\p{Lower}\\d])(\\p{Upper})", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * Return a util class of type {@link SuperDataITUtil}.
     *
     * @return A util class for testing.
     */
    @Override
    SuperDataITUtil<E, I, D> getUtil();

    /**
     * Return the mock {@link MockMvc}.
     *
     * @return The mock {@link MockMvc}.
     */
    MockMvc getMockMvc();

    /**
     * Return a mock service that extends {@link SuperService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @SuppressWarnings("EmptyMethod")
    SuperService<E, I> getService();

    /**
     * Return the {@link ObjectMapper}.
     *
     * @return The {@link ObjectMapper}.
     */
    ObjectMapper getObjectMapper();

    /**
     * The url path of the controller.
     *
     * @return The url.
     */
    default String getUrl() {

        return BASE_URI + "/" + getBasePath();
    }

    /**
     * The base url path of the controller.
     * For integration test the context path is unnecessary.
     *
     * @return The base path.
     */
    default String getBasePath() {

        return English.plural(CAMEL_CASE_PATTERN
            .matcher(getUtil().getEntityClass().getSimpleName())
            .replaceAll("$1-$2")
            .toLowerCase(Locale.ENGLISH));
    }

    /**
     * Get JSON string from an object.
     *
     * @param  expectedResult The expected result object.
     * @return                The JSON string.
     */
    default String getJsonString(final Object expectedResult)
        throws JsonProcessingException {

        return getObjectMapper().writeValueAsString(expectedResult);
    }

    /**
     * The fields that need to be ignored in the assert.
     *
     * @return A list of fields that need to be ignored in the assert.
     */
    default List<String> fieldsToIgnoreInAssert() {

        return List.of();
    }

    /**
     * Assert the body of the result actions.
     *
     * @param result          The result actions.
     * @param jsonPath        The JSON path.
     * @param expectedObjects List of expected objects.
     * @param ignoredFields   The ignored fields.
     * @param <T>             The type of the expected objects.
     */
    @SuppressWarnings("java:S923")
    default <T> void assertArrayInBody(
        final ResultActions result, final String jsonPath,
        final List<T> expectedObjects, final String... ignoredFields)
        throws Exception {

        final String body
            = result.andReturn().getResponse().getContentAsString();

        final List<T> actualArray
            = ((Collection<?>) JsonPath.read(body, jsonPath)).stream()
                .map(e -> getObjectFromJson(expectedObjects, e))
                .toList();

        assertThat(actualArray).usingRecursiveComparison()
            .ignoringFields(ignoredFields)
            .ignoringFields(fieldsToIgnoreInAssert().toArray(String[]::new))
            .withComparatorForType(SuperControllerIT::temporalComparator,
                Temporal.class)
            .usingOverriddenEquals()
            .isEqualTo(expectedObjects);
    }

    private static
        int temporalComparator(final Temporal a, final Temporal exp) {

        if (a instanceof final ZonedDateTime zonedDateTime
            && exp instanceof final ZonedDateTime expZonedDateTime) {

            return ChronoZonedDateTime.timeLineOrder()
                .compare(zonedDateTime, expZonedDateTime);
        } else if (a instanceof final OffsetDateTime offsetDateTime
            && exp instanceof final OffsetDateTime expOffsetDateTime) {

            return OffsetDateTime.timeLineOrder()
                .compare(offsetDateTime, expOffsetDateTime);
        } else {

            @SuppressWarnings("unchecked")
            final Comparable<? super Temporal> comparable
                = (Comparable<? super Temporal>) a;
            return comparable.compareTo(exp);
        }
    }

    private <T> T getObjectFromJson(
        final List<T> expectedObjects, final Object json) {

        if (expectedObjects.isEmpty()) {

            throw new RuntimeException("No expected object was found.");
        }

        try {

            @SuppressWarnings("unchecked")
            final T object
                = (T) getObjectMapper().readValue(getJsonString(json),
                    expectedObjects.get(0).getClass());
            return object;
        } catch (final JsonProcessingException ex) {

            throw new RuntimeException(ex);
        }
    }
}
