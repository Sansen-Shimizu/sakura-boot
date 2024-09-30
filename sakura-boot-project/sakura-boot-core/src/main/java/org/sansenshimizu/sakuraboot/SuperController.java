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

package org.sansenshimizu.sakuraboot;

import java.io.Serializable;

/**
 * The base controller interface.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from {@link SuperController},
 * follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourController
 *     extends SuperController&lt;YourEntity, YourIdType&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link SuperController}, follow
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
 *     implements SuperController&lt;YourEntity, YourIdType&gt; {
 *
 *     // Or implements your interface that extends SuperController.
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
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SuperController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> {

    /**
     * Returns the {@link SuperService} associated with this
     * {@code SuperController}.
     *
     * @return The {@link SuperService} implementation.
     */
    @SuppressWarnings("EmptyMethod")
    SuperService<E, I> getService();
}
