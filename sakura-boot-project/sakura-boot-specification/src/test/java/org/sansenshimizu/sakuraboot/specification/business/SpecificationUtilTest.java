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
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import org.mockito.ArgumentMatchers;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.BooleanFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CollectionFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.DateFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.Filter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.TextFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Utility test class for building filtering conditions for the
 * {@link SpecificationBuilderTest}.
 *
 * @author Malcolm Rozé
 * @see    SpecificationBuilderTest
 * @since  0.1.0
 */
public sealed interface SpecificationUtilTest permits SpecificationBuilderTest {

    /**
     * The getter for the {@link CriteriaBuilder} use in test.
     *
     * @return The {@link CriteriaBuilder} use in test.
     */
    CriteriaBuilder getCriteriaBuilder();

    /**
     * The getter for the {@link CommonFilter} use in test.
     *
     * @return The {@link CommonFilter} use in test.
     */
    CommonFilter<Character> getCommonFilter();

    /**
     * The getter for the {@link CommonFilter} value use in test.
     *
     * @return The {@link CommonFilter} value use in test.
     */
    Character getCommonFilterValue();

    /**
     * The getter for the {@link Path} use in test for the {@link CommonFilter}.
     *
     * @return The {@link Path} use in test for the {@link CommonFilter}.
     */
    Path<Character> getCommonPath();

    /**
     * The getter for the {@link BooleanFilter} use in test.
     *
     * @return The {@link BooleanFilter} use in test.
     */
    BooleanFilter getBooleanFilter();

    /**
     * The getter for the {@link BooleanFilter} value use in test.
     *
     * @return The {@link BooleanFilter} value use in test.
     */
    Boolean getBooleanFilterValue();

    /**
     * The getter for the {@link Path} use in test for the
     * {@link BooleanFilter}.
     *
     * @return The {@link Path} use in test for the {@link BooleanFilter}.
     */
    Path<Boolean> getBooleanPath();

    /**
     * The getter for the {@link NumberFilter} use in test.
     *
     * @return The {@link NumberFilter} use in test.
     */
    NumberFilter<Double> getNumberFilter();

    /**
     * The getter for the {@link NumberFilter} value use in test.
     *
     * @return The {@link NumberFilter} value use in test.
     */
    Double getNumberFilterValue();

    /**
     * The getter for the {@link Path} use in test for the {@link NumberFilter}.
     *
     * @return The {@link Path} use in test for the {@link NumberFilter}.
     */
    Path<Double> getNumberPath();

    /**
     * The getter for the {@link TextFilter} use in test.
     *
     * @return The {@link TextFilter} use in test.
     */
    TextFilter getTextFilter();

    /**
     * The getter for the {@link TextFilter} value use in test.
     *
     * @return The {@link TextFilter} value use in test.
     */
    String getTextFilterValue();

    /**
     * The getter for the {@link Path} use in test for the {@link TextFilter}.
     *
     * @return The {@link Path} use in test for the {@link TextFilter}.
     */
    Path<String> getTextPath();

    /**
     * The getter for the {@link DateFilter} use in test.
     *
     * @return The {@link DateFilter} use in test.
     */
    DateFilter<LocalDate> getDateFilter();

    /**
     * The getter for the {@link DateFilter} value use in test.
     *
     * @return The {@link DateFilter} value use in test.
     */
    LocalDate getDateFilterValue();

    /**
     * The getter for the {@link Path} use in test for the {@link DateFilter}.
     *
     * @return The {@link Path} use in test for the {@link DateFilter}.
     */
    Path<LocalDate> getDatePath();

    /**
     * The getter for the {@link CollectionFilter} use in test.
     *
     * @return The {@link CollectionFilter} use in test.
     */
    CollectionFilter<Integer> getCollectionFilter();

    /**
     * The getter for the {@link CollectionFilter} value use in test.
     *
     * @return The {@link CollectionFilter} value use in test.
     */
    List<Integer> getCollectionFilterValue();

