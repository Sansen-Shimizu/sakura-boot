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

package org.sansenshimizu.sakuraboot.test.hypermedia;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModelAssembler;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The abstract test class for the {@link AbstractBasicModelAssembler}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from
 * {@link AbstractBasicModelAssemblerTest}, follow these steps:
 * </p>
 * <p>
 * Extends the {@link AbstractBasicModelAssemblerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourBasicModelAssemblerTest
 *     extends //
 *     AbstractBasicModelAssemblerTest&lt;YourModelAssembler, YourData&gt; {
 *
 *     &#064;Override
 *     protected String getPath() {
 *
 *         return "yourPath";
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <MA> The {@link AbstractBasicModelAssembler} type.
 * @param  <D>  The {@link DataPresentation} type.
 * @author      Malcolm Rozé
 * @see         AbstractBasicModelAssembler
 * @since       0.1.0
 */
public abstract class AbstractBasicModelAssemblerTest<
    MA extends AbstractBasicModelAssembler<D, ?>,
    D extends DataPresentation<?>> {

    /**
     * The name of the field data in the model class.
     */
    private static final String DATA_FIELD_NAME = "data";

    private Class<MA> getModelAssemblerClass() {

        @SuppressWarnings("unchecked")
        final Class<MA> modelAssemblerClass = (Class<
            MA>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        return modelAssemblerClass;
    }

    /**
     * Retrieves the model assembler of the specified type.
     *
     * @return the model assembler of type M
     */
    protected MA getModelAssembler() {

        return BeanCreatorHelper.getEmptyBean(getModelAssemblerClass());
    }

    protected abstract String getPath();

    /**
     * Retrieves the data object of the specified type.
     *
     * @return the data object of type D
     */
    protected D getData() {

        @SuppressWarnings("unchecked")
        final Class<D> dataClass
            = (Class<D>) ((ParameterizedType) getModelAssemblerClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return BeanCreatorHelper.getBean(dataClass);
    }

    /**
     * Get the name of the relationship used in the links.
     *
     * @return The name of the relationship.
     */
    protected String getRelationshipName() {

        return "relationships";
    }

    /**
     * Get the name of the second relationship used in the links.
     *
     * @return The name of the second relationship.
     */
    protected String getSecondRelationshipName() {

        return "secondRelationships";
    }

    @Test
    @DisplayName("GIVEN a basicModelAssembler and a data object,"
        + " WHEN converting the data object to model,"
        + " THEN the model should be the expected model with the same data "
        + "and expected links")
    final void testToModel() {

        // GIVEN
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder
            .setRequestAttributes(new ServletRequestAttributes(request));

        // WHEN
        final RepresentationModel<?> model
            = getModelAssembler().toModel(getData());

        // THEN
        assertModel(model, getData(), getPath(), request);
    }

    @Test
    @DisplayName("GIVEN a basicModelAssembler and a collection data object,"
        + " WHEN converting the data object to model,"
        + " THEN the collection model should be the expected collection model "
        + "with the same data and expected links")
    final void testToCollectionModel() {

        // GIVEN
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder
            .setRequestAttributes(new ServletRequestAttributes(request));

        // WHEN
        @SuppressWarnings("unchecked")
        final CollectionModel<RepresentationModel<?>> collectionModel
            = (CollectionModel<RepresentationModel<?>>) getModelAssembler()
                .toCollectionModel(List.of(getData()));
        final RepresentationModel<?> model
            = collectionModel.getContent().iterator().next();

        // THEN
        assertModel(model, getData(), getPath(), request);
    }

    private void assertModel(
        final RepresentationModel<?> model, final D data, final String path,
        final MockHttpServletRequest request) {

        assertThat(ReflectionTestUtils.getField(model, DATA_FIELD_NAME))
            .isEqualTo(data);
        assertThat(model).extracting(RepresentationModel::getLinks)
            .extracting(
                links -> links.getLink(IanaLinkRelations.SELF)
                    .orElseThrow()
                    .getHref(),
                links -> links.getLink(IanaLinkRelations.COLLECTION)
                    .orElseThrow()
                    .getHref())
            .containsExactly(
                request.getRequestURL() + "/" + path + "/" + data.getId(),
                request.getRequestURL() + "/" + path);

        if (hasRelationLink(data)) {

            assertThat(model).extracting(RepresentationModel::getLinks)
                .extracting(links -> links.getLink(IanaLinkRelations.RELATED)
                    .orElse(null))
                .isNotNull();
        }
    }

    private boolean hasRelationLink(final D data) {

        final boolean hasRelationAnyToOneLink
            = data instanceof final DataPresentation1RelationshipAnyToOne<?,
                ?> relationshipAnyToOne
                && relationshipAnyToOne.getRelationship() != null;

        final boolean hasRelationAnyToManyLink;

        if (data instanceof final DataPresentation1RelationshipAnyToMany<?,
            ?> relationshipAnyToMany) {

            final Set<?> relationships
                = relationshipAnyToMany.getRelationships();
            hasRelationAnyToManyLink
                = relationships != null && !relationships.isEmpty();
        } else {

            hasRelationAnyToManyLink = false;
        }

        final boolean hasRelation2AnyToOneLink
            = data instanceof final DataPresentation2RelationshipAnyToOne<?, ?,
                ?> relationship2AnyToOne
                && relationship2AnyToOne.getSecondRelationship() != null;

        final boolean hasRelation2AnyToManyLink;

        if (data instanceof final DataPresentation2RelationshipAnyToMany<?, ?,
            ?> relationship2AnyToMany) {

            final Set<?> secondRelationships
                = relationship2AnyToMany.getSecondRelationships();
            hasRelation2AnyToManyLink
                = secondRelationships != null && !secondRelationships.isEmpty();
        } else {

            hasRelation2AnyToManyLink = false;
        }

        return hasRelationAnyToOneLink
            || hasRelationAnyToManyLink
            || hasRelation2AnyToOneLink
            || hasRelation2AnyToManyLink;
    }
}
