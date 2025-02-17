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
import java.lang.reflect.Field;
import java.util.Objects;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.test.functional.SuperFT;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for download file functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link DownloadFileFT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link DownloadFileFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements DownloadFileFT&lt;YourEntity, YourIdType&gt; {
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
public interface DownloadFileFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperFT<E, I>, SuperFileTest<E, I> {

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN downloading a file,"
        + " THEN the controller should return a valid response with "
        + "the corresponding file")
    default void testDownloadFile() throws Exception {

        // GIVEN
        final E saveEntity = createAndSaveEntity();
        final I savedEntityId = saveEntity.getId();

        for (final String fileFieldName: getFileFieldNames()) {

            final Field fileField = ReflectionUtils
                .getField(getUtil().getEntityClass(), fileFieldName);
            final File file = (File) fileField.get(saveEntity);
            final Resource expectedResult = new ByteArrayResource(
                Objects.requireNonNull(file.getBytes())) {

                @Override
                public String getFilename() {

                    return file.getFilename();
                }
            };

            // WHEN
            final ValidatableResponse response = RestAssured.given()
                .when()
                .get("/" + savedEntityId + "/" + fileFieldName)
                .then();

            // THEN
            response.assertThat()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .contentType(
                    ContentType.fromContentType("application/octet-stream"))
                .assertThat()
                .header("Content-Disposition",
                    Matchers.containsString("attachment; filename=\""
                        + expectedResult.getFilename()
                        + "\""));
            assertThat(response.extract().asByteArray())
                .isEqualTo(expectedResult.getContentAsByteArray());
        }
    }

    @Test
    @DisplayName("GIVEN no ID,"
        + " WHEN downloading a file,"
        + " THEN the controller should return an error response")
    default void testDownloadFileWithNoId() {

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().get(ENCODE_SPACE).then();
        // Need to use %20 because rest-assured remove the space

        // THEN
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
            ID_MISSING_ERROR_MESSAGE);
    }
}
