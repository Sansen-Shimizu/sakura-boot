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

package org.sansenshimizu.sakuraboot.test.integration.controller.basic.controllers;

import java.io.Serializable;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.FindAllController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;
import org.sansenshimizu.sakuraboot.test.integration.controller.SuperControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.hypermedia.HypermediaIT;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The interface for find all controller integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link FindAllControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     FindAllControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 *         return "yourPath";
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
 * @see        FindAllController
 * @see        SuperControllerTest
 * @since      0.1.0
 */
public interface FindAllControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends SuperControllerIT<E, I, D> {

    /**
     * Return a mock service that extends {@link FindAllService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    FindAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN a pageable request,"
        + " WHEN finding all,"
        + " THEN the controller should return a valid response with the page")
    default void testFindAllWithPageable() throws Exception {

        // GIVEN
        final D dataWithId = getUtil().getData();
        final Pageable pageable = Pageable.ofSize(1);
        final MultiValueMap<String, String> paramPageable
            = new LinkedMultiValueMap<>();
        paramPageable.add("page", "" + pageable.getPageNumber());
        paramPageable.add("size", "" + pageable.getPageSize());
        given(getService().findAll(any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(dataWithId)));

        // WHEN
        final ResultActions result = getMockMvc().perform(
            MockMvcRequestBuilders.get(getUrl()).params(paramPageable));

        // THEN
        final String contentPath;

        if (this instanceof final HypermediaIT<?, ?, ?> hypermediaIT) {

            contentPath = EMBEDDED_PATH + hypermediaIT.entityCollectionName();
        } else {

            contentPath = CONTENT_PATH;
        }
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
        assertArrayInBody(result, contentPath, List.of(dataWithId),
            LINKS_STRING);

        // noinspection InstanceofIncompatibleInterface
        if (getUtil() instanceof HypermediaIT) {

            result
                .andExpect(MockMvcResultMatchers.jsonPath(LINKS_PATH,
                    Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath(PAGE_PATH,
                    Matchers.notNullValue()));
        }
    }

    @Test
    @DisplayName("GIVEN no pageable request,"
        + " WHEN finding all,"
        + " THEN the controller should return a valid response with the page")
    default void testFindAll() throws Exception {

        // GIVEN
        final D dataWithId = getUtil().getData();
        final D otherDataWithId = getUtil().getDifferentData();
        given(getService().findAll(any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(dataWithId, otherDataWithId)));

        // WHEN
        final ResultActions result
            = getMockMvc().perform(MockMvcRequestBuilders.get(getUrl()));

        // THEN
        final String contentPath;

        if (this instanceof final HypermediaIT<?, ?, ?> hypermediaIT) {

            contentPath = EMBEDDED_PATH + hypermediaIT.entityCollectionName();
        } else {

            contentPath = CONTENT_PATH;
        }
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
        assertArrayInBody(result, contentPath,
            List.of(dataWithId, otherDataWithId), LINKS_STRING);

        // noinspection InstanceofIncompatibleInterface
        if (getUtil() instanceof HypermediaIT) {

            result
                .andExpect(MockMvcResultMatchers.jsonPath(LINKS_PATH,
                    Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath(PAGE_PATH,
                    Matchers.notNullValue()));
        }
    }
}
