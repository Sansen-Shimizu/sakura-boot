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

package org.sansenshimizu.sakuraboot.hypermedia;

import java.util.function.Function;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * This class can be used to convert a {@link DataPresentation} to
 * an {@link AbstractBasicModel}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a model assembler that inherits from
 * {@link AbstractBasicModelAssembler}, follow these
 * steps:
 * </p>
 * <p>
 * Create a new model assembler class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourModelAssembler
 *     extends AbstractBasicModelAssembler&lt;YourData, YourModel&gt; {
 *
 *     protected YourModelAssembler() {
 *
 *         super(YourController.class, YourModel.class, "pathOfTheController");
 *     }
 *
 *     &#064;Override
 *     protected Function&lt;YourData, YourModel&gt; instantiateModel() {
 *
 *         return YourModel::new;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * <b>NOTE:</b> If you want to add some link to the model you can do it in the
 * {@link #instantiateModel()} after instantiate the model and before returning
 * it or in the {@link #toModel(DataPresentation)} method.
 * </p>
 *
 * @param  <D> The {@link DataPresentation} type.
 * @param  <M> The {@link AbstractBasicModel} type.
 * @author     Malcolm Rozé
 * @see        RepresentationModelAssemblerSupport
 * @since      0.1.0
 */
public abstract class AbstractBasicModelAssembler<D extends DataPresentation<?>,
    M extends AbstractBasicModel<D>>
    extends RepresentationModelAssemblerSupport<D, M> {

    /**
     * The path use by the controller.
     */
    protected final String path;

    /**
     * This constructor only calls the super constructor of the
     * {@link RepresentationModelAssemblerSupport} class and sets the path.
     *
     * @param controllerClass The class of the controller that used this model
     *                        assembler.
     * @param modelType       The class of the model.
     * @param path            The path of your controller ("/yourPath").
     */
    protected AbstractBasicModelAssembler(
        final Class<?> controllerClass, final Class<M> modelType,
        final String path) {

        super(controllerClass, modelType);
        this.path = path;
    }

    /**
     * The {@link Function} to instantiate a given model with parameters.
     *
     * @return A function that instantiates the model with parameters.
     */
    protected abstract Function<D, M> instantiateModel();

    /**
     * Converts the given data into a model, which extends
     * {@link AbstractBasicModel}.
     *
     * @param data The data to convert.
     */
    @Override
    public M toModel(final D data) {

        final M model = instantiateModel().apply(data);
        model.addLink(path + "/" + data.getId(), IanaLinkRelations.SELF);
        model.addLink(path, IanaLinkRelations.COLLECTION);
        return model;
    }
}
