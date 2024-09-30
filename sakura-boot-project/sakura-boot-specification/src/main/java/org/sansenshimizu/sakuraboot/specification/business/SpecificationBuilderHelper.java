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

package org.sansenshimizu.sakuraboot.specification.business;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.Attribute;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.Filter;

/**
 * Helper class that use reflection and {@link EntityManager} to get the field
 * and their related attribute.
 *
 * @author Malcolm Rozé
 * @see    SpecificationBuilderImpl
 * @since  0.1.0
 */
@Component
@SuppressWarnings("java:S1258")
public class SpecificationBuilderHelper {

    /**
     * The {@link EntityManager} used to get the metamodel of the entity to
     * create the specification.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Return the fields of the filter using reflection.
     *
     * @param  filter      The filter.
     * @param  entityClass The entity class.
     * @return             The fields of the filter.
     */
    @SuppressWarnings("java:S1452")
    protected List<Pair<Object, Attribute<?, ?>>> getFilterWithAttribute(
        final Class<?> entityClass, final Object filter) {

        return getFilterWithAttribute(entityClass, filter, null);
    }

    private List<Pair<Object, Attribute<?, ?>>> getFilterWithAttribute(
        final Class<?> entityClass, final Object filter,
        @Nullable final Attribute<?, ?> attribute) {

        final List<Pair<Object, Attribute<?, ?>>> result = new ArrayList<>();
        ReflectionUtils.doWithFields(filter.getClass(), field -> result.addAll(
            mapFieldWithAttribute(entityClass, filter, field, attribute)));
        return result.stream().filter(pair -> pair.getValue() != null).toList();
    }

    private List<Pair<Object, Attribute<?, ?>>> mapFieldWithAttribute(
        final Class<?> entityClass, final Object filter, final Field field,
        @Nullable final Attribute<?, ?> attribute) {

        if (field.trySetAccessible()
            && !"serialVersionUID".equals(field.getName())) {

            final Object filterField = ReflectionUtils.getField(field, filter);

            if (filterField != null) {

                return mapFieldWithAttributeAux(entityClass, field, attribute,
                    filterField);
            }
        }
        return List.of();
    }

    private List<Pair<Object, Attribute<?, ?>>> mapFieldWithAttributeAux(
        final Class<?> entityClass, final Field field,
        @Nullable final Attribute<?, ?> attribute, final Object filterField) {

        Attribute<?, ?> actualAttribute = attribute;

        if (attribute == null) {

            if (entityClass.isAnnotationPresent(Entity.class)) {

                actualAttribute = entityManager.getMetamodel()
                    .entity(entityClass)
                    .getAttribute(field.getName());
            } else if (entityClass.isAnnotationPresent(Embeddable.class)) {

                actualAttribute = entityManager.getMetamodel()
                    .embeddable(entityClass)
                    .getAttribute(field.getName());
            } else {

                return List.of();
            }
        }

        if (!(filterField instanceof Filter) && actualAttribute != null) {

            return getFilterWithAttribute(entityClass, filterField,
                actualAttribute);
        }

        return List.of(Pair.of(filterField, actualAttribute));
    }
}
