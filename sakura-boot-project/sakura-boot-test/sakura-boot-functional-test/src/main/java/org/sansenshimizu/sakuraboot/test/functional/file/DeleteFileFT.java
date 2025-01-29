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
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.test.functional.SuperFT;

/**
 * The interface for delete file functional tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link DeleteFileFT},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteFileFT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFT //
 *     implements DeleteFileFT&lt;YourEntity, YourIdType&gt; {
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
public interface DeleteFileFT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperFT<E, I>, SuperFileTest<E, I> {

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN deleting a file,"
        + " THEN the controller should return a no-content response")
    default void testDeleteFile() {

        // GIVEN
        final I savedEntityId = createAndSaveEntity().getId();

        for (final String fileFieldName: getFileFieldNames()) {

            // WHEN
            final ValidatableResponse response = RestAssured.given()
                .when()
                .delete("/" + savedEntityId + "/" + fileFieldName)
                .then();

            // THEN
            response.assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .assertThat()
                .body(Matchers.emptyString());
        }
    }

    @Test
    @DisplayName("GIVEN no ID,"
        + " WHEN deleting a file,"
        + " THEN the controller should return an error response")
    default void testDeleteFileWithNoId() {

        // WHEN
        final ValidatableResponse response
            = RestAssured.given().when().delete(ENCODE_SPACE).then();
        // Need to use %20 because rest-assured remove the space

        // THEN
        assertErrorResponse(response, HttpStatus.BAD_REQUEST,
            ID_MISSING_ERROR_MESSAGE);
    }
}
