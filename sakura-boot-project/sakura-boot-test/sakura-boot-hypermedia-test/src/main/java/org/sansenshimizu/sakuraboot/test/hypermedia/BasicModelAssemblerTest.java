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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Entity;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModelAssembler;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;
import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The abstract test class for the {@link AbstractBasicModelAssembler}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from
 * {@link BasicModelAssemblerTest}, follow these steps:
 * </p>
 * <p>
 * Extends the {@link BasicModelAssemblerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourBasicModelAssemblerTest
 *     implements //
 *     BasicModelAssemblerTest&lt;YourModelAssembler, YourData&gt; {}
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
@ExtendWith(MockitoExtension.class)
public interface BasicModelAssemblerTest<
    MA extends AbstractBasicModelAssembler<D, ?>,
    D extends DataPresentation<?>> {

    /**
     * The name of the field data in the model class.
     */
    String DATA_FIELD_NAME = "data";

    /**
     * The {@link GlobalSpecification} used to check the relational links.
     *
     * @return The {@link GlobalSpecification}.
     */
    default GlobalSpecification getGlobalSpecification() {

        return new GlobalSpecification(getEntityPackageName(),
            getServicePackageName(), getDtoPackageName(),
            getMapperPackageName(), getControllerPackageName());
    }

    /**
     * Get the entity package name.
     *
     * @return The entity package name.
     */
    default String getEntityPackageName() {

        return "persistence";
    }

    /**
     * Get the service package name.
     *
     * @return The service package name.
     */
    default String getServicePackageName() {

        return "business";
    }

    /**
     * Get the DTO package name.
     *
     * @return The DTO package name.
     */
    default String getDtoPackageName() {

        return "business";
    }

    /**
     * Get the mapper package name.
     *
     * @return The mapper package name.
     */
    default String getMapperPackageName() {

        return "business";
    }

    /**
     * Get the controller package name.
     *
     * @return The controller package name.
     */
    default String getControllerPackageName() {

        return "presentation";
    }

    private Class<MA> getModelAssemblerClass() {

        @SuppressWarnings("unchecked")
        final Class<MA> modelAssemblerClass = (Class<
            MA>) ((ParameterizedType) getClass().getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        return modelAssemblerClass;
    }

    /**
     * Retrieves the model assembler of the specified type.
     *
     * @return the model assembler of type M
     */
    default MA getModelAssembler() {

        final MA modelAssembler
            = BeanCreatorHelper.getEmptyBean(getModelAssemblerClass());

        try {

            final Field globalSpecificationField = ReflectionUtils
                .findField(modelAssembler.getClass(), "globalSpecification");

            if (globalSpecificationField == null) {

                throw new RuntimeException("Shouldn't happen");
            }
            globalSpecificationField.setAccessible(true);
            globalSpecificationField.set(modelAssembler,
                getGlobalSpecification());
        } catch (final IllegalAccessException e) {

            throw new RuntimeException(e);
        }

        return modelAssembler;
    }

    /**
     * The path use by the controller.
     *
     * @return The path use by the controller.
     */
    default String getPath() {

        return English.plural(
            StringUtils.uncapitalize(getModelAssemblerClass().getSimpleName())
                .replace("ModelAssembler", ""));
    }

    /**
     * Retrieves the data object of the specified type.
     *
     * @return the data object of type D
     */
    default D getData() {

        @SuppressWarnings("unchecked")
        final Class<D> dataClass
            = (Class<D>) ((ParameterizedType) getModelAssemblerClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return BeanCreatorHelper.getBean(dataClass);
    }

    @Test
    @DisplayName("GIVEN a basicModelAssembler and a data object,"
        + " WHEN converting the data object to model,"
        + " THEN the model should be the expected model with the same data "
        + "and expected links")
    default void testToModel() {

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
    default void testToCollectionModel() {

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

        if (data.getClass().isAnnotationPresent(Entity.class)) {

            return Arrays.stream(data.getClass().getDeclaredFields())
                .anyMatch(RelationshipUtils::isRelationship);
        }
        return Arrays.stream(data.getClass().getDeclaredFields())
            .anyMatch((final Field field) -> {

                final AnnotatedElement entityField = RelationshipUtils
                    .getEntityFieldFromDto(field, data.getClass(),
                        getEntityPackageName(), getDtoPackageName());

                if (entityField == null) {

                    return false;
                }
                return RelationshipUtils.isRelationship(entityField);
            });
    }
}
