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

package org.sansenshimizu.sakuraboot.specification.api.presentation;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.DeleteByIdController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.FindByIdController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.PatchByIdController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.SaveController;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.UpdateByIdController;
import org.sansenshimizu.sakuraboot.specification.api.business.CriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.controllers.FindAllByCriteriaController;

/**
 * The base controller interface for CRUD operations with filtering capabilities
 * using criteria-based queries.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface with filtering support that inherits from
 * {@link CriteriaController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends CriteriaController&lt;YourEntity, YourIdType, //
 *         YourDataPresentation, YourFilter&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class with filtering support that implements
 * {@link CriteriaController}, follow these steps:
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
 *     implements CriteriaController&lt;YourEntity, YourIdType,
 *         YourDataPresentation, YourFilter&gt; {
 *
 *     // Or implements your interface that extends CriteriaController.
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
 * @param  <F> The
 *             {@link FilterPresentation}
 *             type.
 * @author     Malcolm Rozé
 * @see        SaveController
 * @see        FindAllByCriteriaController
 * @see        FindByIdController
 * @see        UpdateByIdController
 * @see        PatchByIdController
 * @see        DeleteByIdController
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface CriteriaController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>, F extends FilterPresentation<?>>
    extends SaveController<E, I, D>, FindAllByCriteriaController<E, I, F>,
    FindByIdController<E, I>, UpdateByIdController<E, I, D>,
    PatchByIdController<E, I, D>, DeleteByIdController<E, I> {

    @Override
    CriteriaService<E, I, F> getService();
}
