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

package org.sansenshimizu.sakuraboot.test.integration.controller.bulk;

import java.io.Serializable;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.BasicBulkService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.BasicBulkController;
import org.sansenshimizu.sakuraboot.test.integration.controller.bulk.controllers.DeleteAllControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.bulk.controllers.PatchAllControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.bulk.controllers.SaveAllControllerIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.bulk.controllers.UpdateAllControllerIT;

/**
 * The base integration test interface for all bulk controllers. This interface
 * provides common integration tests for testing {@link BasicBulkController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller integration test class that inherits from
 * {@link BasicBulkControllerIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicBulkControllerIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;WebMvcTest(YourController.class)
 * public class YourIT //
 *     implements //
 *     BasicBulkControllerIT&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 * @see        BasicBulkController
 * @see        SaveAllControllerIT
 * @see        UpdateAllControllerIT
 * @see        PatchAllControllerIT
 * @see        DeleteAllControllerIT
 * @since      0.1.2
 */
public interface BasicBulkControllerIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SaveAllControllerIT<E, I, D>, UpdateAllControllerIT<E, I, D>,
    PatchAllControllerIT<E, I, D>, DeleteAllControllerIT<E, I, D> {

    /**
     * Return a mock service that extends {@link BasicBulkService}.
     * Need to use {@link MockBean}.
     *
     * @return A mock service use in integration test.
     */
    @Override
    BasicBulkService<E, I> getService();
}