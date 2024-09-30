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

package org.sansenshimizu.sakuraboot.specification.api.business;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;

/**
 * Interface that helps create a {@link Specification}.
 *
 * @param  <D> The entity type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@FunctionalInterface
public interface SpecificationBuilder<D extends DataPresentation<?>>
    extends BiFunction<FilterPresentation<?>, Class<D>, Specification<D>> {

    /**
     * Creates a JPA Specification based on the provided
     * {@link FilterPresentation} to be used for criteria-based filtering of
     * {@link DataPresentation}.
     *
     * @param  filter      The {@link FilterPresentation} object containing
     *                     criteria
     *                     for filtering {@link DataPresentation}.
     * @param  entityClass The class of the entity.
     * @return             A JPA Specification representing the criteria-based
     *                     filter
     *                     for {@link DataPresentation} retrieval.
     */
    Specification<D>
        apply(@Nullable FilterPresentation<?> filter, Class<D> entityClass);

    /**
     * Creates a Specification for the equality condition based on the provided
     * attribute and value.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to match for the equality condition.
     * @return               The Specification with the equality condition.
     */
    default <T> Specification<D> eq(
        final Function<Root<D>, Expression<T>> attributeFunc, final T value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .equal(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the not equal condition based on the provided
     * attribute and value.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to exclude for the not equal condition.
     * @return               The Specification with the not equal condition.
     */
    default <T> Specification<D> neq(
        final Function<Root<D>, Expression<T>> attributeFunc, final T value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .notEqual(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "in" condition based on the provided
     * attribute and a list of values.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  values        The list of values to check for the "in" condition.
     * @return               The Specification with the "in" condition.
     */
    default <T> Specification<D> in(
        final Function<Root<D>, Expression<T>> attributeFunc,
        final Iterable<T> values) {

        return (
            final Root<D> root, final CriteriaQuery<?> query,
            final CriteriaBuilder cb) -> {

            final CriteriaBuilder.In<T> in = cb.in(attributeFunc.apply(root));
            values.forEach(in::value);
            return in;
        };
    }

    /**
     * Creates a Specification for the "not in" condition based on the provided
     * attribute and a list of values.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  values        The list of values to check for the "not in"
     *                       condition.
     * @return               The Specification with the "not in" condition.
     */
    default <T> Specification<D> nin(
        final Function<Root<D>, Expression<T>> attributeFunc,
        final Iterable<T> values) {

        return (
            final Root<D> root, final CriteriaQuery<?> query,
            final CriteriaBuilder cb) -> {

            final CriteriaBuilder.In<T> in = cb.in(attributeFunc.apply(root));
            values.forEach(in::value);
            return cb.not(in);
        };
    }

    /**
     * Creates a Specification for the "is null" condition based on the provided
     * attribute and a value.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @return               The Specification with the "is null" condition.
     */
    default <T> Specification<D> isNull(
        final Function<Root<D>, Expression<T>> attributeFunc) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isNull(attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "is not null" condition based on the
     * provided attribute and a value.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @return               The Specification with the "is not null" condition.
     */
    default <T> Specification<D> isNotNull(
        final Function<Root<D>, Expression<T>> attributeFunc) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isNotNull(attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "is true" condition based on the provided
     * attribute and a value.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @return               The Specification with the "is true" condition.
     */
    default Specification<D> isTrue(
        final Function<Root<D>, Expression<Boolean>> attributeFunc) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isTrue(attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "is false" condition based on the
     * provided attribute and a value.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @return               The Specification with the "is false" condition.
     */
    default Specification<D> isFalse(
        final Function<Root<D>, Expression<Boolean>> attributeFunc) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isFalse(attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "greater than" condition based on the
     * provided attribute and value.
     *
     * @param  <N>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to compare for the "greater than"
     *                       condition.
     * @return               The Specification with the "greater than"
     *                       condition.
     */
    default <N extends Number> Specification<D> gt(
        final Function<Root<D>, Expression<N>> attributeFunc, final N value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .gt(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "greater than or equal" condition based
     * on the provided attribute and value.
     *
     * @param  <N>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to compare for the "greater than or
     *                       equal" condition.
     * @return               The Specification with the "greater than or equal"
     *                       condition.
     */
    default <N extends Number> Specification<D> gte(
        final Function<Root<D>, Expression<N>> attributeFunc, final N value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .ge(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "less than" condition based on the
     * provided attribute and value.
     *
     * @param  <N>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to compare for the "less than" condition.
     * @return               The Specification with the "less than" condition.
     */
    default <N extends Number> Specification<D> lt(
        final Function<Root<D>, Expression<N>> attributeFunc, final N value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .lt(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "less than or equal" condition based on
     * the provided attribute and value.
     *
     * @param  <N>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to compare for the "less than or equal"
     *                       condition.
     * @return               The Specification with the "less than or equal"
     *                       condition.
     */
    default <N extends Number> Specification<D> lte(
        final Function<Root<D>, Expression<N>> attributeFunc, final N value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .le(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "contains" condition based on the
     * provided attribute and value.
     * The comparison is case-insensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the "contains" condition.
     * @return               The Specification with the "contains" condition.
     */
    default Specification<D> contains(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .like(cb.upper(attributeFunc.apply(root)),
                "%" + value.toUpperCase(Locale.ENGLISH) + "%");
    }

    /**
     * Creates a Specification for the "contains" condition based on the
     * provided attribute and value.
     * The comparison is case-sensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the case-sensitive
     *                       "contains" condition.
     * @return               The Specification with the case-sensitive
     *                       "contains" condition.
     */
    default Specification<D> containsSensitive(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .like(attributeFunc.apply(root), "%" + value + "%");
    }

    /**
     * Creates a Specification for the "not contains" condition based on the
     * provided attribute and value.
     * The comparison is case-insensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the "not contains"
     *                       condition.
     * @return               The Specification with the "not contains"
     *                       condition.
     */
    default Specification<D> notContains(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .notLike(cb.upper(attributeFunc.apply(root)),
                "%" + value.toUpperCase(Locale.ENGLISH) + "%");
    }

    /**
     * Creates a Specification for the "not contains" condition based on the
     * provided attribute and value.
     * The comparison is case-sensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the case-sensitive "not
     *                       contains" condition.
     * @return               The Specification with the case-sensitive "not
     *                       contains" condition.
     */
    default Specification<D> notContainsSensitive(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .notLike(attributeFunc.apply(root), "%" + value + "%");
    }

    /**
     * Creates a Specification for the "starts with" condition based on the
     * provided attribute and value.
     * The comparison is case-insensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the "starts with" condition.
     * @return               The Specification with the "starts with" condition.
     */
    default Specification<D> startWith(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .like(cb.upper(attributeFunc.apply(root)),
                value.toUpperCase(Locale.ENGLISH) + "%");
    }

    /**
     * Creates a Specification for the "starts with" condition based on the
     * provided attribute and value.
     * The comparison is case-sensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the case-sensitive "starts
     *                       with" condition.
     * @return               The Specification with the case-sensitive "starts
     *                       with" condition.
     */
    default Specification<D> startWithSensitive(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .like(attributeFunc.apply(root), value + "%");
    }

    /**
     * Creates a Specification for the "ends with" condition based on the
     * provided attribute and value.
     * The comparison is case-insensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the "ends with" condition.
     * @return               The Specification with the "ends with" condition.
     */
    default Specification<D> endWith(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .like(cb.upper(attributeFunc.apply(root)),
                "%" + value.toUpperCase(Locale.ENGLISH));
    }

    /**
     * Creates a Specification for the "ends with" condition based on the
     * provided attribute and value.
     * The comparison is case-sensitive.
     *
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for the case-sensitive "ends
     *                       with" condition.
     * @return               The Specification with the case-sensitive "ends
     *                       with" condition.
     */
    default Specification<D> endWithSensitive(
        final Function<Root<D>, Expression<String>> attributeFunc,
        final String value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .like(attributeFunc.apply(root), "%" + value);
    }

    /**
     * Creates a Specification for the "from" condition based
     * on the provided attribute and value.
     * The comparison is inclusive.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to compare for the "from" condition.
     * @return               The Specification with the "from" condition.
     */
    default <T extends Temporal & Comparable<? super T>> Specification<D> from(
        final Function<Root<D>, Expression<T>> attributeFunc, final T value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .greaterThanOrEqualTo(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "to" condition based
     * on the provided attribute and value.
     * The comparison is inclusive.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to compare for the "to" condition.
     * @return               The Specification with the "to" condition.
     */
    default <T extends Temporal & Comparable<? super T>> Specification<D> to(
        final Function<Root<D>, Expression<T>> attributeFunc, final T value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .lessThanOrEqualTo(attributeFunc.apply(root), value);
    }

    /**
     * Creates a Specification for the "is empty" condition based on the
     * provided attribute and a value.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @return               The Specification with the "is empty" condition.
     */
    default <T> Specification<D> isEmpty(
        final Function<Root<D>, Expression<List<T>>> attributeFunc) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isEmpty(attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "is not empty" condition based on the
     * provided attribute and a value.
     *
     * @param  <T>           The value type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @return               The Specification with the "is not empty"
     *                       condition.
     */
    default <T> Specification<D> isNotEmpty(
        final Function<Root<D>, Expression<List<T>>> attributeFunc) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isNotEmpty(attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "is member" condition based on the
     * provided attribute and a value.
     *
     * @param  <T>           The value type.
     * @param  <C>           The list type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for being a member of the list.
     * @return               The Specification with the "is member" condition.
     */
    default <T, C extends List<T>> Specification<D> isMember(
        final Function<Root<D>, Expression<C>> attributeFunc, final T value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isMember(value, attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for the "is not member" condition based on the
     * provided attribute and a value.
     *
     * @param  <T>           The value type.
     * @param  <C>           The list type.
     * @param  attributeFunc The function to get the attribute expression from
     *                       the entity root.
     * @param  value         The value to check for not being a member of the
     *                       list.
     * @return               The Specification with the "is not member"
     *                       condition.
     */
    default <T, C extends List<T>> Specification<D> isNotMember(
        final Function<Root<D>, Expression<C>> attributeFunc, final T value) {

        return (Root<D> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> cb
            .isNotMember(value, attributeFunc.apply(root));
    }

    /**
     * Creates a Specification for setting the "distinct" flag on the query
     * based on the provided value.
     *
     * @param  value The boolean value indicating whether to set the "distinct"
     *               flag on the query.
     * @return       The Specification for setting the "distinct" flag on the
     *               query.
     */
    default Specification<D> isDistinct(final boolean value) {

        return (
            final Root<D> root, final CriteriaQuery<?> query,
            final CriteriaBuilder cb) -> {

            query.distinct(value);
            return null;
        };
    }
}