    /**
     * The getter for the {@link Path} use in test for the
     * {@link CollectionFilter}.
     *
     * @return The {@link Path} use in test for the {@link CollectionFilter}.
     */
    Path<List<Integer>> getCollectionPath();

    private static <T extends Serializable> void setNullParameter(
        final CommonFilter<T> filter) {

        given(filter.getIn()).willReturn(List.of());
        given(filter.getNotIn()).willReturn(List.of());
        given(filter.getIsNull()).willReturn(null);
    }

    private static void setNullParameterForBoolean(final BooleanFilter filter) {

        given(filter.getEqual()).willReturn(null);
        given(filter.getNotEqual()).willReturn(null);
        given(filter.getIsTrue()).willReturn(null);
    }

    private static <T extends Serializable> void setNullParameterForCollection(
        final CollectionFilter<T> filter) {

        given(filter.getEqual()).willReturn(List.of());
        given(filter.getNotEqual()).willReturn(List.of());
        given(filter.getIsEmpty()).willReturn(null);
    }

    private <T extends Serializable> void givenEqual(
        final CommonFilter<T> filter, final T value,
        final Predicate predicate) {

        given(filter.getEqual()).willReturn(value);
        given(getCriteriaBuilder().equal(any(), ArgumentMatchers.<T>any()))
            .willReturn(predicate);
    }

    private <T extends Serializable> void givenEqualCollection(
        final CollectionFilter<T> filter, final List<T> value,
        final Predicate predicate) {

        given(filter.getEqual()).willReturn(value);
        given(getCriteriaBuilder().equal(any(), ArgumentMatchers.<T>any()))
            .willReturn(predicate);
    }

    private <T extends Serializable> Predicate givenIn(
        final CommonFilter<T> filter, final T value) {

        final CriteriaBuilder.In<T> in = mock();
        given(filter.getIn()).willReturn(List.of(value));
        given(getCriteriaBuilder().in(ArgumentMatchers.<Path<T>>any()))
            .willReturn(in);
        given(in.value(value)).willReturn(in);
        return in;
    }

