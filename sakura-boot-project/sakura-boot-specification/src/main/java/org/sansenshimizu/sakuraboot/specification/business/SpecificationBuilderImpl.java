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

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.BooleanFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CollectionFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.DateFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.TextFilter;

/**
 * The {@link SpecificationBuilderImpl} base class provides methods for building
 * criteria-based specifications for {@link DataPresentation} filtering.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a specific specificationBuilder for your {@link DataPresentation}
 * that inherits from {@link SpecificationBuilderImpl}, follow these steps:
 * </p>
 * <p>
 * Create a new specification builder class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourSpecificationBuilder
 *     extends SpecificationBuilderImpl&lt;YourEntity&gt; {}
 * </pre>
 *
 * </blockquote>
 * <p>
 * <b>NOTE:</b> This class already have all the methods
 * to create great specifications. Some methods are protected so you can make
 * your custom specification if needed.
 * </p>
 *
 * @param  <D> The entity type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        SpecificationBuilder
 * @since      0.1.0
 */
@Component
public class SpecificationBuilderImpl<D extends DataPresentation<?>>
    implements SpecificationBuilder<D> {

    /**
     * The list of the different specification that will be used to construct
     * the final specification.
     */
    private final List<Specification<D>> specifications;

    /**
     * Helper class to get the attribute expression from the
     * {@link DataPresentation}.
     */
    private final SpecificationBuilderHelper helper;

    /**
     * Constructor to initialize the specification list and the helper.
     *
     * @param helper The {@link SpecificationBuilderHelper}.
     */
    public SpecificationBuilderImpl(final SpecificationBuilderHelper helper) {

        specifications = new ArrayList<>();
        this.helper = helper;
    }

    @Override
    @Logging
    public Specification<D> apply(
        @Nullable final FilterPresentation<?> filter,
        final Class<D> entityClass) {

        Specification<D> specification = Specification.where(null);

        if (filter != null) {

            prepareSpecification(filter, entityClass);

            final Boolean distinct = filter.getDistinct();

            if (distinct != null) {

                specification = specification.and(applyDistinct(distinct));
            }
            specification = specification
                .and(assembleSpecification(filter.getInclusive()));
        }
        specifications.clear();
        return specification;
    }

    /**
     * Creates an inclusive or exclusive specification.
     *
     * @param  isInclusive A boolean flag indicating whether the specification
     *                     should be inclusive or exclusive.
     * @return             A specification build from the list of the
     *                     {@link SpecificationBuilderImpl#specifications}
     *                     and the given isInclusive.
     */
    protected Specification<D> assembleSpecification(
        @Nullable final Boolean isInclusive) {

        if (specifications.isEmpty()) {

            return Specification.where(null);
        }

        final Iterator<Specification<D>> it = specifications.iterator();
        Specification<D> specification = it.next();

        while (it.hasNext()) {

            if (Boolean.TRUE.equals(isInclusive)) {

                specification = specification.or(it.next());
            } else {

                specification = specification.and(it.next());
            }
        }
        return specification;
    }

    /**
     * Create for each field that has a filter associated a specification using
     * the given {@link FilterPresentation}.
     *
     * @param filter      The {@link FilterPresentation} use to get the
     *                    different filter for each field.
     * @param entityClass The class of the entity.
     */
    protected void prepareSpecification(
        final FilterPresentation<?> filter, final Class<?> entityClass) {

        final List<Attribute<?, ?>> attributes = new ArrayList<>();
        helper.getFilterWithAttribute(entityClass, filter)
            .forEach((final Pair<Object, Attribute<?, ?>> pair) -> {

                attributes.add(pair.getValue());
                createSpecificationForFields(pair.getKey(), attributes);
            });
    }

    private void createSpecificationForFields(
        final Object filter, final List<Attribute<?, ?>> attributes) {

        switch (filter) {

            case final NumberFilter<?> numberFilter -> applyFilter(numberFilter,
                createAttributeFunction(attributes));

            case final TextFilter textFilter
                -> applyFilter(textFilter, createAttributeFunction(attributes));

            case final DateFilter<?> dateFilter
                -> applyFilter(dateFilter, createAttributeFunction(attributes));

            case final BooleanFilter booleanFilter -> applyFilter(booleanFilter,
                createAttributeFunction(attributes));

            case final CommonFilter<?> commonFilter -> applyFilter(commonFilter,
                createAttributeFunction(attributes));

            case final CollectionFilter<?> collectionFilter -> applyFilter(
                collectionFilter, createListAttributeFunction(attributes));

            default -> createSpecificationForRelationship(filter, attributes);
        }
    }

    private void createSpecificationForRelationship(
        final Object filter, final List<Attribute<?, ?>> attributes) {

        final Class<?> relationshipClass
            = attributes.get(attributes.size() - 1).getJavaType();
        helper.getFilterWithAttribute(relationshipClass, filter)
            .forEach((final Pair<Object, Attribute<?, ?>> pair) -> {

                attributes.add(pair.getValue());
                createSpecificationForFields(pair.getKey(), attributes);
            });
    }

    private <T, Y> Function<Root<D>, Expression<T>> createAttributeFunction(
        final List<Attribute<?, ?>> attributes) {

        if (attributes.size() == 1) {

            @SuppressWarnings("unchecked")
            final SingularAttribute<? super D, T> attribute
                = (SingularAttribute<? super D, T>) attributes.get(0);
            return root -> root.get(attribute);
        }

        final Iterator<Attribute<?, ?>> it = attributes.iterator();
        @SuppressWarnings("unchecked")
        final SingularAttribute<D, Y> attribute
            = (SingularAttribute<D, Y>) it.next();
        final Function<Root<D>, Join<D, Y>> join
            = createAttributeFunctionForRelationship(attribute);
        return createAttributeFunctionRecursive(join, it);
    }

    private <
        X, Y, Z, T> Function<Root<D>, Expression<T>>
        createAttributeFunctionRecursive(
            final Function<Root<D>, Join<X, Y>> parentJoin,
            final Iterator<Attribute<?, ?>> it) {

        final Attribute<?, ?> attribute = it.next();

        if (!it.hasNext()) {

            @SuppressWarnings("unchecked")
            final SingularAttribute<? super Y, T> castAttribute
                = (SingularAttribute<? super Y, T>) attribute;
            return root -> parentJoin.apply(root).get(castAttribute);
        } else {

            @SuppressWarnings("unchecked")
            final SingularAttribute<Y, Z> castAttribute
                = (SingularAttribute<Y, Z>) attribute;
            final Function<Root<D>, Join<Y, Z>> currentJoin
                = createAttributeFunctionForNestedRelationship(parentJoin,
                    castAttribute);
            return createAttributeFunctionRecursive(currentJoin, it);
        }
    }

    private <
        T, Y> Function<Root<D>, Expression<List<T>>>
        createListAttributeFunction(final List<Attribute<?, ?>> attributes) {

        if (attributes.size() == 1) {

            @SuppressWarnings("unchecked")
            final ListAttribute<D, T> attribute
                = (ListAttribute<D, T>) attributes.get(0);
            return root -> root.get(attribute);
        }

        final Iterator<Attribute<?, ?>> it = attributes.iterator();
        @SuppressWarnings("unchecked")
        final SingularAttribute<D, Y> attribute
            = (SingularAttribute<D, Y>) it.next();
        final Function<Root<D>, Join<D, Y>> join
            = createAttributeFunctionForRelationship(attribute);
        return createListAttributeFunctionRecursive(join, it);
    }

    private <
        X, Y, Z, T> Function<Root<D>, Expression<List<T>>>
        createListAttributeFunctionRecursive(
            final Function<Root<D>, Join<X, Y>> parentJoin,
            final Iterator<Attribute<?, ?>> it) {

        final Attribute<?, ?> attribute = it.next();

        if (!it.hasNext()) {

            @SuppressWarnings("unchecked")
            final ListAttribute<Y, T> castAttribute
                = (ListAttribute<Y, T>) attribute;
            return root -> parentJoin.apply(root).get(castAttribute);
        } else {

            @SuppressWarnings("unchecked")
            final SingularAttribute<Y, Z> castAttribute
                = (SingularAttribute<Y, Z>) attribute;
            final Function<Root<D>, Join<Y, Z>> currentJoin
                = createAttributeFunctionForNestedRelationship(parentJoin,
                    castAttribute);
            return createListAttributeFunctionRecursive(currentJoin, it);
        }
    }

    private <
        Y> Function<Root<D>, Join<D, Y>> createAttributeFunctionForRelationship(
            final SingularAttribute<D, Y> attribute) {

        return root -> root.join(attribute, JoinType.LEFT);
    }

    private <
        X, Y, Z> Function<Root<D>, Join<Y, Z>>
        createAttributeFunctionForNestedRelationship(
            final Function<Root<D>, Join<X, Y>> join,
            final SingularAttribute<Y, Z> attribute) {

        return root -> join.apply(root).join(attribute, JoinType.LEFT);
    }

    /**
     * Add a list of filtering conditions based on the given
     * {@link CommonFilter}.
     *
     * @param  <T>           The value type.
     * @param  filter        The CommonFilter containing the filter conditions.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the {@link DataPresentation} root.
     * @return               {@code true} if the filter contains an equal or in
     *                       criteria, {@code false} otherwise.
     */
    protected <T extends Serializable> boolean applyFilter(
        final CommonFilter<T> filter,
        final Function<Root<D>, Expression<T>> attributeFunc) {

        final T eq = filter.getEqual();

        if (eq != null) {

            specifications.add(eq(attributeFunc, eq));
            return true;
        } else if (!filter.getIn().isEmpty()) {

            specifications.add(in(attributeFunc, filter.getIn()));
            return true;
        } else {

            final T neq = filter.getNotEqual();

            if (neq != null) {

                specifications.add(neq(attributeFunc, neq));
            }

            if (!filter.getNotIn().isEmpty()) {

                specifications.add(nin(attributeFunc, filter.getNotIn()));
            }

            if (Boolean.TRUE.equals(filter.getIsNull())) {

                specifications.add(isNull(attributeFunc));
            }

            if (Boolean.FALSE.equals(filter.getIsNull())) {

                specifications.add(isNotNull(attributeFunc));
            }
            return false;
        }
    }

    /**
     * Add a list of filtering conditions based on the given BooleanFilter.
     *
     * @param filter        The BooleanFilter containing the filter conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     */
    protected void applyFilter(
        final BooleanFilter filter,
        final Function<Root<D>, Expression<Boolean>> attributeFunc) {

        if (!applyFilter((CommonFilter<Boolean>) filter, attributeFunc)) {

            if (Boolean.TRUE.equals(filter.getIsTrue())) {

                specifications.add(isTrue(attributeFunc));
            }

            if (Boolean.FALSE.equals(filter.getIsTrue())) {

                specifications.add(isFalse(attributeFunc));
            }
        }
    }

    /**
     * Add a list of filtering conditions to the provided Specification based on
     * the given NumberFilter.
     *
     * @param <N>           The value type.
     * @param filter        The NumberFilter containing the filter conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     */
    protected <N extends Number> void applyFilter(
        final NumberFilter<N> filter,
        final Function<Root<D>, Expression<N>> attributeFunc) {

        if (!applyFilter((CommonFilter<N>) filter, attributeFunc)) {

            final N gt = filter.getGreaterThan();

            if (gt != null) {

                specifications.add(gt(attributeFunc, gt));
            }
            final N gte = filter.getGreaterThanOrEqual();

            if (gte != null) {

                specifications.add(gte(attributeFunc, gte));
            }
            final N lt = filter.getLessThan();

            if (lt != null) {

                specifications.add(lt(attributeFunc, lt));
            }
            final N lte = filter.getLessThanOrEqual();

            if (lte != null) {

                specifications.add(lte(attributeFunc, lte));
            }
        }
    }

    /**
     * Add a list of filtering conditions to the provided Specification based on
     * the given TextFilter.
     *
     * @param filter        The TextFilter containing the filter conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     */
    protected void applyFilter(
        final TextFilter filter,
        final Function<Root<D>, Expression<String>> attributeFunc) {

        if (!applyFilter((CommonFilter<String>) filter, attributeFunc)) {

            if (Boolean.TRUE.equals(filter.getCaseSensitive())) {

                applyTextFilterSensitive(filter, attributeFunc);
            } else {

                applyTextFilterNotSensitive(filter, attributeFunc);
            }
        }
    }

    /**
     * Add a list of filtering conditions to the provided Specification based on
     * the given DateFilter.
     *
     * @param <T>           The value type.
     * @param filter        The DateFilter containing the filter conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     */
    protected <
        T extends Temporal & Comparable<? super T> & Serializable> void
        applyFilter(
            final DateFilter<T> filter,
            final Function<Root<D>, Expression<T>> attributeFunc) {

        if (!applyFilter((CommonFilter<T>) filter, attributeFunc)) {

            final T from = filter.getFrom();

            if (from != null) {

                specifications.add(from(attributeFunc, from));
            }
            final T to = filter.getTo();

            if (to != null) {

                specifications.add(to(attributeFunc, to));
            }
        }
    }

    /**
     * Add a list of filtering conditions to the provided Specification based on
     * the given CollectionFilter.
     *
     * @param <T>           The value type.
     * @param filter        The CollectionFilter containing the filter
     *                      conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     */
    protected <T extends Serializable> void applyFilter(
        final CollectionFilter<T> filter,
        final Function<Root<D>, Expression<List<T>>> attributeFunc) {

        if (!filter.getEqual().isEmpty()) {

            specifications.add(eq(attributeFunc, filter.getEqual()));
        } else {

            if (!filter.getNotEqual().isEmpty()) {

                specifications.add(neq(attributeFunc, filter.getNotEqual()));
            }

            if (Boolean.TRUE.equals(filter.getIsEmpty())) {

                specifications.add(isEmpty(attributeFunc));
            }

            if (Boolean.FALSE.equals(filter.getIsEmpty())) {

                specifications.add(isNotEmpty(attributeFunc));
            }
            final T isMember = filter.getIsMember();

            if (isMember != null) {

                specifications.add(isMember(attributeFunc, isMember));
            }
            final T isNotMember = filter.getIsNotMember();

            if (isNotMember != null) {

                specifications.add(isNotMember(attributeFunc, isNotMember));
            }
        }
    }

    /**
     * Add a list of filtering conditions based on the given TextFilter with
     * case-sensitive.
     *
     * @param filter        The NumberFilter containing the filter conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     * @see                 SpecificationBuilderImpl#applyFilter(TextFilter,
     *                      Function)
     */
    protected void applyTextFilterSensitive(
        final TextFilter filter,
        final Function<Root<D>, Expression<String>> attributeFunc) {

        final String containsSensitive = filter.getContains();

        if (containsSensitive != null) {

            specifications
                .add(containsSensitive(attributeFunc, containsSensitive));
        }
        final String notContainsSensitive = filter.getNotContains();

        if (notContainsSensitive != null) {

            specifications
                .add(notContainsSensitive(attributeFunc, notContainsSensitive));
        }
        final String startWithSensitive = filter.getStartWith();

        if (startWithSensitive != null) {

            specifications
                .add(startWithSensitive(attributeFunc, startWithSensitive));
        }
        final String endWithSensitive = filter.getEndWith();

        if (endWithSensitive != null) {

            specifications
                .add(endWithSensitive(attributeFunc, endWithSensitive));
        }
    }

    /**
     * Add a list of filtering conditions to the provided Specification based on
     * the given TextFilter with case NOT sensitive.
     *
     * @param filter        The NumberFilter containing the filter conditions.
     * @param attributeFunc The function to get the attribute expression from
     *                      the {@link DataPresentation} root.
     * @see                 SpecificationBuilderImpl#applyFilter(TextFilter,
     *                      Function)
     */
    protected void applyTextFilterNotSensitive(
        final TextFilter filter,
        final Function<Root<D>, Expression<String>> attributeFunc) {

        final String contains = filter.getContains();

        if (contains != null) {

            specifications.add(contains(attributeFunc, contains));
        }
        final String notContains = filter.getNotContains();

        if (notContains != null) {

            specifications.add(notContains(attributeFunc, notContains));
        }
        final String startWith = filter.getStartWith();

        if (startWith != null) {

            specifications.add(startWith(attributeFunc, startWith));
        }
        final String endWith = filter.getEndWith();

        if (endWith != null) {

            specifications.add(endWith(attributeFunc, endWith));
        }
    }

    /**
     * Applies distinct filtering to the provided Specification.
     *
     * @param  value The value indicating whether to apply distinct filtering.
     * @return       The Specification with applied distinct filtering.
     */
    protected Specification<D> applyDistinct(final boolean value) {

        return isDistinct(value);
    }
}
