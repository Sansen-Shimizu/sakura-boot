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

package org.sansenshimizu.sakuraboot.bulk.api.presentation;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.BasicBulkService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.DeleteAllController;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.PatchAllController;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.SaveAllController;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.UpdateAllController;

/**
 * The base controller interface for bulk operations.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link BasicBulkController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends //
 *     BasicBulkController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link BasicBulkController},
 * follow these steps:
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
 *     BasicBulkController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *
 *     // Or implements your interface that extends BasicBulkController.
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
 * @see        SaveAllController
 * @see        UpdateAllController
 * @see        PatchAllController
 * @see        DeleteAllController
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BasicBulkController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SaveAllController<E, I, D>, UpdateAllController<E, I, D>,
    PatchAllController<E, I, D>, DeleteAllController<E, I> {

    @Override
    BasicBulkService<E, I> getService();
}
