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

package org.sansenshimizu.sakuraboot.hypermedia.relationship.two;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModel;
import org.sansenshimizu.sakuraboot.hypermedia.relationship.one.AbstractBasicModelAssembler1Relationship;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two.BasicDto2RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOneAndAnyToMany;

/**
 * This class can be used to convert a {@link DataPresentation} with two
 * relationships to an {@link AbstractBasicModel}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a model assembler that inherits from
 * {@link AbstractBasicModelAssembler2Relationship}, follow these
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
 *     AbstractBasicModelAssembler2Relationship&lt;YourData, YourModel&gt; {
 *
 *     protected YourModelAssembler() {
 *
 *         super(YourController.class, YourModel.class, "pathOfTheController",
 *             "relationshipName", "secondRelationshipName");
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
 * @see        AbstractBasicModelAssembler1Relationship
 * @since      0.1.0
 */
public abstract class AbstractBasicModelAssembler2Relationship<
    D extends DataPresentation<?>, M extends AbstractBasicModel<D>>
    extends AbstractBasicModelAssembler1Relationship<D, M> {

    /**
     * The name of the second relationship.
     */
    protected final String secondRelationshipName;

    /**
     * This constructor only calls the super constructor of the
     * {@link RepresentationModelAssemblerSupport} class and sets the path.
     *
     * @param controllerClass        The class of the controller that used this
     *                               model
     *                               assembler.
     * @param modelType              The class of the model.
     * @param path                   The path of your controller ("/yourPath").
     * @param relationshipName       The name of the relationship.
     * @param secondRelationshipName The name of the second relationship.
     */
    protected AbstractBasicModelAssembler2Relationship(
        final Class<?> controllerClass, final Class<M> modelType,
        final String path, final String relationshipName,
        final String secondRelationshipName) {

        super(controllerClass, modelType, path, relationshipName);
        this.secondRelationshipName = secondRelationshipName;
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

        if (data instanceof final DataPresentation2RelationshipAnyToOne<?, ?,
            ?> entity) {

            addLinkSecondRelationshipAnyToOne(entity, model);
        }

        if (data instanceof final DataPresentation2RelationshipAnyToMany<?, ?,
            ?> entity) {

            addLinkSecondRelationshipAnyToMany(entity, model);
        }

        /*@formatter:off*/
        if (data instanceof final
            DataPresentation2RelationshipAnyToOneAndAnyToMany<?, ?, ?> entity) {
            /*@formatter:on*/

            addLinkRelationshipAnyToMany(entity, model, secondRelationshipName);
        }

        return model;
    }

    /**
     * Add a link to the model to reference the related entity.
     *
     * @param entity The entity to be linked.
     * @param model  The model that will have the link.
     */
    protected void addLinkSecondRelationshipAnyToMany(
        final DataPresentation2RelationshipAnyToMany<?, ?, ?> entity,
        final M model) {

        if (entity instanceof final BasicDto2RelationshipAnyToMany<?, ?, ?, ?,
            ?> dto) {

            final Set<?> ids = dto.getSecondRelationshipsId();

            if (ids != null && !ids.isEmpty()) {

                final String idsFilter = getPath(ids, secondRelationshipName);
                model.addLink(idsFilter, IanaLinkRelations.RELATED);
            }
        } else {

            final Set<? extends DataPresentation<?>> relationalEntities
                = entity.getSecondRelationships();

            if (relationalEntities != null && !relationalEntities.isEmpty()) {

                final String idsFilter = getPath(
                    relationalEntities.stream()
                        .map(DataPresentation::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toUnmodifiableSet()),
                    secondRelationshipName);
                model.addLink(idsFilter, IanaLinkRelations.RELATED);
            }
        }
    }

    /**
     * Add a link to the model to reference the related entity.
     *
     * @param entity The entity to be linked.
     * @param model  The model that will have the link.
     */
    protected void addLinkSecondRelationshipAnyToOne(
        final DataPresentation2RelationshipAnyToOne<?, ?, ?> entity,
        final M model) {

        if (entity instanceof final BasicDto2RelationshipAnyToOne<?, ?, ?, ?,
            ?> dto) {

            final Object id = dto.getSecondRelationshipId();

            if (id != null) {

                model.addLink(secondRelationshipName + "/" + id,
                    IanaLinkRelations.RELATED);
            }
        } else {

            final DataPresentation<?> relationalEntity
                = entity.getSecondRelationship();

            if (relationalEntity != null) {

                final Object id = relationalEntity.getId();

                if (id != null) {

                    model.addLink(secondRelationshipName + "/" + id,
                        IanaLinkRelations.RELATED);
                }
            }
        }
    }
}
