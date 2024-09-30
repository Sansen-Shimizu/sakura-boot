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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.BooleanFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CollectionFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.DateFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.Filter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.TextFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The {@link SpecificationBuilderTest} base class provides test for
 * criteria-based specifications for {@link DataPresentation} filtering.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a specific specificationBuilderTest for your specific
 * {@link SpecificationBuilder} that inherits from
 * {@link SpecificationBuilderTest}, follow these steps:
 * </p>
 * <p>
 * Create a new specification builder test class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourSpecificationBuilderTest
 *     extends SpecificationBuilderTest&lt;YourEntity&gt; {
 *     // Add your test or override the getter to customize the existing tests.
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * <b>NOTE:</b> This test class already have all the methods to test the
 * specifications. All the getters are protected so you can customize the tests
 * if needed.
 * </p>
 *
 * @param  <D> The entity type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        SpecificationUtilTest
 * @since      0.1.0
 */
@ExtendWith(MockitoExtension.class)
@Getter
non-sealed class SpecificationBuilderTest<D extends DataPresentation<?>>
    implements SpecificationUtilTest {

    /**
     * The {@link SpecificationBuilderHelper} use for testing.
     */
    @Mock
    private SpecificationBuilderHelper helper;

    /**
     * The {@link SpecificationBuilder} to test.
     */
    private SpecificationBuilder<D> specificationBuilder;

    /**
     * The entity use for testing.
     */
    @Mock
    private D entity;

    /**
     * The entity class use for testing.
     */
    private Class<D> entityClass;

    /**
     * The {@link Root} use for testing.
     */
    @Mock
    private Root<D> root;

    /**
     * The {@link CriteriaQuery} use for testing.
     */
    @Mock
    private CriteriaQuery<?> criteriaQuery;

    /**
     * The {@link CriteriaBuilder} use for testing.
     */
    @Mock
    private CriteriaBuilder criteriaBuilder;

    /**
     * The {@link FilterPresentation} use for testing.
     */
    @Mock
    private FilterPresentation<?> filterPresentation;

    /**
     * The {@link CommonFilter} use for testing.
     */
    @Mock
    private CommonFilter<Character> commonFilter;

    /**
     * The {@link SingularAttribute} use for testing the {@link CommonFilter}.
     */
    @Mock
    private SingularAttribute<D, Character> commonAttribute;

    /**
     * The path for the {@link CommonFilter} use for testing.
     */
    @Mock
    private Path<Character> commonPath;

    /**
     * The {@link BooleanFilter} use for testing.
     */
    @Mock
    private BooleanFilter booleanFilter;

    /**
     * The {@link SingularAttribute} use for testing the {@link BooleanFilter}.
     */
    @Mock
    private SingularAttribute<D, Boolean> booleanAttribute;

    /**
     * The path for the {@link BooleanFilter} use for testing.
     */
    @Mock
    private Path<Boolean> booleanPath;

    /**
     * The {@link NumberFilter} use for testing.
     */
    @Mock
    private NumberFilter<Double> numberFilter;

    /**
     * The {@link SingularAttribute} use for testing the {@link NumberFilter}.
     */
    @Mock
    private SingularAttribute<D, Double> numberAttribute;

    /**
     * The path for the {@link NumberFilter} use for testing.
     */
    @Mock
    private Path<Double> numberPath;

    /**
     * The {@link TextFilter} use for testing.
     */
    @Mock
    private TextFilter textFilter;

    /**
     * The {@link SingularAttribute} use for testing the {@link TextFilter}.
     */
    @Mock
    private SingularAttribute<D, String> textAttribute;

    /**
     * The path for the {@link TextFilter} use for testing.
     */
    @Mock
    private Path<String> textPath;

    /**
     * The {@link DateFilter} use for testing.
     */
    @Mock
    private DateFilter<LocalDate> dateFilter;

    /**
     * The {@link SingularAttribute} use for testing the {@link DateFilter}.
     */
    @Mock
    private SingularAttribute<D, LocalDate> dateAttribute;

    /**
     * The path for the {@link DateFilter} use for testing.
     */
    @Mock
    private Path<LocalDate> datePath;

    /**
     * The {@link CollectionFilter} use for testing.
     */
    @Mock
    private CollectionFilter<Integer> collectionFilter;

    /**
     * The collection value used for testing.
     */
    private final List<Integer> collectionFilterValue
        = new ArrayList<>(List.of(1, 2, 3, 4, 5));

    /**
     * The {@link SingularAttribute} use for testing the
     * {@link CollectionFilter}.
     */
    @Mock
    private ListAttribute<D, Integer> collectionAttribute;

    /**
     * The path for the {@link CollectionFilter} use for testing.
     */
    @Mock
    private Path<List<Integer>> collectionPath;

    /**
     * The {@link FilterPresentation} use in relation for testing.
     */
    @Mock
    private FilterPresentation<?> relationship;

    /**
     * The {@link SingularAttribute} use in relation for testing.
     */
    @Mock
    private SingularAttribute<D, DataPresentation<?>> relationAttribute;

    /**
     * The {@link Join} use in relation for testing.
     */
    @Mock
    private Join<D, DataPresentation<?>> join;

    /**
     * The second {@link FilterPresentation} use in relation for testing.
     */
    @Mock
    private FilterPresentation<?> secondRelationship;

    /**
     * The {@link SingularAttribute} use in the second relation for testing.
     */
    @Mock
    private SingularAttribute<DataPresentation<?>,
        DataPresentation<?>> secondRelationAttribute;

    /**
     * The {@link Join} use in the second relation for testing.
     */
    @Mock
    private Join<DataPresentation<?>, DataPresentation<?>> secondJoin;

    @Override
    public Character getCommonFilterValue() {

        return 'A';
    }

    @Override
    public Boolean getBooleanFilterValue() {

        return true;
    }

    @Override
    public Double getNumberFilterValue() {

        return 1.0d;
    }

    @Override
    public String getTextFilterValue() {

        return "HELLO WORLD!";
    }

    @Override
    public LocalDate getDateFilterValue() {

        return LocalDate.now();
    }

    @BeforeEach
    public void setUp() {

        specificationBuilder = new SpecificationBuilderImpl<>(getHelper());

        @SuppressWarnings("unchecked")
        final Class<D> castClass = (Class<D>) entity.getClass();
        entityClass = castClass;
    }

    private void givenGeneralTest(
        @Nullable final Boolean isDistinct,
        @Nullable final Boolean isInclusive) {

        given(getFilterPresentation().getDistinct()).willReturn(isDistinct);
        given(getFilterPresentation().getInclusive()).willReturn(isInclusive);
    }

    private void givenGeneralTest(
        @Nullable final Filter filter,
        final RelationshipPossibility relationshipPossibility) {

        givenGeneralTest((Boolean) null, null);

        if (relationshipPossibility == RelationshipPossibility.NONE) {

            if (filter == getCommonFilter()) {

                given(getRoot().get(
                    ArgumentMatchers.<SingularAttribute<D, Character>>any()))
                    .willReturn(getCommonPath());
                given(getHelper().getFilterWithAttribute(any(), any()))
                    .willReturn(List
                        .of(Pair.of(getCommonFilter(), getCommonAttribute())));
            }

            if (filter == getBooleanFilter()) {

                given(getRoot()
                    .get(ArgumentMatchers.<SingularAttribute<D, Boolean>>any()))
                    .willReturn(getBooleanPath());
                given(getHelper().getFilterWithAttribute(any(), any()))
                    .willReturn(List.of(
                        Pair.of(getBooleanFilter(), getBooleanAttribute())));
            }

            if (filter == getNumberFilter()) {

                given(getRoot()
                    .get(ArgumentMatchers.<SingularAttribute<D, Double>>any()))
                    .willReturn(getNumberPath());
                given(getHelper().getFilterWithAttribute(any(), any()))
                    .willReturn(List
                        .of(Pair.of(getNumberFilter(), getNumberAttribute())));
            }

            if (filter == getTextFilter()) {

                given(getRoot()
                    .get(ArgumentMatchers.<SingularAttribute<D, String>>any()))
                    .willReturn(getTextPath());
                given(getHelper().getFilterWithAttribute(any(), any()))
                    .willReturn(
                        List.of(Pair.of(getTextFilter(), getTextAttribute())));
            }

            if (filter == getDateFilter()) {

                given(getRoot().get(
                    ArgumentMatchers.<SingularAttribute<D, LocalDate>>any()))
                    .willReturn(getDatePath());
                given(getHelper().getFilterWithAttribute(any(), any()))
                    .willReturn(
                        List.of(Pair.of(getDateFilter(), getDateAttribute())));
            }

            if (filter == getCollectionFilter()) {

                given(getRoot()
                    .get(ArgumentMatchers.<ListAttribute<D, Integer>>any()))
                    .willReturn(getCollectionPath());
                given(getHelper().getFilterWithAttribute(any(), any()))
                    .willReturn(List.of(Pair.of(getCollectionFilter(),
                        getCollectionAttribute())));
            }
        }

        if (relationshipPossibility == RelationshipPossibility.ONE) {

            if (filter != null) {

                given(
                    getRoot().join(
                        ArgumentMatchers.<
                            SingularAttribute<D, DataPresentation<?>>>any(),
                        any()))
                    .willReturn(getJoin());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getFilterPresentation())))
                    .willReturn(List.of(
                        Pair.of(getRelationship(), getRelationAttribute())));
            }

            if (filter == getCommonFilter()) {

                given(getJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, Character>>any()))
                    .willReturn(getCommonPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship())))
                    .willReturn(List
                        .of(Pair.of(getCommonFilter(), getCommonAttribute())));
            }

            if (filter == getBooleanFilter()) {

                given(getJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, Boolean>>any()))
                    .willReturn(getBooleanPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship())))
                    .willReturn(List.of(
                        Pair.of(getBooleanFilter(), getBooleanAttribute())));
            }

            if (filter == getNumberFilter()) {

                given(getJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, Double>>any()))
                    .willReturn(getNumberPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship())))
                    .willReturn(List
                        .of(Pair.of(getNumberFilter(), getNumberAttribute())));
            }

            if (filter == getTextFilter()) {

                given(getJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, String>>any()))
                    .willReturn(getTextPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship()))).willReturn(
                        List.of(Pair.of(getTextFilter(), getTextAttribute())));
            }

            if (filter == getDateFilter()) {

                given(getJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, LocalDate>>any()))
                    .willReturn(getDatePath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship()))).willReturn(
                        List.of(Pair.of(getDateFilter(), getDateAttribute())));
            }

            if (filter == getCollectionFilter()) {

                given(getJoin().get(ArgumentMatchers.<
                    ListAttribute<DataPresentation<?>, Integer>>any()))
                    .willReturn(getCollectionPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship())))
                    .willReturn(List.of(Pair.of(getCollectionFilter(),
                        getCollectionAttribute())));
            }
        }

        if (relationshipPossibility == RelationshipPossibility.TWO) {

            if (filter != null) {

                given(
                    getRoot().join(
                        ArgumentMatchers.<
                            SingularAttribute<D, DataPresentation<?>>>any(),
                        any()))
                    .willReturn(getJoin());
                given(
                    getJoin()
                        .join(
                            ArgumentMatchers.<SingularAttribute<
                                DataPresentation<?>, DataPresentation<?>>>any(),
                            any()))
                    .willReturn(getSecondJoin());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getFilterPresentation())))
                    .willReturn(List.of(
                        Pair.of(getRelationship(), getRelationAttribute())));
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getRelationship())))
                    .willReturn(List.of(Pair.of(getSecondRelationship(),
                        getSecondRelationAttribute())));
            }

            if (filter == getCommonFilter()) {

                given(getSecondJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, Character>>any()))
                    .willReturn(getCommonPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getSecondRelationship())))
                    .willReturn(List
                        .of(Pair.of(getCommonFilter(), getCommonAttribute())));
            }

            if (filter == getBooleanFilter()) {

                given(getSecondJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, Boolean>>any()))
                    .willReturn(getBooleanPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getSecondRelationship())))
                    .willReturn(List.of(
                        Pair.of(getBooleanFilter(), getBooleanAttribute())));
            }

            if (filter == getNumberFilter()) {

                given(getSecondJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, Double>>any()))
                    .willReturn(getNumberPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getSecondRelationship())))
                    .willReturn(List
                        .of(Pair.of(getNumberFilter(), getNumberAttribute())));
            }

            if (filter == getTextFilter()) {

                given(getSecondJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, String>>any()))
                    .willReturn(getTextPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getSecondRelationship()))).willReturn(
                        List.of(Pair.of(getTextFilter(), getTextAttribute())));
            }

            if (filter == getDateFilter()) {

                given(getSecondJoin().get(ArgumentMatchers.<
                    SingularAttribute<DataPresentation<?>, LocalDate>>any()))
                    .willReturn(getDatePath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getSecondRelationship()))).willReturn(
                        List.of(Pair.of(getDateFilter(), getDateAttribute())));
            }

            if (filter == getCollectionFilter()) {

                given(getSecondJoin().get(ArgumentMatchers.<
                    ListAttribute<DataPresentation<?>, Integer>>any()))
                    .willReturn(getCollectionPath());
                given(getHelper().getFilterWithAttribute(any(),
                    eq(getSecondRelationship())))
                    .willReturn(List.of(Pair.of(getCollectionFilter(),
                        getCollectionAttribute())));
            }
        }
    }

    @Test
    @DisplayName("GIVEN a null filter presentation,"
        + " WHEN creating a specification,"
        + " THEN the specification shouldn't contain any conditions")
    final void testCreateSpecificationNullFilter() {

        // WHEN
        final Specification<D> specification
            = getSpecificationBuilder().apply(null, getEntityClass());

        // THEN
        assertThat(specification.toPredicate(getRoot(), getCriteriaQuery(),
            getCriteriaBuilder())).isNull();
    }

    @Test
    @DisplayName("GIVEN a filter with no Parameter,"
        + " WHEN creating a specification,"
        + " THEN the specification shouldn't contain any conditions")
    final void testCreateSpecificationEmptyFilter() {

        // GIVEN
        givenGeneralTest(null, false);

        // WHEN
        final Specification<D> specification = getSpecificationBuilder()
            .apply(getFilterPresentation(), getEntityClass());

        // THEN
        assertThat(specification.toPredicate(getRoot(), getCriteriaQuery(),
            getCriteriaBuilder())).isNull();
    }

    @ParameterizedTest
    @ValueSource(booleans = {
        true, false
    })
    @DisplayName("GIVEN a filter with multiple criteria,"
        + " WHEN creating a specification,"
        + " THEN the specification should have the desired criteria")
    final
        void testCreateSpecificationMultipleFilter(final boolean isInclusive) {

        // GIVEN
        final NumberFilter<Double> filter = getNumberFilter();
        final Predicate predicate = mock(Predicate.class);
        givenGeneralTest(null, isInclusive);
        given(filter.getIn()).willReturn(List.of());
        given(filter.getNotIn()).willReturn(List.of());
        given(filter.getIsNull()).willReturn(null);
        given(filter.getGreaterThan()).willReturn(getNumberFilterValue());
        given(getCriteriaBuilder().gt(ArgumentMatchers.<Path<Double>>any(),
            ArgumentMatchers.<Double>any())).willReturn(predicate);
        given(filter.getLessThan()).willReturn(getNumberFilterValue());
        given(getHelper().getFilterWithAttribute(any(), any())).willReturn(
            List.of(Pair.of(getNumberFilter(), getNumberAttribute())));

        // WHEN
        final Specification<D> specification = getSpecificationBuilder()
            .apply(getFilterPresentation(), getEntityClass());

        // THEN
        final Predicate actualPredicate = specification.toPredicate(getRoot(),
            getCriteriaQuery(), getCriteriaBuilder());
        assertThat(actualPredicate).isNotNull().isEqualTo(predicate);
    }

    @ParameterizedTest
    @ValueSource(booleans = {
        true, false
    })
    @DisplayName("GIVEN a filter with multiple criteria,"
        + " WHEN creating a specification,"
        + " THEN the specification should have the desired criteria")
    final void testCreateSpecificationDistinctFilter(final boolean isDistinct) {

        // GIVEN
        final NumberFilter<Double> filter = getNumberFilter();
        final Predicate predicate = mock(Predicate.class);
        givenGeneralTest(isDistinct, false);
        given(filter.getEqual()).willReturn(getNumberFilterValue());
        given(getCriteriaBuilder().equal(any(), ArgumentMatchers.<Double>any()))
            .willReturn(predicate);
        given(getHelper().getFilterWithAttribute(any(), any())).willReturn(
            List.of(Pair.of(getNumberFilter(), getNumberAttribute())));

        // WHEN
        final Specification<D> specification = getSpecificationBuilder()
            .apply(getFilterPresentation(), getEntityClass());

        // THEN
        final Predicate actualPredicate = specification.toPredicate(getRoot(),
            getCriteriaQuery(), getCriteriaBuilder());
        verify(getCriteriaQuery(), times(1)).distinct(isDistinct);
        assertThat(actualPredicate).isNotNull().isEqualTo(predicate);
    }

    @ParameterizedTest
    @MethodSource("generateTestData")
    @DisplayName("GIVEN a filter,"
        + " WHEN creating a specification,"
        + " THEN the specification should contain the desired criteria")
    final void testCreateSpecification(
        final Class<? extends Filter> filterClass, final String filterName,
        final RelationshipPossibility relationshipPossibility) {

        // PARAM
        final Filter filter = getFilter(filterClass);

        // GIVEN
        givenGeneralTest(filter, relationshipPossibility);
        final Predicate expectedPredicate
            = getExpectedPredicate(filterName, filter);

        // WHEN
        final Specification<D> specification = getSpecificationBuilder()
            .apply(getFilterPresentation(), getEntityClass());

        // THEN
        final Predicate actualPredicate = specification.toPredicate(getRoot(),
            getCriteriaQuery(), getCriteriaBuilder());
        assertThat(actualPredicate).isNotNull().isEqualTo(expectedPredicate);
    }

    private Filter getFilter(final Class<? extends Filter> filterClass) {

        Filter filter = getCommonFilter();

        if (filterClass == BooleanFilter.class) {

            filter = getBooleanFilter();
        }

        if (filterClass == NumberFilter.class) {

            filter = getNumberFilter();
        }

        if (filterClass == TextFilter.class) {

            filter = getTextFilter();
        }

        if (filterClass == DateFilter.class) {

            filter = getDateFilter();
        }

        if (filterClass == CollectionFilter.class) {

            filter = getCollectionFilter();
        }
        return filter;
    }

    private static Stream<Arguments> generateTestData() {

        final List<Class<? extends Filter>> listOfFilterClass = List.of(
            CommonFilter.class, BooleanFilter.class, NumberFilter.class,
            TextFilter.class, DateFilter.class, CollectionFilter.class);

        return listOfFilterClass.stream()
            .flatMap(SpecificationBuilderTest::getArgumentsStream);
    }

    private static Stream<Arguments> getArgumentsStream(
        final Class<? extends Filter> filter) {

        Stream<Arguments> argumentsStream = Stream.empty();

        if (CommonFilter.class.isAssignableFrom(filter)) {

            argumentsStream = addArguments(CommonFilterName.values(), filter);
        }

        if (BooleanFilter.class.isAssignableFrom(filter)) {

            argumentsStream = Stream.concat(argumentsStream,
                addArguments(BooleanFilterName.values(), filter));
        }

        if (NumberFilter.class.isAssignableFrom(filter)) {

            argumentsStream = Stream.concat(argumentsStream,
                addArguments(NumberFilterName.values(), filter));
        }

        if (TextFilter.class.isAssignableFrom(filter)) {

            argumentsStream = Stream.concat(argumentsStream,
                addArguments(TextFilterName.values(), filter));
        }

        if (DateFilter.class.isAssignableFrom(filter)) {

            argumentsStream = Stream.concat(argumentsStream,
                addArguments(DateFilterName.values(), filter));
        }

        if (CollectionFilter.class.isAssignableFrom(filter)) {

            argumentsStream
                = addArguments(CollectionFilterName.values(), filter);
        }
        return argumentsStream;
    }

    private static Stream<Arguments> addArguments(
        final Enum<?>[] enums, final Class<? extends Filter> filter) {

        return Stream.of(enums)
            .flatMap(filterName -> Stream
                .of(RelationshipPossibility.NONE, RelationshipPossibility.ONE,
                    RelationshipPossibility.TWO)
                .flatMap(type -> Stream
                    .of(Arguments.of(filter, filterName.name(), type))));
    }
}
