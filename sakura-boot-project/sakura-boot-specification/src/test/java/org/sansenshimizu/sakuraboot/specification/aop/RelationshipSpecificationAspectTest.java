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

package org.sansenshimizu.sakuraboot.specification.aop;

import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.specification.api.business.services.FindAllByCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.persistence.CriteriaRepository;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;
import org.sansenshimizu.sakuraboot.specification.api.relationship.FetchRelationshipSpecificationRepository;
import org.sansenshimizu.sakuraboot.specification.api.relationship.annotations.FindAllByCriteriaWithRelationship;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * The aspect test class for relationship in service class with filter.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @since  0.1.0
 */
class RelationshipSpecificationAspectTest implements AspectUtilTest {

    /**
     * The ID use in test.
     */
    private static final long ID = 1L;

    /**
     * The mock {@link ProceedingJoinPoint}.
     */
    @Mock
    @Getter
    private ProceedingJoinPoint joinPoint;

    /**
     * The mock {@link FindAllByCriteriaWithRelationship} annotation.
     */
    @Mock
    private FindAllByCriteriaWithRelationship findAllByCriteriaAnnotation;

    /**
     * The {@link RelationshipSpecificationAspect} to test.
     */
    @Getter
    private final RelationshipSpecificationAspect<
        DataPresentation1RelationshipAnyToMany<Long, DataPresentation<Long>>,
        Long, FilterPresentation<NumberFilter<Long>>> aspect
            = new RelationshipSpecificationAspect<>();

    @ParameterizedTest
    @MethodSource("getFindAllByCriteriaTarget")
    @DisplayName("GIVEN the caching aspect method call,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testFindAllByCriteriaWithRelationship(
        final FindAllByCriteriaService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long,
            FilterPresentation<NumberFilter<Long>>> findAllByCriteriaTarget,
        final Boolean fetchRelationship, final Object expectedResult,
        final boolean testLog)
        throws Throwable {

        // GIVEN
        final FilterPresentation<NumberFilter<Long>> filter = mock();
        final Pageable pageable = Pageable.unpaged();

        if (!fetchRelationship) {

            mockJoinPoint(expectedResult);
        }

        final MethodSignature signature = mock();
        given(joinPoint.getSignature()).willReturn(signature);

        if (testLog) {

            given(joinPoint.getArgs()).willReturn(getArgs());
        }
        given(signature.getMethod()).willReturn(
            FindAllByCriteriaService.class.getMethod("findAllByCriteria",
                FilterPresentation.class, Pageable.class));

        mockForLog(() -> {

            // WHEN
            final Object result = aspect.findAllByCriteriaWithRelationship(
                joinPoint, filter, pageable, findAllByCriteriaTarget,
                findAllByCriteriaAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedResult);
        }, true);
    }

    private static Stream<Arguments> getFindAllByCriteriaTarget() {

        final DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>> entity = mock();
        given(entity.getId()).willReturn(ID);

        // Service with criteria repository
        final CriteriaRepository<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> repository = mock();
        final FindAllByCriteriaService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long,
            FilterPresentation<NumberFilter<Long>>> findAllByCriteriaTarget
                = mock();
        given(findAllByCriteriaTarget.getRepository()).willReturn(repository);

        // Service with an empty result
        final CriteriaRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> repositoryRelationshipWithEmptyResult
                = mock(withSettings().extraInterfaces(
                    FetchRelationshipSpecificationRepository.class));

        @SuppressWarnings("unchecked")
        final FetchRelationshipSpecificationRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> castRepository = (FetchRelationshipSpecificationRepository<
                DataPresentation1RelationshipAnyToMany<Long,
                    DataPresentation<Long>>,
                Long>) repositoryRelationshipWithEmptyResult;
        given(castRepository.findAllIds(any(), any(), any()))
            .willReturn(new PageImpl<>(List.of()));
        given(castRepository.findAllEagerRelationship(any(), any()))
            .willReturn(List.of());
        final FindAllByCriteriaService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long, FilterPresentation<
                NumberFilter<Long>>> findAllRelationshipNoResultTarget = mock();
        given(findAllRelationshipNoResultTarget.getRepository())
            .willReturn(repositoryRelationshipWithEmptyResult);

        // Service with a result
        final FluentQuery.FetchableFluentQuery<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>> fetchableFluentQuery = mock();
        given(fetchableFluentQuery.project(anyString()))
            .willReturn(fetchableFluentQuery);
        given(fetchableFluentQuery.page(any()))
            .willReturn(new PageImpl<>(List.of(entity)));
        final CriteriaRepository<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> repositoryRelationshipWithResult
                = mock(withSettings().extraInterfaces(
                    FetchRelationshipSpecificationRepository.class));

        @SuppressWarnings("unchecked")
        final FetchRelationshipSpecificationRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> castRepositoryWithResult
                = (FetchRelationshipSpecificationRepository<
                    DataPresentation1RelationshipAnyToMany<Long,
                        DataPresentation<Long>>,
                    Long>) repositoryRelationshipWithResult;
        given(castRepositoryWithResult.findAllIds(any(), any(), any()))
            .willReturn(new PageImpl<>(List.of(ID)));
        given(castRepositoryWithResult.findAllEagerRelationship(any(), any()))
            .willReturn(List.of(entity));
        final FindAllByCriteriaService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long,
            FilterPresentation<NumberFilter<Long>>> findAllRelationshipTarget
                = mock();
        given(findAllRelationshipTarget.getRepository())
            .willReturn(repositoryRelationshipWithResult);

        // Service with loggable
        final FindAllByCriteriaService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long,
            FilterPresentation<NumberFilter<Long>>> findAllWithLoggableTarget
                = mock(withSettings().extraInterfaces(Loggable.class));
        given(findAllWithLoggableTarget.getRepository())
            .willReturn(repositoryRelationshipWithResult);

        return Stream.of(
            Arguments.of(findAllByCriteriaTarget, false,
                new PageImpl<>(List.of(entity)), false),
            Arguments.of(findAllRelationshipNoResultTarget, true,
                new PageImpl<>(List.of()), false),
            Arguments.of(findAllRelationshipTarget, true,
                new PageImpl<>(List.of(entity)), false),
            Arguments.of(findAllWithLoggableTarget, true,
                new PageImpl<>(List.of(entity)), true));
    }
}
