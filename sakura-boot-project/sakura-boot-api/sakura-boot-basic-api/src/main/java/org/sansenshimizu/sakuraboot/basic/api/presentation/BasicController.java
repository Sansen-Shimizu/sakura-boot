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

package org.sansenshimizu.sakuraboot.basic.api.presentation;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.BasicService;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.DeleteByIdController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.FindAllController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.FindByIdController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.PatchByIdController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.SaveController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.UpdateByIdController;

/**
 * The base controller interface for CRUD operations.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from {@link BasicController},
 * follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends //
 *     BasicController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link BasicController}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new controller class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;RestController
 * &#064;RequestMapping("/yourPath")
 * public class YourController
 *     implements
 *     BasicController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *
 *     // Or implements your interface that extends BasicController.
 *     private final YourService service;
 *
 *     public YourController(final YourService service) {
 *
 *         this.service = service;
 *     }
 *
 *     public YourService getService() {
 *
 *         return this.service;
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
 * @see        SaveController
 * @see        FindAllController
 * @see        FindByIdController
 * @see        UpdateByIdController
 * @see        PatchByIdController
 * @see        DeleteByIdController
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BasicController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SaveController<E, I, D>, FindAllController<E, I>,
    FindByIdController<E, I>, UpdateByIdController<E, I, D>,
    PatchByIdController<E, I, D>, DeleteByIdController<E, I> {

    @Override
    BasicService<E, I> getService();
}
