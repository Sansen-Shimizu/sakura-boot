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

package org.sansenshimizu.sakuraboot.test.functional.file;

import java.io.Serializable;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.MultiPartSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.test.functional.SuperFT;

/**
 * The interface for upload file functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link UploadFileFT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link UploadFileFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements UploadFileFT&lt;YourEntity, YourIdType&gt; {
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
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface UploadFileFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperFT<E, I>, SuperFileTest<E, I> {

    /**
     * A test file name.
     */
    String FILE_NAME = "testFile.jpg";

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN uploading a file,"
        + " THEN the controller should return a valid response with "
        + "a text message")
    default void testUploadFile() {

        // GIVEN
        final I savedEntityId = createAndSaveEntity().getId();
        final MultiPartSpecification multipartFile
            = new MultiPartSpecBuilder(FILE_NAME.getBytes()).fileName(FILE_NAME)
                .build();

        for (final String fileFieldName: getFileFieldNames()) {

            // WHEN
            final ValidatableResponse response = RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .multiPart(multipartFile)
                .when()
                .put("/" + savedEntityId + "/" + fileFieldName)
                .then();

            // THEN
            response.assertThat()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .contentType(ContentType.TEXT)
                .assertThat()
                .body(Matchers.equalTo(FILE_NAME + " uploaded successfully."));
        }
    }

    @Test
    @DisplayName("GIVEN no file and content type,"
        + " WHEN uploading a file,"
        + " THEN the controller shouldn't upload and return an error response")
    default void testUploadFileWithNoFileAndContentType() {

        // GIVEN
        final I savedEntityId = createAndSaveEntity().getId();
        final String fileFieldName = getFileFieldNames().get(0);

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .when()
            .put("/" + savedEntityId + "/" + fileFieldName)
            .then();

        // THEN
        assertErrorResponse(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            MEDIA_TYPE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN no file,"
        + " WHEN uploading a file,"
        + " THEN the controller shouldn't upload and return an error response")
    default void testUploadFileWithNoFile() {

        // GIVEN
        final I savedEntityId = createAndSaveEntity().getId();
        final String fileFieldName = getFileFieldNames().get(0);

        // WHEN
        final ValidatableResponse response = RestAssured.given()
            .contentType(ContentType.MULTIPART)
            .when()
            .put("/" + savedEntityId + "/" + fileFieldName)
            .then();

        // THEN
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
            "the request contains invalid or no multipart data");
    }
}
