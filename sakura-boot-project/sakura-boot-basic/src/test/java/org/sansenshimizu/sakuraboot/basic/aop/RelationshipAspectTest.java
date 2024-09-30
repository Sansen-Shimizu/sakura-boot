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

package org.sansenshimizu.sakuraboot.basic.aop;

import java.util.List;
import java.util.Optional;
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
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.FetchRelationshipRepository;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.FindAllWithRelationship;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.FindByIdWithRelationship;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * The aspect test class for relationship in service class.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @since  0.1.0
 */
class RelationshipAspectTest implements AspectUtilTest {

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
     * The mock {@link FindAllWithRelationship} annotation.
     */
    @Mock
    private FindAllWithRelationship findAllAnnotation;

    /**
     * The mock {@link FindByIdWithRelationship} annotation.
     */
    @Mock
    private FindByIdWithRelationship findByIdAnnotation;

    /**
     * The {@link RelationshipAspect} to test.
     */
    @Getter
    private final RelationshipAspect<
        DataPresentation1RelationshipAnyToMany<Long, DataPresentation<Long>>,
        Long> aspect = new RelationshipAspect<>();

    @ParameterizedTest
    @MethodSource("getFindAllTarget")
    @DisplayName("GIVEN the caching aspect method call,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testFindAllWithRelationship(
        final FindAllService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findAllTarget,
        final Boolean fetchRelationship, final Object expectedResult,
        final boolean testLog)
        throws Throwable {

        // GIVEN
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
            FindAllService.class.getMethod("findAll", Pageable.class));