    private <T extends Serializable> void givenNotEqual(
        final CommonFilter<T> filter, final T value,
        final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getNotEqual()).willReturn(value);
        given(getCriteriaBuilder().notEqual(any(), ArgumentMatchers.<T>any()))
            .willReturn(predicate);
    }

    private <T extends Serializable> void givenNotEqualCollection(
        final CollectionFilter<T> filter, final List<T> value,
        final Predicate predicate) {

        setNullParameterForCollection(filter);
        given(filter.getNotEqual()).willReturn(value);
        given(getCriteriaBuilder().notEqual(any(), ArgumentMatchers.<T>any()))
            .willReturn(predicate);
    }

    private <T extends Serializable> void givenNotIn(
        final CommonFilter<T> filter, final T value,
        final Predicate predicate) {

        setNullParameter(filter);
        final CriteriaBuilder.In<T> in = mock();
        given(filter.getNotIn()).willReturn(List.of(value));
        given(getCriteriaBuilder().in(ArgumentMatchers.<Path<T>>any()))
            .willReturn(in);
        given(in.value(ArgumentMatchers.<T>any())).willReturn(in);
        given(getCriteriaBuilder().not(any())).willReturn(predicate);
    }

    private <T extends Serializable> void givenIsNull(
        final CommonFilter<T> filter, final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getIsNull()).willReturn(true);
        given(getCriteriaBuilder().isNull(any())).willReturn(predicate);
    }

    private <T extends Serializable> void givenIsNotNull(
        final CommonFilter<T> filter, final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getIsNull()).willReturn(false);
        given(getCriteriaBuilder().isNotNull(any())).willReturn(predicate);
    }

    private void givenIsTrue(
        final BooleanFilter filter, final Predicate predicate) {

        setNullParameter(filter);
        setNullParameterForBoolean(filter);
        given(filter.getIsTrue()).willReturn(true);
        given(getCriteriaBuilder().isTrue(any())).willReturn(predicate);
    }

    private void givenIsFalse(
        final BooleanFilter filter, final Predicate predicate) {

        setNullParameter(filter);
        setNullParameterForBoolean(filter);
        given(filter.getIsTrue()).willReturn(false);
        given(getCriteriaBuilder().isFalse(any())).willReturn(predicate);
    }

    private <N extends Number> void givenGreaterThan(
        final NumberFilter<N> filter, final N value,
        final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getGreaterThan()).willReturn(value);
        given(getCriteriaBuilder().gt(ArgumentMatchers.<Path<N>>any(),
            ArgumentMatchers.<N>any())).willReturn(predicate);
    }

    private <N extends Number> void givenGreaterThanOrEqual(
        final NumberFilter<N> filter, final N value,
        final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getGreaterThanOrEqual()).willReturn(value);
        given(getCriteriaBuilder().ge(ArgumentMatchers.<Path<N>>any(),
            ArgumentMatchers.<N>any())).willReturn(predicate);
    }

    private <N extends Number> void givenLessThan(
        final NumberFilter<N> filter, final N value,
        final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getLessThan()).willReturn(value);
        given(getCriteriaBuilder().lt(ArgumentMatchers.<Path<N>>any(),
            ArgumentMatchers.<N>any())).willReturn(predicate);
    }

    private <N extends Number> void givenLessThanOrEqual(
        final NumberFilter<N> filter, final N value,
        final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getLessThanOrEqual()).willReturn(value);
        given(getCriteriaBuilder().le(ArgumentMatchers.<Path<N>>any(),
            ArgumentMatchers.<N>any())).willReturn(predicate);
    }

    private void givenContains(
        final TextFilter filter, final String value, final Predicate predicate,
        final boolean sensitive) {

        setNullParameter(filter);
        given(filter.getCaseSensitive()).willReturn(sensitive);
        given(filter.getContains()).willReturn(value);

        if (!sensitive) {

            given(getCriteriaBuilder().upper(any()))
                .willAnswer(invocation -> invocation.getArguments()[0]);
        }
        given(getCriteriaBuilder().like(any(), ArgumentMatchers.<String>any()))
            .willReturn(predicate);
    }

    private void givenNotContains(
        final TextFilter filter, final String value, final Predicate predicate,
        final boolean sensitive) {

        setNullParameter(filter);
        given(filter.getCaseSensitive()).willReturn(sensitive);
        given(filter.getNotContains()).willReturn(value);

        if (!sensitive) {

            given(getCriteriaBuilder().upper(any()))
                .willAnswer(invocation -> invocation.getArguments()[0]);
        }
        given(
            getCriteriaBuilder().notLike(any(), ArgumentMatchers.<String>any()))
            .willReturn(predicate);
    }

    private void givenStartWith(
        final TextFilter filter, final String value, final Predicate predicate,
        final boolean sensitive) {

        setNullParameter(filter);
        given(filter.getCaseSensitive()).willReturn(sensitive);
        given(filter.getStartWith()).willReturn(value);

        if (!sensitive) {

            given(getCriteriaBuilder().upper(any()))
                .willAnswer(invocation -> invocation.getArguments()[0]);
        }
        given(getCriteriaBuilder().like(any(), ArgumentMatchers.<String>any()))
            .willReturn(predicate);
    }

    private void givenEndWith(
        final TextFilter filter, final String value, final Predicate predicate,
        final boolean sensitive) {

        setNullParameter(filter);
        given(filter.getCaseSensitive()).willReturn(sensitive);
        given(filter.getEndWith()).willReturn(value);

        if (!sensitive) {

            given(getCriteriaBuilder().upper(any()))
                .willAnswer(invocation -> invocation.getArguments()[0]);
        }
        given(getCriteriaBuilder().like(any(), ArgumentMatchers.<String>any()))
            .willReturn(predicate);
    }

    private <
        T extends Temporal & Comparable<? super T> & Serializable> void
        givenFrom(
            final DateFilter<T> filter, final T value,
            final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getFrom()).willReturn(value);
        given(getCriteriaBuilder().greaterThanOrEqualTo(
            ArgumentMatchers.<Path<T>>any(), ArgumentMatchers.<T>any()))
            .willReturn(predicate);
    }

    private <
        T extends Temporal & Comparable<? super T> & Serializable> void givenTo(
            final DateFilter<T> filter, final T value,
            final Predicate predicate) {

        setNullParameter(filter);
        given(filter.getTo()).willReturn(value);
        given(getCriteriaBuilder().lessThanOrEqualTo(
            ArgumentMatchers.<Path<T>>any(), ArgumentMatchers.<T>any()))
            .willReturn(predicate);
    }

    private <T extends Serializable> void givenIsEmpty(
        final CollectionFilter<T> filter, final Predicate predicate) {

        setNullParameterForCollection(filter);
        given(filter.getIsEmpty()).willReturn(true);
        given(getCriteriaBuilder().isEmpty(any())).willReturn(predicate);
    }

    private <T extends Serializable> void givenIsNotEmpty(
        final CollectionFilter<T> filter, final Predicate predicate) {

        setNullParameterForCollection(filter);
        given(filter.getIsEmpty()).willReturn(false);
        given(getCriteriaBuilder().isNotEmpty(any())).willReturn(predicate);
    }

    private <T extends Serializable> void givenIsMember(
        final CollectionFilter<T> filter, final T value,
        final Predicate predicate) {

        setNullParameterForCollection(filter);
        given(filter.getIsMember()).willReturn(value);
        given(getCriteriaBuilder().isMember(ArgumentMatchers.<T>any(), any()))
            .willReturn(predicate);
    }

    private <T extends Serializable> void givenIsNotMember(
        final CollectionFilter<T> filter, final T value,
        final Predicate predicate) {

        setNullParameterForCollection(filter);
        given(filter.getIsNotMember()).willReturn(value);
        given(
            getCriteriaBuilder().isNotMember(ArgumentMatchers.<T>any(), any()))
            .willReturn(predicate);
    }

    /**
     * Create the expected predicate from the filter name and the filter to
     * test.
     *
     * @param  filterName The name of the filter.
     * @param  filter     The filter.
     * @return            The expected predicate.
     */
    @Nullable
    default Predicate getExpectedPredicate(
        final String filterName, @Nullable final Filter filter) {

        final Predicate predicate = mock(Predicate.class);

        return switch (filterName) {

            case CommonFilterName.EQUAL_NAME -> {

                if (filter == getCommonFilter()) {

                    givenEqual(getCommonFilter(), getCommonFilterValue(),
                        predicate);
                }

                if (filter == getBooleanFilter()) {

                    givenEqual(getBooleanFilter(), getBooleanFilterValue(),
                        predicate);
                }

                if (filter == getNumberFilter()) {

                    givenEqual(getNumberFilter(), getNumberFilterValue(),
                        predicate);
                }

                if (filter == getTextFilter()) {

                    givenEqual(getTextFilter(), getTextFilterValue(),
                        predicate);
                }

                if (filter == getDateFilter()) {

                    givenEqual(getDateFilter(), getDateFilterValue(),
                        predicate);
                }

                if (filter == getCollectionFilter()) {

                    givenEqualCollection(getCollectionFilter(),
                        getCollectionFilterValue(), predicate);
                }
                yield predicate;
            }
            case CommonFilterName.IN_NAME -> {

                if (filter == getCommonFilter()) {

                    yield givenIn(getCommonFilter(), getCommonFilterValue());
                }

                if (filter == getBooleanFilter()) {

                    yield givenIn(getBooleanFilter(), getBooleanFilterValue());
                }

                if (filter == getNumberFilter()) {

                    yield givenIn(getNumberFilter(), getNumberFilterValue());
                }

                if (filter == getDateFilter()) {

                    yield givenIn(getDateFilter(), getDateFilterValue());
                }

                if (filter == getTextFilter()) {

                    yield givenIn(getTextFilter(), getTextFilterValue());
                }
                yield predicate;
            }
            case CommonFilterName.NOT_EQUAL_NAME -> {

                if (filter == getCommonFilter()) {

                    givenNotEqual(getCommonFilter(), getCommonFilterValue(),
                        predicate);
                }

                if (filter == getBooleanFilter()) {

                    setNullParameterForBoolean(getBooleanFilter());
                    givenNotEqual(getBooleanFilter(), getBooleanFilterValue(),
                        predicate);
                }

                if (filter == getNumberFilter()) {

                    givenNotEqual(getNumberFilter(), getNumberFilterValue(),
                        predicate);
                }

                if (filter == getTextFilter()) {

                    givenNotEqual(getTextFilter(), getTextFilterValue(),
                        predicate);
                }

                if (filter == getDateFilter()) {

                    givenNotEqual(getDateFilter(), getDateFilterValue(),
                        predicate);
                }

                if (filter == getCollectionFilter()) {

                    givenNotEqualCollection(getCollectionFilter(),
                        getCollectionFilterValue(), predicate);
                }
                yield predicate;
            }
            case CommonFilterName.NOT_IN_NAME -> {

                if (filter == getCommonFilter()) {

                    givenNotIn(getCommonFilter(), getCommonFilterValue(),
                        predicate);
                }

                if (filter == getBooleanFilter()) {

                    setNullParameterForBoolean(getBooleanFilter());
                    givenNotIn(getBooleanFilter(), getBooleanFilterValue(),
                        predicate);
                }

                if (filter == getNumberFilter()) {

                    givenNotIn(getNumberFilter(), getNumberFilterValue(),
                        predicate);
                }

                if (filter == getTextFilter()) {

                    givenNotIn(getTextFilter(), getTextFilterValue(),
                        predicate);
                }

                if (filter == getDateFilter()) {

                    givenNotIn(getDateFilter(), getDateFilterValue(),
                        predicate);
                }
                yield predicate;
            }
            case CommonFilterName.IS_NULL_NAME -> {

                if (filter == getCommonFilter()) {

                    givenIsNull(getCommonFilter(), predicate);
                }

                if (filter == getBooleanFilter()) {

                    setNullParameterForBoolean(getBooleanFilter());
                    givenIsNull(getBooleanFilter(), predicate);
                }

                if (filter == getNumberFilter()) {

                    givenIsNull(getNumberFilter(), predicate);
                }

                if (filter == getTextFilter()) {

                    givenIsNull(getTextFilter(), predicate);
                }

                if (filter == getDateFilter()) {

                    givenIsNull(getDateFilter(), predicate);
                }
                yield predicate;
            }
            case CommonFilterName.IS_NOT_NULL_NAME -> {

                if (filter == getCommonFilter()) {

                    givenIsNotNull(getCommonFilter(), predicate);
                }

                if (filter == getBooleanFilter()) {

                    setNullParameterForBoolean(getBooleanFilter());
                    givenIsNotNull(getBooleanFilter(), predicate);
                }

                if (filter == getNumberFilter()) {

                    givenIsNotNull(getNumberFilter(), predicate);
                }

                if (filter == getTextFilter()) {

                    givenIsNotNull(getTextFilter(), predicate);
                }

                if (filter == getDateFilter()) {

                    givenIsNotNull(getDateFilter(), predicate);
                }
                yield predicate;
            }
            case BooleanFilterName.IS_TRUE_NAME -> {

                if (filter == getBooleanFilter()) {

                    givenIsTrue(getBooleanFilter(), predicate);
                }
                yield predicate;
            }
            case BooleanFilterName.IS_FALSE_NAME -> {

                if (filter == getBooleanFilter()) {

                    givenIsFalse(getBooleanFilter(), predicate);
                }
                yield predicate;
            }
            case NumberFilterName.GREATER_THAN_NAME -> {

                if (filter == getNumberFilter()) {

                    givenGreaterThan(getNumberFilter(), getNumberFilterValue(),
                        predicate);
                }
                yield predicate;
            }
            case NumberFilterName.GREATER_THAN_OR_EQUAL_NAME -> {

                if (filter == getNumberFilter()) {

                    givenGreaterThanOrEqual(getNumberFilter(),
                        getNumberFilterValue(), predicate);
                }
                yield predicate;
            }
            case NumberFilterName.LESS_THAN_NAME -> {

                if (filter == getNumberFilter()) {

                    givenLessThan(getNumberFilter(), getNumberFilterValue(),
                        predicate);
                }
                yield predicate;
            }
            case NumberFilterName.LESS_THAN_OR_EQUAL_NAME -> {

                if (filter == getNumberFilter()) {

                    givenLessThanOrEqual(getNumberFilter(),
                        getNumberFilterValue(), predicate);
                }
                yield predicate;
            }
            case TextFilterName.CONTAINS_NAME -> {

                if (filter == getTextFilter()) {

                    givenContains(getTextFilter(), getTextFilterValue(),
                        predicate, false);
                }
                yield predicate;
            }
            case TextFilterName.CONTAINS_SENSITIVE_NAME -> {

                if (filter == getTextFilter()) {

                    givenContains(getTextFilter(), getTextFilterValue(),
                        predicate, true);
                }
                yield predicate;
            }
            case TextFilterName.NOT_CONTAINS_NAME -> {

                if (filter == getTextFilter()) {

                    givenNotContains(getTextFilter(), getTextFilterValue(),
                        predicate, false);
                }
                yield predicate;
            }
            case TextFilterName.NOT_CONTAINS_SENSITIVE_NAME -> {

                if (filter == getTextFilter()) {

                    givenNotContains(getTextFilter(), getTextFilterValue(),
                        predicate, true);
                }
                yield predicate;
            }
            case TextFilterName.START_WITH_NAME -> {

                if (filter == getTextFilter()) {

                    givenStartWith(getTextFilter(), getTextFilterValue(),
                        predicate, false);
                }
                yield predicate;
            }
            case TextFilterName.START_WITH_SENSITIVE_NAME -> {

                if (filter == getTextFilter()) {

                    givenStartWith(getTextFilter(), getTextFilterValue(),
                        predicate, true);
                }
                yield predicate;
            }
            case TextFilterName.END_WITH_NAME -> {

                if (filter == getTextFilter()) {

                    givenEndWith(getTextFilter(), getTextFilterValue(),
                        predicate, false);
                }
                yield predicate;
            }
            case TextFilterName.END_WITH_SENSITIVE_NAME -> {

                if (filter == getTextFilter()) {

                    givenEndWith(getTextFilter(), getTextFilterValue(),
                        predicate, true);
                }
                yield predicate;
            }
            case DateFilterName.FROM_NAME -> {

                if (filter == getDateFilter()) {

                    givenFrom(getDateFilter(), getDateFilterValue(), predicate);
                }
                yield predicate;
            }
            case DateFilterName.TO_NAME -> {

                if (filter == getDateFilter()) {

                    givenTo(getDateFilter(), getDateFilterValue(), predicate);
                }
                yield predicate;
            }
            case CollectionFilterName.IS_EMPTY_NAME -> {

                if (filter == getCollectionFilter()) {

                    givenIsEmpty(getCollectionFilter(), predicate);
                }
                yield predicate;
            }
            case CollectionFilterName.IS_NOT_EMPTY_NAME -> {

                if (filter == getCollectionFilter()) {

                    givenIsNotEmpty(getCollectionFilter(), predicate);
                }
                yield predicate;
            }
            case CollectionFilterName.IS_MEMBER_NAME -> {

                if (filter == getCollectionFilter()) {

                    givenIsMember(getCollectionFilter(),
                        getCollectionFilterValue().get(0), predicate);
                }
                yield predicate;
            }
            case CollectionFilterName.IS_NOT_MEMBER_NAME -> {

                if (filter == getCollectionFilter()) {

                    givenIsNotMember(getCollectionFilter(),
                        getCollectionFilterValue().get(0), predicate);
                }
                yield predicate;
            }
            default -> null;
        };
    }

    /**
     * The different relationship possibility when apply a specification.
     */
    enum RelationshipPossibility {

        /**
         * The name when the attribute doesn't have any relation.
         */
        NONE,

        /**
         * The name when the attribute has only one relation.
         */
        ONE,

        /**
         * The name when the attribute has two relations.
         */
        TWO
    }

    /**
     * The name for all the CommonFilter.
     */
    enum CommonFilterName {

        /**
         * The name of the equal filter.
         */
        EQUAL,
        /**
         * The name of the in filter.
         */
        IN,
        /**
         * The name of the notEqual filter.
         */
        NOT_EQUAL,
        /**
         * The name of the notIn filter.
         */
        NOT_IN,
        /**
         * The name of the isNull filter.
         */
        IS_NULL,
        /**
         * The name of the isNotNull filter.
         */
        IS_NOT_NULL;

        /**
         * The name of the equal filter as a constant value.
         */
        public static final String EQUAL_NAME = "EQUAL";

        /**
         * The name of the in filter as a constant value.
         */
        public static final String IN_NAME = "IN";

        /**
         * The name of the notEqual filter as a constant value.
         */
        public static final String NOT_EQUAL_NAME = "NOT_EQUAL";

        /**
         * The name of the notIn filter as a constant value.
         */
        public static final String NOT_IN_NAME = "NOT_IN";

        /**
         * The name of the isNull filter as a constant value.
         */
        public static final String IS_NULL_NAME = "IS_NULL";

        /**
         * The name of the isNotNull filter as a constant value.
         */
        public static final String IS_NOT_NULL_NAME = "IS_NOT_NULL";
    }

    /**
     * The name for all the BooleanFilter.
     */
    enum BooleanFilterName {

        /**
         * The name of the isTrue filter.
         */
        IS_TRUE,
        /**
         * The name of the isFalse filter.
         */
        IS_FALSE;

        /**
         * The name of the isTrue filter as a constant value.
         */
        public static final String IS_TRUE_NAME = "IS_TRUE";

        /**
         * The name of the isFalse filter as a constant value.
         */
        public static final String IS_FALSE_NAME = "IS_FALSE";
    }

    /**
     * The name for all the NumberFilter.
     */
    enum NumberFilterName {

        /**
         * The name of the greaterThan filter.
         */
        GREATER_THAN,
        /**
         * The name of the greaterThanOrEqualTo filter.
         */
        GREATER_THAN_OR_EQUAL,
        /**
         * The name of the lessThan filter.
         */
        LESS_THAN,
        /**
         * The name of the lessThanOrEqualTo filter.
         */
        LESS_THAN_OR_EQUAL;

        /**
         * The name of the greaterThan filter as a constant value.
         */
        public static final String GREATER_THAN_NAME = "GREATER_THAN";

        /**
         * The name of the greaterThanOrEqualTo filter as a constant value.
         */
        public static final String GREATER_THAN_OR_EQUAL_NAME
            = "GREATER_THAN_OR_EQUAL";

        /**
         * The name of the lessThan filter as a constant value.
         */
        public static final String LESS_THAN_NAME = "LESS_THAN";

        /**
         * The name of the lessThanOrEqualTo filter as a constant value.
         */
        public static final String LESS_THAN_OR_EQUAL_NAME
            = "LESS_THAN_OR_EQUAL";
    }

    /**
     * The name for all the TextFilter.
     */
    enum TextFilterName {

        /**
         * The name of the contains filter.
         */
        CONTAINS,
        /**
         * The name of the containsSensitive filter.
         */
        CONTAINS_SENSITIVE,
        /**
         * The name of the notContains filter.
         */
        NOT_CONTAINS,
        /**
         * The name of the notContainsSensitive filter.
         */
        NOT_CONTAINS_SENSITIVE,
        /**
         * The name of the startWith filter.
         */
        START_WITH,
        /**
         * The name of the startWithSensitive filter.
         */
        START_WITH_SENSITIVE,
        /**
         * The name of the endWith filter.
         */
        END_WITH,
        /**
         * The name of the endWithSensitive filter.
         */
        END_WITH_SENSITIVE;

        /**
         * The name of the contains filter as a constant value.
         */
        public static final String CONTAINS_NAME = "CONTAINS";

        /**
         * The name of the containsSensitive filter as a constant value.
         */
        public static final String CONTAINS_SENSITIVE_NAME
            = "CONTAINS_SENSITIVE";

        /**
         * The name of the notContains filter as a constant value.
         */
        public static final String NOT_CONTAINS_NAME = "NOT_CONTAINS";

        /**
         * The name of the notContainsSensitive filter as a constant value.
         */
        public static final String NOT_CONTAINS_SENSITIVE_NAME
            = "NOT_CONTAINS_SENSITIVE";

        /**
         * The name of the startWith filter as a constant value.
         */
        public static final String START_WITH_NAME = "START_WITH";

        /**
         * The name of the startWithSensitive filter as a constant value.
         */
        public static final String START_WITH_SENSITIVE_NAME
            = "START_WITH_SENSITIVE";

        /**
         * The name of the endWith filter as a constant value.
         */
        public static final String END_WITH_NAME = "END_WITH";

        /**
         * The name of the endWithSensitive filter as a constant value.
         */
        public static final String END_WITH_SENSITIVE_NAME
            = "END_WITH_SENSITIVE";
    }

    /**
     * The name for all the DateFilter.
     */
    enum DateFilterName {

        /**
         * The name of the from filter.
         */
        FROM,
        /**
         * The name of the to filter.
         */
        TO;

        /**
         * The name of the from filter as a constant value.
         */
        public static final String FROM_NAME = "FROM";

        /**
         * The name of the to filter as a constant value.
         */
        public static final String TO_NAME = "TO";
    }

    /**
     * The name for all the CollectionFilter.
     */
    enum CollectionFilterName {

        /**
         * The name of the equal filter.
         */
        EQUAL,
        /**
         * The name of the notEqual filter.
         */
        NOT_EQUAL,
        /**
         * The name of the isEmpty filter.
         */
        IS_EMPTY,
        /**
         * The name of the isNotEmpty filter.
         */
        IS_NOT_EMPTY,
        /**
         * The name of the isMember filter.
         */
        IS_MEMBER,
        /**
         * The name of the isNotMember filter.
         */
        IS_NOT_MEMBER;

        /**
         * The name of the isEmpty filter as a constant value.
         */
        public static final String IS_EMPTY_NAME = "IS_EMPTY";

        /**
         * The name of the isNotEmpty filter as a constant value.
         */
        public static final String IS_NOT_EMPTY_NAME = "IS_NOT_EMPTY";

        /**
         * The name of the isMember filter as a constant value.
         */
        public static final String IS_MEMBER_NAME = "IS_MEMBER";

        /**
         * The name of the isNotMember filter as a constant value.
         */
        public static final String IS_NOT_MEMBER_NAME = "IS_NOT_MEMBER";
    }
}
