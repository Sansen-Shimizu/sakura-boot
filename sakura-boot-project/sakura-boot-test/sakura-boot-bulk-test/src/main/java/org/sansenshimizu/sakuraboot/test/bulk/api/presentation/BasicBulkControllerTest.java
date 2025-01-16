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

package org.sansenshimizu.sakuraboot.test.bulk.api.presentation;

import java.io.Serializable;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.BasicBulkService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.BasicBulkController;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.controllers.DeleteAllControllerTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.controllers.PatchAllControllerTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.controllers.SaveAllControllerTest;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.controllers.UpdateAllControllerTest;

/**
 * The base test interface for all basic bulk controllers. This interface
 * provides common tests for testing {@link BasicBulkController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link BasicBulkControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicBulkControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourControllerTest
 *     implements BasicBulkControllerTest&lt;YourEntity, YourIdType, //
 *         YourDataPresentation&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourController controller;
 *
 *     &#064;Mock
 *     private YourService service;
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourController getController() {
 *
 *         return controller;
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
 * @see        SaveAllControllerTest
 * @see        UpdateAllControllerTest
 * @see        PatchAllControllerTest
 * @see        DeleteAllControllerTest
 * @since      0.1.2
 */
public interface BasicBulkControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SaveAllControllerTest<E, I, D>, UpdateAllControllerTest<E, I, D>,
    PatchAllControllerTest<E, I, D>, DeleteAllControllerTest<E, I> {

    /**
     * Get the {@link BasicBulkController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link BasicBulkController}.
     */
    @Override
    BasicBulkController<E, I, D> getController();

    /**
     * Get the {@link BasicBulkService} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicBulkService}.
     */
    @Override
    BasicBulkService<E, I> getService();
}
