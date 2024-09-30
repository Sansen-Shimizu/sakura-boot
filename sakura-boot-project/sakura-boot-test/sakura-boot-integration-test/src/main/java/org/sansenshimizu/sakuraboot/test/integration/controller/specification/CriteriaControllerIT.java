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

package org.sansenshimizu.sakuraboot.test.integration.controller.specification;

import java.io.Serializable;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.presentation.BasicController;
import org.sansenshimizu.sakuraboot.specification.api.business.CriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.CriteriaController;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.integration.controller.basic.controllers.DeleteByIdControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.basic.controllers.FindByIdControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.basic.controllers.PatchByIdControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.basic.controllers.SaveControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.basic.controllers.UpdateByIdControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.specification.controllers.FindAllByCriteriaControllerIT;

/**
 * The base integration test interface for all criteria controllers. This
 * interface provides common integration tests for testing
 * {@link CriteriaController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller integration test class that inherits from
 * {@link CriteriaControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link CriteriaControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     BasicControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        BasicController
 * @see        SaveControllerIT
 * @see        FindAllByCriteriaControllerIT
 * @see        FindByIdControllerIT
 * @see        UpdateByIdControllerIT
 * @see        PatchByIdControllerIT
 * @see        DeleteByIdControllerIT
 * @since      0.1.0
 */
public interface CriteriaControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>, F extends FilterPresentation<?>>
    extends SaveControllerIT<E, I, D>,
    FindAllByCriteriaControllerIT<E, I, D, F>, FindByIdControllerIT<E, I, D>,
    UpdateByIdControllerIT<E, I, D>, PatchByIdControllerIT<E, I, D>,
    DeleteByIdControllerIT<E, I, D> {

    /**
     * Return a mock service that extends {@link CriteriaService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    CriteriaService<E, I, F> getService();
}
