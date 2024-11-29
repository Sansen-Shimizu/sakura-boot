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

package org.sansenshimizu.sakuraboot.test.functional;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.specification.api.presentation.CriteriaController;
import org.sansenshimizu.sakuraboot.test.SuperIT;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.hypermedia.HypermediaFTUtil;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;

/**
 * The super interface for all functional tests. This interface has no test but
 * can be used to simplify the access to the {@link BasicFTUtil} interface and
 * do functional test.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link BasicFT}, follow
 * these steps:
 * </p>
 * <p>
 * Implements the {@link BasicFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT implements BasicFT&lt;YourEntity, YourIdType&gt; {
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
 * @see        SuperIT
 * @see        BasicFTUtil
 * @since      0.1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public interface BasicFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperIT<E, I> {

    /**
     * The base URI use by the server in this test.
     */
    String BASE_URI = "http://localhost";

    /**
     * The error message for a bad media type.
     */
    String MEDIA_TYPE_ERROR_MESSAGE
        = "null media type is not supported. Supported media types are ";

    /**
     * The error message for required request body.
     */
    String REQUIRED_BODY_ERROR_MESSAGE = "Required request body is missing";

    /**
     * The error message for validation.
     */
    String VALIDATION_ERROR_MESSAGE = "Validation error ";

    /**
     * The space encoding.
     */
    String ENCODE_SPACE = "/%20";

    /**
     * The error message for id missing.
     */
    String ID_MISSING_ERROR_MESSAGE
        = "The property: id is not applicable for the value: %20. "
            + "The supported type is: ";

    /**
     * Retrieves the application context.
     *
     * @return the application context
     */
    ApplicationContext getApplicationContext();

    /**
     * The port use by the server in this test.
     *
     * @return The local port.
     */
    int getPort();

    /**
     * Return a Util class of type {@link BasicFTUtil}.
     *
     * @return A Util class for testing.
     */
    @Override
    BasicFTUtil<E, I> getUtil();

    /**
     * The base url path of the controller.
     *
     * @return The base path.
     */
    default String getBasePath() {

        return "/" + getUtil().getPath();
    }

    /**
     * Get an object mapper for converting an object to JSON.
     *
     * @return An objectMapper.
     */
    ObjectMapper getObjectMapper();

    /**
     * If the stackTrace must be present in the response error.
     * Default : {@code false}.
     *
     * @return {@code true} if the stackTrace must be present in the response
     *         error, {@code false} otherwise.
     */
    default boolean showStackTrace() {

        return false;
    }

    /**
     * The setUp function call before every test.
     */
    @SuppressWarnings("java:S2696")
    @BeforeEach
    default void setUp() {

        RestAssured.baseURI = BASE_URI;
        RestAssured.port = getPort();
        RestAssured.basePath = getBasePath();
    }

    /**
     * Return a repository class that extends {@link SuperRepository}.
     *
     * @return A repository class use in integration test.
     */
    default SuperRepository<E, I> getRepository() {

        final Repositories repositories
            = new Repositories(getApplicationContext());
        @SuppressWarnings("unchecked")
        final SuperRepository<E,
            I> repository = (SuperRepository<E, I>) repositories
                .getRepositoryFor(getUtil().getEntityClass())
                .orElseThrow();
        return repository;
    }

    /**
     * The reset function call after every test.
     */
    @AfterEach
    default void reset() {

        final Repositories repositories
            = new Repositories(getApplicationContext());
        final List<Class<?>> orderedRepositories = new LinkedList<>();

        for (final Class<?> clazz: repositories) {

            final List<Class<?>> fieldsClass
                = RelationshipUtils.getRelationClass(clazz);

            final ListIterator<Class<?>> iterator
                = orderedRepositories.listIterator();
            boolean notAdded = true;

            while (iterator.hasNext()) {

                if (fieldsClass.contains(iterator.next())) {

                    iterator.previous();
                    iterator.add(clazz);
                    notAdded = false;
                    break;
                }
            }

            if (notAdded) {

                orderedRepositories.addLast(clazz);
            }
        }
        orderedRepositories.stream()
            .map(clazz -> repositories.getRepositoryFor(clazz).orElse(null))
            .filter(CrudRepository.class::isInstance)
            .map(CrudRepository.class::cast)
            .forEach(CrudRepository<E, I>::deleteAll);
    }

    /**
     * Create and save a new entity for testing purpose.
     *
     * @return The saved entity.
     */
    default E createAndSaveEntity() {

        return getRepository().save(getUtil().getEntityWithoutId());
    }

    @Test
    @DisplayName("GIVEN a cache configuration, "
        + "WHEN initialize the context, "
        + "THEN the bean shouldn't be null and caches are created")
    default void testCacheConfigurationInitialization() {

        if (getUtil() instanceof final CachingFTUtil<?, ?> cachingUtil) {

            // THEN
            assertThat(cachingUtil.getCacheManager()).isNotNull();
            assertThat(cachingUtil.getCacheManager())
                .isInstanceOf(JCacheCacheManager.class);
            assertThat(((AbstractTransactionSupportingCacheManager) cachingUtil
                .getCacheManager()).isTransactionAware()).isTrue();

            cachingUtil.assertCacheCreated();
        }
    }

    /**
     * The function creates a hyperlink reference for an entity, including self
     * and collection links, as well as any additional links specified by a
     * {@link HypermediaFTUtil#addOtherLink(Object, String)} object.
     *
     * @param  path           The path parameter is a string representing the
     *                        base URL path.
     * @param  body           The response body of the request.
     * @param  hypermediaUtil An HypermediaFTUtil.
     * @return                The method is returning a string that represents a
     *                        JSON object containing hypermedia links.
     */
    default String createHrefForEntity(
        final String path, final DataPresentation<I> body,
        final HypermediaFTUtil<?, I> hypermediaUtil) {

        final List<String> relations = new ArrayList<>();
        RelationshipUtils.doWithIdRelationFields(body,
            (final Field field, final Object id) -> {

                if (id != null
                    && !field.isAnnotationPresent(JsonIgnore.class)) {

                    final String relationPath = RestAssured.baseURI
                        + ":"
                        + RestAssured.port
                        + getBasePath().replace(getClassName(body.getClass()),
                            getClassName(field, false));
                    relations.add(relationPath + "/" + id);
                }
            }, (final Field field, final Collection<?> collection) -> {

                if (field.isAnnotationPresent(JsonIgnore.class)) {

                    return;
                }
                final Class<?> controllerClass;

                try {

                    controllerClass = Class.forName(body.getClass()
                        .getName()
                        .replace(getUtil().getEntityPackageName(),
                            getUtil().getControllerPackageName())
                        .replace(getUtil().getDtoPackageName(),
                            getUtil().getControllerPackageName())
                        .replace("Dto", "")
                        + "Controller");
                } catch (final ClassNotFoundException e) {

                    throw new RuntimeException(
                        "The controller class must follow the name convention."
                            + " (EntityName + Controller)",
                        e);
                }
                final String relationPath = RestAssured.baseURI
                    + ":"
                    + RestAssured.port
                    + getBasePath().replace(getClassName(body.getClass()),
                        getClassName(field, true));

                if (CriteriaController.class
                    .isAssignableFrom(controllerClass)) {

                    final String idsFilter = collection.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.joining(",",
                            relationPath + "?id" + ".in=", ""));
                    relations.add(idsFilter);
                } else {

                    relations.add(relationPath);
                }
            }, getUtil().getGlobalSpecification());

        final String relationshipPrefix;
        final String relationshipSuffix;

        if (relations.isEmpty()) {

            relationshipPrefix = "";
            relationshipSuffix = "";
        } else {

            if (relations.size() == 1) {

                relationshipPrefix = ", \"related\":";
                relationshipSuffix = "";
            } else {

                relationshipPrefix = ", \"related\":[";
                relationshipSuffix = "]";
            }
        }

        return ", \"_links\":{\"self\":{\"href\":\""
            + path
            + "/"
            + body.getId()
            + "\"}, \"collection\":{\"href\":\""
            + path
            + "\"}"
            + relations.stream()
                .map(r -> "{\"href\":\"" + r + "\"}")
                .collect(Collectors.joining(", ", relationshipPrefix,
                    relationshipSuffix))
            + hypermediaUtil.addOtherLink(body, path)
            + "}";
    }

    private static
        String getClassName(final Field field, final boolean plural) {

        if (plural) {

            return field.getName().replace("Id", "");
        }
        return English.plural(field.getName().replace("Id", ""));
    }

    private static String getClassName(final Class<?> clazz) {

        return English.plural(
            StringUtils.uncapitalize(clazz.getSimpleName()).replace("Dto", ""));
    }

    /**
     * The function creates a JSON string containing an href link with the given
     * path and parameters.
     *
     * @param  path       Is a string representing the base URL.
     * @param  parameters The parameters given to the request.
     * @return            The method is returning a JSON string that contains an
     *                    "href" field with a value that is constructed using
     *                    the provided path and parameters.
     */
    default String createHrefWithParam(
        final String path, final Map<String, String> parameters) {

        final StringJoiner params = new StringJoiner("&", "?", "");

        for (final Map.Entry<String, String> param: parameters.entrySet()) {

            params.add(param.getKey() + "=" + param.getValue());
        }
        return "{\"href\":\"" + path + params + "\"}";
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
     * Assert for testing the created response by rest assured.
     *
     * @param  response                The response creates in the test.
     * @param  status                  The expected status of the response.
     * @param  contentType             The expected content type of the body.
     * @param  headers                 The expected headers in the response as a
     *                                 map.
     * @param  bodyContain             Contain the expected body of the
     *                                 response.
     * @param  assertLinks             If the links should be asserted.
     * @throws JsonProcessingException if the bodyContains can't be converted to
     *                                 JSON.
     */
    default void assertResponse(
        final ValidatableResponse response, final HttpStatus status,
        final ContentType contentType, final Map<String, String> headers,
        final DataPresentation<I> bodyContain, final boolean assertLinks)
        throws JsonProcessingException {

        final String links;

        if (assertLinks
            && getUtil() instanceof final HypermediaFTUtil<?,
                I> hypermediaUtil) {

            final String path
                = RestAssured.baseURI + ":" + RestAssured.port + getBasePath();
            links = createHrefForEntity(path, bodyContain, hypermediaUtil);
        } else {

            links = "";

            if (getUtil() instanceof HypermediaFTUtil) {

                response.assertThat().body("_links", Matchers.notNullValue());
            }
        }

        final String objectJson = StringUtils
            .chop(getObjectMapper().writeValueAsString(bodyContain));
        final JsonPath expectedJson = new JsonPath(objectJson + links + "}");

        response.assertThat()
            .body("", allOf(expectedJson.<String, Object>getMap("")
                .entrySet()
                .stream()
                .filter(
                    entry -> !fieldsToIgnoreInAssert().contains(entry.getKey())
                        && entry.getValue() != null)
                .map(entry -> hasEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())));
        response.assertThat()
            .statusCode(status.value())
            .assertThat()
            .contentType(contentType);

        for (final Map.Entry<String, String> header: headers.entrySet()) {

            response.assertThat()
                .header(header.getKey(),
                    Matchers.containsString(header.getValue()));
        }
    }

    /**
     * Assert for testing the created response by rest assured.
     *
     * @param  response                The response creates in the test.
     * @param  status                  The expected status of the response.
     * @param  contentType             The expected content type of the body.
     * @param  headers                 The expected headers in the response as a
     *                                 map.
     * @param  bodyContains            Contains the expected body of the
     *                                 response as a page.
     * @param  param                   The parameters given to the request.
     * @throws JsonProcessingException if the bodyContains can't be converted to
     *                                 JSON.
     */
    default void assertResponse(
        final ValidatableResponse response, final HttpStatus status,
        final ContentType contentType, final Map<String, String> headers,
        final Page<DataPresentation<I>> bodyContains,
        final Map<String, String> param)
        throws JsonProcessingException {

        if (getUtil() instanceof final HypermediaFTUtil<?, I> hypermediaUtil) {

            final String path
                = RestAssured.baseURI + ":" + RestAssured.port + getBasePath();
            final String pageString = "page";

            final String currentPageableJson
                = "\"self\":" + createHrefWithParam(path, param);
            param.put(pageString, "0");
            final String firstPageableJson
                = "\"first\":" + createHrefWithParam(path, param);
            param.put(pageString, "" + (bodyContains.getTotalPages() - 1));
            final String lastPageableJson
                = "\"last\":" + createHrefWithParam(path, param);
            param.put(pageString,
                "" + bodyContains.previousOrFirstPageable().getPageNumber());
            final String previousPageableJson
                = "\"previous\":" + createHrefWithParam(path, param);
            param.put(pageString,
                "" + bodyContains.nextOrLastPageable().getPageNumber());
            final String nextPageableJson
                = "\"next\":" + createHrefWithParam(path, param);
            final String pageJson = "\"page\":{\"size\":"
                + bodyContains.getSize()
                + ", \"totalElements\":"
                + bodyContains.getTotalElements()
                + ", \"totalPages\":"
                + bodyContains.getTotalPages()
                + ", \"number\":"
                + bodyContains.getNumber()
                + "}";
            final StringJoiner linksJoiner
                = new StringJoiner(", ", "\"_links\":{", "}");

            if (!bodyContains.hasPrevious() && !bodyContains.hasNext()) {

                linksJoiner.add(currentPageableJson);
            } else {

                linksJoiner.add(firstPageableJson);

                if (bodyContains.hasPrevious()) {

                    linksJoiner.add(previousPageableJson);
                }
                linksJoiner.add(currentPageableJson);

                if (bodyContains.hasNext()) {

                    linksJoiner.add(nextPageableJson);
                }
                linksJoiner.add(lastPageableJson);
            }
            final StringJoiner stringJoiner = new StringJoiner(", ",
                "{\"_embedded\":{\""
                    + hypermediaUtil.entityCollectionName()
                    + "\":[",
                "]}, " + linksJoiner + ", " + pageJson + "}");

            final String embeddedString = "_embedded";

            for (final DataPresentation<I> bodyContain: bodyContains
                .getContent()) {

                final String links;
                links = createHrefForEntity(path, bodyContain, hypermediaUtil);

                final String objectJson = StringUtils
                    .chop(getObjectMapper().writeValueAsString(bodyContain))
                    + links
                    + "}";
                stringJoiner.add(objectJson);

                final JsonPath expectedJson = new JsonPath(objectJson);

                response.assertThat()
                    .body(
                        embeddedString
                            + "."
                            + hypermediaUtil.entityCollectionName(),
                        hasItem(allOf(expectedJson.<String, Object>getMap("")
                            .entrySet()
                            .stream()
                            .filter(entry -> !fieldsToIgnoreInAssert()
                                .contains(entry.getKey())
                                && entry.getValue() != null)
                            .map(entry -> hasEntry(entry.getKey(),
                                entry.getValue()))
                            .collect(Collectors.toList()))));
            }

            final JsonPath expectedJson = new JsonPath(stringJoiner.toString());

            response.assertThat()
                .body("", allOf(expectedJson.<String, Object>getMap("")
                    .entrySet()
                    .stream()
                    .filter(entry -> !embeddedString.equals(entry.getKey()))
                    .map((final Map.Entry<String, Object> entry) -> hasEntry(
                        entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList())));
        } else {

            final Pageable pageable = bodyContains.getPageable();
            final Sort sort = pageable.getSort();
            final JsonPath expectedJson = new JsonPath("{\"content\":"
                + getObjectMapper()
                    .writeValueAsString(bodyContains.getContent())
                + ", \"pageable\":{\"pageNumber\":"
                + pageable.getPageNumber()
                + ", \"pageSize\":"
                + pageable.getPageSize()
                + ", \"sort\":{\"empty\":"
                + sort.isEmpty()
                + ", \"sorted\":"
                + sort.isSorted()
                + ", \"unsorted\":"
                + sort.isUnsorted()
                + "}, \"offset\":"
                + pageable.getOffset()
                + ", \"paged\":"
                + pageable.isPaged()
                + ", \"unpaged\":"
                + pageable.isUnpaged()
                + "}, \"totalElements\":"
                + bodyContains.getTotalElements()
                + ", \"totalPages\":"
                + bodyContains.getTotalPages()
                + ", \"last\":"
                + bodyContains.isLast()
                + ", \"size\":"
                + bodyContains.getSize()
                + ", \"number\":"
                + bodyContains.getNumber()
                + ", \"sort\":{\"empty\":"
                + bodyContains.getSort().isEmpty()
                + ", \"sorted\":"
                + bodyContains.getSort().isSorted()
                + ", \"unsorted\":"
                + bodyContains.getSort().isUnsorted()
                + "}, \"numberOfElements\":"
                + bodyContains.getNumberOfElements()
                + ", \"first\":"
                + bodyContains.isFirst()
                + ", \"empty\":"
                + bodyContains.isEmpty()
                + "}");

            response.assertThat()
                .body("", allOf(expectedJson.<String, Object>getMap("")
                    .entrySet()
                    .stream()
                    .filter(entry -> !fieldsToIgnoreInAssert()
                        .contains(entry.getKey()) && entry.getValue() != null)
                    .map(entry -> hasEntry(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList())));

            response.assertThat()
                .statusCode(status.value())
                .assertThat()
                .contentType(contentType);
        }

        for (final Map.Entry<String, String> header: headers.entrySet()) {

            response.assertThat()
                .header(header.getKey(),
                    Matchers.containsString(header.getValue()));
        }
    }

    /**
     * Assert for testing the created error response by rest assured.
     *
     * @param response             The response creates in the test.
     * @param status               The expected status of the response.
     * @param errorMessageContains The expected error message of the response.
     */
    default void assertErrorResponse(
        final ValidatableResponse response, final HttpStatus status,
        final String errorMessageContains) {

        response.assertThat()
            .contentType(ContentType.JSON)
            .assertThat()
            .body("status", Matchers.equalTo(status.value()))
            .assertThat()
            .body("message",
                StringContains.containsStringIgnoringCase(errorMessageContains))
            .assertThat()
            .body("description",
                StringContains
                    .containsStringIgnoringCase("uri=" + getBasePath()))
            .assertThat()
            .statusCode(status.value());

        final String stackTraceString = "stackTrace";

        if (showStackTrace()) {

            response.assertThat()
                .body(stackTraceString,
                    Matchers.not(Matchers.emptyOrNullString()));
        } else {

            response.assertThat()
                .body(stackTraceString, Matchers.emptyOrNullString());
        }
    }

    /**
     * Removes relational entities if needed.
     *
     * @param applicationContext  The application context.
     * @param globalSpecification The {@link GlobalSpecification}.
     * @param data                The DataPresentation instance that needs
     *                            to be updated.
     */
    @SuppressWarnings("ReturnOfNull")
    static void removeRelationshipsIfNeeded(
        final ApplicationContext applicationContext,
        final GlobalSpecification globalSpecification,
        @Nullable final Object data) {

        if (data == null) {

            return;
        }

        final Class<?> entityClass;

        try {

            entityClass = Class.forName(data.getClass()
                .getName()
                .replace(globalSpecification.dtoPackage(),
                    globalSpecification.entityPackage())
                .replace("Dto", ""));
        } catch (final ClassNotFoundException e) {

            throw new RuntimeException(e);
        }
        final Repositories repositories = new Repositories(applicationContext);
        final Object repository
            = repositories.getRepositoryFor(entityClass).orElse(null);

        if (repository instanceof FetchRelationshipRepository<?, ?>) {

            RelationshipUtils.doWithRelationFields(data,
                (field, relationship) -> removeRelationshipsIfNeeded(
                    applicationContext, globalSpecification, relationship),
                globalSpecification);
        } else {

            RelationshipUtils.updateRelationFields(data, (field, value) -> null,
                (field, collection) -> Set.of(), globalSpecification);
        }
    }
}
