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

package org.sansenshimizu.sakuraboot.test.basic.api.presentation;

import java.io.Serializable;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.BasicService;
import org.sansenshimizu.sakuraboot.basic.api.presentation.BasicController;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers.DeleteByIdControllerTest;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers.FindAllControllerTest;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers.FindByIdControllerTest;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers.PatchByIdControllerTest;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers.SaveControllerTest;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers.UpdateByIdControllerTest;

/**
 * The base test interface for all basic controllers. This interface provides
 * common tests for testing {@link BasicController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link BasicControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourControllerTest
 *     implements
 *     BasicControllerTest&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
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
 * @see        BasicController
 * @see        SaveControllerTest
 * @see        FindAllControllerTest
 * @see        FindByIdControllerTest
 * @see        UpdateByIdControllerTest
 * @see        PatchByIdControllerTest
 * @see        DeleteByIdControllerTest
 * @since      0.1.0
 */
public interface BasicControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SaveControllerTest<E, I, D>, FindAllControllerTest<E, I>,
    FindByIdControllerTest<E, I>, UpdateByIdControllerTest<E, I, D>,
    PatchByIdControllerTest<E, I, D>, DeleteByIdControllerTest<E, I> {

    /**
     * Get the {@link BasicController} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link BasicController}.
     */
    @Override
    BasicController<E, I, D> getController();

    /**
     * Get the {@link BasicService} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicService}.
     */
    @Override
    BasicService<E, I> getService();
}
