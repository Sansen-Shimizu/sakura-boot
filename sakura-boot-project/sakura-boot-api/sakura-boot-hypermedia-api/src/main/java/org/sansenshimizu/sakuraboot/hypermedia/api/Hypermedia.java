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

package org.sansenshimizu.sakuraboot.hypermedia.api;

import java.util.Map;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * Interface for all controller class that add hypermedia support needs to
 * implement.
 * <p>
 * To create a controller that implements {@link Hypermedia}, follow these
 * steps:
 * </p>
 * <p>
 * Create a new controller:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourController
 *     implements Hypermedia&lt;YourData, YourModelAssembler&gt; {
 *
 *     private final YourModelAssembler yourModelAssembler;
 *
 *     protected YourController(final YourModelAssembler yourModelAssembler) {
 *
 *         this.yourModelAssembler = yourModelAssembler;
 *     }
 *
 *     &#064;Override
 *     public YourModelAssembler getModelAssembler() {
 *
 *         return yourModelAssembler;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <D> The {@link DataPresentation} type.
 * @param  <M> The {@link RepresentationModelAssemblerSupport} type.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface Hypermedia<D extends DataPresentation<?>,
    M extends RepresentationModelAssemblerSupport<D, ?>> {

    /**
     * The default key for the pagedResourcesAssembler.
     */
    String DEFAULT_PAGED_NAME = "default";

    /**
     * Get the class of the data use by the
     * {@link RepresentationModelAssemblerSupport}.
     *
     * @return The class of the data.
     */
    default Class<D> getDataClass() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            Hypermedia.class.getTypeName());
    }

    /**
     * Get the {@link RepresentationModelAssemblerSupport} that will be used to
     * apply hypermedia.
     *
     * @return A model assembler.
     */
    M getModelAssembler();

    /**
     * Get the different {@link PagedResourcesAssembler} that will be used to
     * apply hypermedia to page.
     *
     * @return A map of paged resources assembler.
     */
    default
        Map<String, PagedResourcesAssembler<D>> getPagedResourcesAssembler() {

        return Map.of(DEFAULT_PAGED_NAME,
            new PagedResourcesAssembler<>(null, null));
    }
}
