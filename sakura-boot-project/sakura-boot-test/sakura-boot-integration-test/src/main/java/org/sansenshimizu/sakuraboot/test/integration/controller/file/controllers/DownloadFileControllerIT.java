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
import java.lang.reflect.Field;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.business.services.DownloadFileService;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.file.api.presentation.controllers.DownloadFileController;
import org.sansenshimizu.sakuraboot.test.file.api.SuperFileTest;
import org.sansenshimizu.sakuraboot.test.integration.controller.SuperControllerIT;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

/**
 * The interface for DownloadFile controller integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link DownloadFileControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DownloadFileControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     DownloadFileControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 * @see        DownloadFileController
 * @see        SuperControllerIT
 * @see        SuperFileTest
 * @since      0.1.2
 */
public interface DownloadFileControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SuperControllerIT<E, I, D>, SuperFileTest<E, I> {

    /**
     * Return a mock service that extends {@link DownloadFileService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    DownloadFileService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID and fileFieldName,"
        + " WHEN downloading a file,"
        + " THEN the controller should return a valid response with "
        + "the corresponding file")
    default void testDownloadFile() throws Exception {

        // GIVEN
        final I validId = getValidId();
        final E entityWithId = getUtil().getEntity();

        for (final String fileFieldName: getFileFieldNames()) {

            final Field fileField = ReflectionUtils
                .getField(getUtil().getEntityClass(), fileFieldName);
            final File file = (File) fileField.get(entityWithId);
            final Resource expectedResult = new ByteArrayResource(
                Objects.requireNonNull(file.getBytes())) {

                @Override
                public String getFilename() {

                    return file.getFilename();
                }
            };
            given(getService().downloadFile(any(), any()))
                .willReturn(expectedResult);

            // WHEN
            final ResultActions result
                = getMockMvc().perform(MockMvcRequestBuilders
                    .get(getUrl() + "/" + validId + "/" + fileFieldName));

            // THEN
            result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(header().string("Content-Disposition",
                    containsString("attachment; filename=\""
                        + expectedResult.getFilename()
                        + "\"")))
                .andExpect(MockMvcResultMatchers.content()
                    .bytes(expectedResult.getContentAsByteArray()));
        }
    }
}
