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

package org.sansenshimizu.sakuraboot.hypermedia.relationship.one;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModel;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModelAssembler;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;

/**
 * This class can be used to convert a {@link DataPresentation} with one
 * relationship to an {@link AbstractBasicModel}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a model assembler that inherits from
 * {@link AbstractBasicModelAssembler1Relationship}, follow these
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
 *     extends //
 *     AbstractBasicModelAssembler1Relationship&lt;YourData, YourModel&gt; {
 *
 *     protected YourModelAssembler() {
 *
 *         super(YourController.class, YourModel.class, "pathOfTheController",
 *             "relationshipName");
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
 * @see        AbstractBasicModelAssembler
 * @since      0.1.0
 */
public abstract class AbstractBasicModelAssembler1Relationship<
    D extends DataPresentation<?>, M extends AbstractBasicModel<D>>
    extends AbstractBasicModelAssembler<D, M> {

    /**
     * The name of the relationship.
     */
    protected final String relationshipName;

    /**
     * This constructor only calls the super constructor of the
     * {@link RepresentationModelAssemblerSupport} class and sets the path.
     *
     * @param controllerClass  The class of the controller that used this model
     *                         assembler.
     * @param modelType        The class of the model.
     * @param path             The path of your controller ("/yourPath").
     * @param relationshipName The name of the relationship.
     */
    protected AbstractBasicModelAssembler1Relationship(
        final Class<?> controllerClass, final Class<M> modelType,
        final String path, final String relationshipName) {

        super(controllerClass, modelType, path);
        this.relationshipName = relationshipName;
    }

    /**
     * Converts the given data into a model, which extends
     * {@link AbstractBasicModel}.
     *
     * @param data The data to convert.
     */
    @Override
    public M toModel(final D data) {

        final M model = super.toModel(data);

        if (data instanceof final DataPresentation1RelationshipAnyToOne<?,
            ?> entity) {

            addLinkRelationshipAnyToOne(entity, model, relationshipName);
            return model;
        }

        if (data instanceof final DataPresentation1RelationshipAnyToMany<?,
            ?> entity) {

            addLinkRelationshipAnyToMany(entity, model, relationshipName);
        }

        return model;
    }

    /**
     * Add a link to the model to reference the related entity.
     *
     * @param entity       The entity to be linked.
     * @param model        The model that will have the link.
     * @param relationship The name of the relationship.
     */
    protected void addLinkRelationshipAnyToMany(
        final DataPresentation1RelationshipAnyToMany<?, ?> entity,
        final M model, final String relationship) {

        if (entity instanceof final BasicDto1RelationshipAnyToMany<?, ?,
            ?> dto) {

            final Set<?> ids = dto.getRelationshipsId();

            if (ids != null && !ids.isEmpty()) {

                final String idsFilter = getPath(ids, relationship);
                model.addLink(idsFilter, IanaLinkRelations.RELATED);
            }
        } else {

            final Set<? extends DataPresentation<?>> relationalEntities
                = entity.getRelationships();

            if (relationalEntities != null && !relationalEntities.isEmpty()) {

                final String idsFilter = getPath(relationalEntities.stream()
                    .map(DataPresentation::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet()), relationship);
                model.addLink(idsFilter, IanaLinkRelations.RELATED);
            }
        }
    }

    /**
     * Add a link to the model to reference the related entity.
     *
     * @param entity       The entity to be linked.
     * @param model        The model that will have the link.
     * @param relationship The name of the relationship.
     */
    protected void addLinkRelationshipAnyToOne(
        final DataPresentation1RelationshipAnyToOne<?, ?> entity, final M model,
        final String relationship) {

        if (entity instanceof final BasicDto1RelationshipAnyToOne<?, ?,
            ?> dto) {

            final Object id = dto.getRelationshipId();

            if (id != null) {

                model.addLink(relationship + "/" + id,
                    IanaLinkRelations.RELATED);
            }
        } else {

            final DataPresentation<?> relationalEntity
                = entity.getRelationship();

            if (relationalEntity != null) {

                final Object id = relationalEntity.getId();

                if (id != null) {

                    model.addLink(relationship + "/" + id,
                        IanaLinkRelations.RELATED);
                }
            }
        }
    }

    /**
     * Generates a path string for a given set of IDs and a relational entity
     * name.
     *
     * @param  ids          the set of IDs
     * @param  relationship the name of the relational entity
     * @return              the generated path string
     */
    protected static
        String getPath(final Set<?> ids, final String relationship) {

        final String idsFilter = ids.stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .collect(Collectors.joining(","));
        return relationship + "?id.in=" + idsFilter;
    }
}
