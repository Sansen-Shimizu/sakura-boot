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

package org.sansenshimizu.sakuraboot.test.integration.controller.bulk.controllers;

import java.io.Serializable;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.PatchAllService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.PatchAllController;
import org.sansenshimizu.sakuraboot.test.integration.controller.SuperControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.hypermedia.HypermediaIT;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The interface for patch all controller integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link PatchAllControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link PatchAllControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     PatchAllControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 * @see        PatchAllController
 * @see        SuperControllerIT
 * @since      0.1.2
 */
public interface PatchAllControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends SuperControllerIT<E, I, D> {

    /**
     * Return a mock service that extends {@link PatchAllService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    PatchAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN valid partial entities,"
        + " WHEN patching all,"
        + " THEN the controller should patch all and return a valid response "
        + "with the patched objects")
    default void testPatchingAll() throws Exception {

        // GIVEN
        final List<DataPresentation<I>> datasWithId
            = List.of(getUtil().getData(), getUtil().getDifferentData());
        final D partialData = getUtil().getPartialData();
        final D secondPartialData = getUtil().getPartialData();
        ReflectionTestUtils.setField(secondPartialData, "id", getInvalidId());
        final List<D> partialDatas = List.of(partialData, secondPartialData);
        given(getService().patchAll(any())).willReturn(datasWithId);

        // WHEN
        final ResultActions result
            = getMockMvc().perform(MockMvcRequestBuilders.patch(getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getJsonString(partialDatas)));

        // THEN
        final String contentPath;

        if (this instanceof final HypermediaIT<?, ?, ?> hypermediaIT) {

            contentPath = EMBEDDED_PATH + hypermediaIT.entityCollectionName();
        } else {

            contentPath = "$";
        }
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
        assertArrayInBody(result, contentPath, datasWithId, LINKS_STRING);

        if (this instanceof HypermediaIT) {

            result.andExpect(MockMvcResultMatchers.jsonPath(
                contentPath + "[*]." + LINKS_STRING, Matchers.notNullValue()));
        }
    }
}
