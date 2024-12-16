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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.specification.api.presentation.CriteriaController;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

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
 *     protected YourModelAssembler(
 *         final GlobalSpecification globalSpecification) {
 *
 *         super(YourController.class, YourModel.class, globalSpecification);
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
     * The {@link GlobalSpecification}.
     */
    private final GlobalSpecification globalSpecification;

    /**
     * The model type.
     */
    private final Class<M> modelType;

    /**
     * This constructor only calls the super constructor of the
     * {@link RepresentationModelAssemblerSupport} class and sets the path.
     *
     * @param controllerClass     The class of the controller that used
     *                            this model assembler.
     * @param modelType           The class of the model.
     * @param globalSpecification The {@link GlobalSpecification},
     *                            help to create the relationship links, if any.
     */
    protected AbstractBasicModelAssembler(
        final Class<?> controllerClass, final Class<M> modelType,
        final GlobalSpecification globalSpecification) {

        super(controllerClass, modelType);
        this.modelType = modelType;
        this.globalSpecification = globalSpecification;
    }

    /**
     * The {@link Function} to instantiate a given model with parameters.
     *
     * @return A function that instantiates the model with parameters.
     */
    protected Function<D, M> instantiateModel() {

        return (final D data) -> {

            try {

                return modelType.getDeclaredConstructor(data.getClass())
                    .newInstance(data);
            } catch (final InstantiationException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException e) {

                throw new IllegalStateException(
                    """
                    Couldn't instantiate model.
                    Follow the convention or override
                    the instantiateModel() method.
                    """, e);
            }
        };
    }

    /**
     * The path use by the controller.
     *
     * @return The path use by the controller.
     */
    protected String getPath() {

        return English
            .plural(StringUtils.uncapitalize(modelType.getSimpleName())
                .replace("Model", ""));
    }

    /**
     * Converts the given data into a model, which extends
     * {@link AbstractBasicModel}.
     *
     * @param data The data to convert.
     */
    @Override
    public M toModel(final D data) {

        final M model = instantiateModel().apply(data);
        model.addLink(getPath() + "/" + data.getId(), IanaLinkRelations.SELF);
        model.addLink(getPath(), IanaLinkRelations.COLLECTION);
        RelationshipUtils.doWithIdRelationFields(data,
            (field, id) -> addRelationLink(field, id, model),
            (field, collection) -> addRelationsLink(field, collection, model),
            globalSpecification);

        return model;
    }

    private static <
        D extends DataPresentation<?>, M extends AbstractBasicModel<D>> void
        addRelationLink(
            final Field field, @Nullable final Object id, final M model) {

        if (id != null) {

            model.addLink(getClassName(field, false) + "/" + id,
                IanaLinkRelations.RELATED);
        }
    }

    private void addRelationsLink(
        final Field field, final Collection<?> collection, final M model) {

        if (CriteriaController.class.isAssignableFrom(getControllerClass())) {

            final String idsFilter = collection.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(",",
                    getClassName(field, true) + "?id" + ".in=", ""));
            model.addLink(idsFilter, IanaLinkRelations.RELATED);
        } else {

            model.addLink(getClassName(field, true), IanaLinkRelations.RELATED);
        }
    }

    private static
        String getClassName(final Field field, final boolean plural) {

        if (plural) {

            return field.getName().replace("Id", "");
        }
        return English.plural(field.getName().replace("Id", ""));
    }
}
