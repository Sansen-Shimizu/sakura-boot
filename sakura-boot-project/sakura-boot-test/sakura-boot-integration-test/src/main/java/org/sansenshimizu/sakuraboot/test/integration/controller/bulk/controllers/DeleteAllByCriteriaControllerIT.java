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

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.DeleteAllByCriteriaService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.DeleteAllByCriteriaController;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;
import org.sansenshimizu.sakuraboot.test.integration.controller.SuperControllerIT;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The interface for delete all by criteria controller integration tests.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link DeleteAllByCriteriaControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteAllByCriteriaControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     DeleteAllByCriteriaControllerIT&lt;YourEntity, YourIdType, //
 *         YourDataType, YourFilter&gt; {
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
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        DeleteAllByCriteriaController
 * @see        SuperControllerTest
 * @since      0.1.2
 */
public interface DeleteAllByCriteriaControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>, F extends FilterPresentation<?>>
    extends SuperControllerIT<E, I, D> {

    /**
     * Return a mock service that extends {@link DeleteAllByCriteriaService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    DeleteAllByCriteriaService<E, I, F> getService();

    @Test
    @DisplayName("GIVEN a filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the controller should return no content")
    default void testDeleteAllByCriteriaWithFilter() throws Exception {

        // GIVEN
        final MultiValueMap<String, String> paramFilter
            = new LinkedMultiValueMap<>();
        paramFilter.add("id.equal", getValidId().toString());

        // WHEN
        final ResultActions result = getMockMvc().perform(
            MockMvcRequestBuilders.delete(getUrl()).params(paramFilter));

        // THEN
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andExpect(MockMvcResultMatchers.content()
                .string(Matchers.blankOrNullString()));
        verify(getService(), times(1)).deleteAllByCriteria(any());
    }

    @Test
    @DisplayName("GIVEN no filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the controller should return no content")
    default void testDeleteAllByCriteriaWithNoFilter() throws Exception {

        // WHEN
        final ResultActions result
            = getMockMvc().perform(MockMvcRequestBuilders.delete(getUrl()));

        // THEN
        result.andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andExpect(MockMvcResultMatchers.content()
                .string(Matchers.blankOrNullString()));
        verify(getService(), times(1)).deleteAllByCriteria(any());
    }
}
