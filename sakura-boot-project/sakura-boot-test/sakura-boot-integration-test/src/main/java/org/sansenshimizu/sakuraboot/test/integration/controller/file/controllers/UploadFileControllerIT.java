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

package org.sansenshimizu.sakuraboot.test.integration.controller.file.controllers;

import java.io.Serializable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.business.services.UploadFileService;
import org.sansenshimizu.sakuraboot.file.api.presentation.controllers.UploadFileController;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.test.integration.controller.SuperControllerIT;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The interface for UploadFile controller integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link UploadFileControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link UploadFileControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     UploadFileControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 * @see        UploadFileController
 * @see        SuperControllerIT
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface UploadFileControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SuperControllerIT<E, I, D>, SuperFileTest<E, I> {

    /**
     * A test file name.
     */
    String FILE_NAME = "testFile.jpg";

    /**
     * Return a mock service that extends {@link UploadFileService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    UploadFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID, fileFieldName, and file,"
        + " WHEN uploading a file,"
        + " THEN the controller should return a valid response with "
        + "a text message")
    default void testUploadFile() throws Exception {

        // GIVEN
        final I validId = getValidId();
        final MockMultipartFile multipartFile = new MockMultipartFile("file",
            FILE_NAME, "text/plain", FILE_NAME.getBytes());

        for (final String fileFieldName: getFileFieldNames()) {

            // WHEN
            final ResultActions result
                = getMockMvc().perform(MockMvcRequestBuilders
                    .multipart(HttpMethod.PUT,
                        getUrl() + "/" + validId + "/" + fileFieldName)
                    .file(multipartFile));

            // THEN
            result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                    .string(FILE_NAME + " uploaded successfully."));
            verify(getService(), times(1)).uploadFile(validId, fileFieldName,
                multipartFile);
        }
    }
}