        mockForLog(() -> {

            // WHEN
            final Object result = aspect.findAllWithRelationship(joinPoint,
                pageable, findAllTarget, findAllAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedResult);
        }, true);
    }

    @ParameterizedTest
    @MethodSource("getFindByIdTarget")
    @DisplayName("GIVEN the caching aspect method call,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testFindByIdWithRelationship(
        final FindByIdService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findByIdTarget,
        final Boolean fetchRelationship, @Nullable final Object expectedResult,
        final boolean testLog)
        throws Throwable {

        // GIVEN
        if (expectedResult != null && !fetchRelationship) {

            mockJoinPoint(expectedResult);
        }

        final MethodSignature signature = mock();
        given(joinPoint.getSignature()).willReturn(signature);

        if (testLog) {

            given(joinPoint.getArgs()).willReturn(getArgs());
        }
        given(signature.getMethod()).willReturn(
            FindByIdService.class.getMethod("findById", Comparable.class));

        mockForLog(() -> {

            // WHEN
            if (expectedResult == null) {

                assertThatThrownBy(() -> aspect.findByIdWithRelationship(
                    joinPoint, ID, findByIdTarget, findByIdAnnotation))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("not found with ID : " + ID);
            } else {

                final Object result = aspect.findByIdWithRelationship(joinPoint,
                    ID, findByIdTarget, findByIdAnnotation);

                // THEN
                assertThat(result).isEqualTo(expectedResult);
            }
        }, true);
    }

    private static Stream<Arguments> getFindAllTarget() {

        final DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>> entity = mock();
        given(entity.getId()).willReturn(ID);

        // Service with basic repository
        final BasicRepository<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> repository = mock();
        final FindAllService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findAllTarget = mock();
        given(findAllTarget.getRepository()).willReturn(repository);

        // Service with an empty result
        final BasicRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> repositoryRelationshipWithEmptyResult = mock(withSettings()
                .extraInterfaces(FetchRelationshipRepository.class));
        given(repositoryRelationshipWithEmptyResult
            .findAllIds(any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of()));

        @SuppressWarnings("unchecked")
        final FetchRelationshipRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> castRepository = (FetchRelationshipRepository<
                DataPresentation1RelationshipAnyToMany<Long,
                    DataPresentation<Long>>,
                Long>) repositoryRelationshipWithEmptyResult;
        given(castRepository.findAllEagerRelationship(any(), any()))
            .willReturn(List.of());
        final FindAllService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> findAllRelationshipNoResultTarget = mock();
        given(findAllRelationshipNoResultTarget.getRepository())
            .willReturn(repositoryRelationshipWithEmptyResult);

        // Service with a result
        final BasicRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> repositoryRelationshipWithResult = mock(withSettings()
                .extraInterfaces(FetchRelationshipRepository.class));
        given(repositoryRelationshipWithResult.findAllIds(any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(ID)));
        @SuppressWarnings("unchecked")
        final FetchRelationshipRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> castRepositoryWithResult = (FetchRelationshipRepository<
                DataPresentation1RelationshipAnyToMany<Long,
                    DataPresentation<Long>>,
                Long>) repositoryRelationshipWithResult;
        given((castRepositoryWithResult).findAllEagerRelationship(any(), any()))
            .willReturn(List.of(entity));
        final FindAllService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> findAllRelationshipTarget = mock();
        given(findAllRelationshipTarget.getRepository())
            .willReturn(repositoryRelationshipWithResult);

        // Service with loggable
        final FindAllService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findAllWithLoggableTarget
                = mock(withSettings().extraInterfaces(Loggable.class));
        given(findAllWithLoggableTarget.getRepository())
            .willReturn(repositoryRelationshipWithResult);

        return Stream.of(
            Arguments.of(findAllTarget, false, new PageImpl<>(List.of(entity)),
                false),
            Arguments.of(findAllRelationshipNoResultTarget, true,
                new PageImpl<>(List.of()), false),
            Arguments.of(findAllRelationshipTarget, true,
                new PageImpl<>(List.of(entity)), false),
            Arguments.of(findAllWithLoggableTarget, true,
                new PageImpl<>(List.of(entity)), true));
    }

    private static Stream<Arguments> getFindByIdTarget() {

        final DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>> entity = mock();
        given(entity.getId()).willReturn(ID);

        // Service with basic repository
        final BasicRepository<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> repository = mock();
        final FindByIdService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findByIdTarget = mock();
        given(findByIdTarget.getRepository()).willReturn(repository);

        // Service with no result
        final BasicRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> repositoryRelationshipWithEmptyResult = mock(withSettings()
                .extraInterfaces(FetchRelationshipRepository.class));

        @SuppressWarnings("unchecked")
        final FetchRelationshipRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> castRepository = (FetchRelationshipRepository<
                DataPresentation1RelationshipAnyToMany<Long,
                    DataPresentation<Long>>,
                Long>) repositoryRelationshipWithEmptyResult;
        given(castRepository.findByIdEagerRelationship(anyLong(), any()))
            .willReturn(Optional.empty());
        final FindByIdService<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> findRelationshipNoResultTarget = mock();
        given(findRelationshipNoResultTarget.getRepository())
            .willReturn(repositoryRelationshipWithEmptyResult);
        @SuppressWarnings("unchecked")
        final Class<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>> castClass
                = (Class<DataPresentation1RelationshipAnyToMany<Long,
                    DataPresentation<Long>>>) entity.getClass();
        given(findRelationshipNoResultTarget.getEntityClass())
            .willReturn(castClass);

        // Service with a result
        final BasicRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> repositoryRelationshipWithResult = mock(withSettings()
                .extraInterfaces(FetchRelationshipRepository.class));

        @SuppressWarnings("unchecked")
        final FetchRelationshipRepository<
            DataPresentation1RelationshipAnyToMany<Long,
                DataPresentation<Long>>,
            Long> castRepositoryWithResult = (FetchRelationshipRepository<
                DataPresentation1RelationshipAnyToMany<Long,
                    DataPresentation<Long>>,
                Long>) repositoryRelationshipWithResult;
        given(castRepositoryWithResult.findByIdEagerRelationship(any(), any()))
            .willReturn(Optional.of(entity));
        final FindByIdService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findRelationshipTarget = mock();
        given(findRelationshipTarget.getRepository())
            .willReturn(repositoryRelationshipWithResult);

        // Service with loggable
        final FindByIdService<DataPresentation1RelationshipAnyToMany<Long,
            DataPresentation<Long>>, Long> findAllWithLoggableTarget
                = mock(withSettings().extraInterfaces(Loggable.class));
        given(findAllWithLoggableTarget.getRepository())
            .willReturn(repositoryRelationshipWithResult);

        return Stream.of(Arguments.of(findByIdTarget, false, entity, false),
            Arguments.of(findRelationshipNoResultTarget, true, null, false),
            Arguments.of(findRelationshipTarget, true, entity, false),
            Arguments.of(findAllWithLoggableTarget, true, entity, true));
    }
}
