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

import java.util.Set;
import java.util.stream.Stream;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.SaveService;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.SaveWithRelationship;
import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * The aspect test class for relationship in service class.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @since  0.1.0
 */
class SaveRelationshipAspectTest implements AspectUtilTest {

    /**
     * The ID use in test.
     */
    private static final long ID = 1L;

    /**
     * The error message.
     */
    private static final String ERROR_MESSAGE
        = "Can't save an entity when the (second )?relationship "
            + "already has an ID";

    /**
     * The mock {@link ProceedingJoinPoint}.
     */
    @Mock
    @Getter
    private ProceedingJoinPoint joinPoint;

    /**
     * The mock {@link SaveWithRelationship} annotation.
     */
    @Mock
    private SaveWithRelationship saveAnnotation;

    /**
     * The {@link RelationshipAspect} to test.
     */
    @Getter
    private final SaveRelationshipAspect<
        DataPresentation2RelationshipAnyToMany<Long, DataPresentation<Long>,
            DataPresentation<Long>>,
        Long> aspect = new SaveRelationshipAspect<>();

    /**
     * The second {@link RelationshipAspect} to test any to one relationship.
     */
    private final SaveRelationshipAspect<
        DataPresentation2RelationshipAnyToOne<Long, DataPresentation<Long>,
            DataPresentation<Long>>,
        Long> secondAspect = new SaveRelationshipAspect<>();

    @ParameterizedTest
    @MethodSource("getSaveTargetAnyToMany")
    @DisplayName("GIVEN the caching aspect method call,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testSaveWithRelationshipAnyToMany(
        final DataPresentation2RelationshipAnyToMany<Long,
            DataPresentation<Long>, DataPresentation<Long>> param,
        @Nullable final Object expectedResult)
        throws Throwable {

        // GIVEN
        final SaveService<DataPresentation2RelationshipAnyToMany<Long,
            DataPresentation<Long>, DataPresentation<Long>>,
            Long> saveTarget = mock();

        if (expectedResult != null) {

            mockJoinPoint(expectedResult);
        }

        mockForLog(() -> {

            // WHEN
            if (expectedResult == null) {

                assertThatThrownBy(() -> aspect.saveWithRelationship(joinPoint,
                    param, saveTarget, saveAnnotation))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageFindingMatch(ERROR_MESSAGE);
            } else {

                final Object result = aspect.saveWithRelationship(joinPoint,
                    param, saveTarget, saveAnnotation);

                // THEN
                assertThat(result).isEqualTo(expectedResult);
            }
        });
    }

    @ParameterizedTest
    @MethodSource("getSaveTargetAnyToOne")
    @DisplayName("GIVEN the caching aspect method call,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testSaveWithRelationshipAnyToOne(
        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>, DataPresentation<Long>> param,
        @Nullable final Object expectedResult)
        throws Throwable {

        // GIVEN
        final SaveService<DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>, DataPresentation<Long>>,
            Long> saveTarget = mock();

        if (expectedResult != null) {

            mockJoinPoint(expectedResult);
        }

        mockForLog(() -> {

            // WHEN
            if (expectedResult == null) {

                assertThatThrownBy(() -> secondAspect.saveWithRelationship(
                    joinPoint, param, saveTarget, saveAnnotation))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageFindingMatch(ERROR_MESSAGE);
            } else {

                final Object result = secondAspect.saveWithRelationship(
                    joinPoint, param, saveTarget, saveAnnotation);

                // THEN
                assertThat(result).isEqualTo(expectedResult);
            }
        });
    }

    private static Stream<Arguments> getSaveTargetAnyToMany() {

        final DataPresentation<Long> entityWithoutId = mock();
        final DataPresentation<Long> entityWithId = mock();
        given(entityWithId.getId()).willReturn(ID);

        final DataPresentation2RelationshipAnyToMany<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> expectedEntity = mock();
        given(expectedEntity.getId()).willReturn(ID);
        given(expectedEntity.getRelationships())
            .willReturn(Set.of(entityWithId));
        given(expectedEntity.getSecondRelationships())
            .willReturn(Set.of(entityWithId));

        final DataPresentation2RelationshipAnyToMany<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> validEntity = mock();
        given(validEntity.getRelationships())
            .willReturn(Set.of(entityWithoutId));
        given(validEntity.getSecondRelationships())
            .willReturn(Set.of(entityWithoutId));

        final DataPresentation2RelationshipAnyToMany<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> entityWith1RelationshipId = mock();
        given(entityWith1RelationshipId.getRelationships())
            .willReturn(Set.of(entityWithId));
        given(entityWith1RelationshipId.getSecondRelationships())
            .willReturn(Set.of(entityWithoutId));

        final DataPresentation2RelationshipAnyToMany<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> entityWith2RelationshipId = mock();
        given(entityWith2RelationshipId.getRelationships())
            .willReturn(Set.of(entityWithoutId));
        given(entityWith2RelationshipId.getSecondRelationships())
            .willReturn(Set.of(entityWithId));

        return Stream.of(Arguments.of(validEntity, expectedEntity),
            Arguments.of(entityWith1RelationshipId, null),
            Arguments.of(entityWith2RelationshipId, null));
    }

    private static Stream<Arguments> getSaveTargetAnyToOne() {

        final DataPresentation<Long> entityWithoutId = mock();
        final DataPresentation<Long> entityWithId = mock();
        given(entityWithId.getId()).willReturn(ID);

        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> expectedEntity = mock();
        given(expectedEntity.getId()).willReturn(ID);
        given(expectedEntity.getRelationship()).willReturn(entityWithId);
        given(expectedEntity.getSecondRelationship()).willReturn(entityWithId);

        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> validEntity = mock();
        given(validEntity.getRelationship()).willReturn(entityWithoutId);
        given(validEntity.getSecondRelationship()).willReturn(entityWithoutId);

        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> entityWith1RelationshipId = mock();
        given(entityWith1RelationshipId.getRelationship())
            .willReturn(entityWithId);
        given(entityWith1RelationshipId.getSecondRelationship())
            .willReturn(entityWithoutId);

        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> entityWith2RelationshipId = mock();
        given(entityWith2RelationshipId.getRelationship())
            .willReturn(entityWithoutId);
        given(entityWith2RelationshipId.getSecondRelationship())
            .willReturn(entityWithId);

        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> expectedEntityNullRelationship = mock();
        given(expectedEntityNullRelationship.getId()).willReturn(ID);

        final DataPresentation2RelationshipAnyToOne<Long,
            DataPresentation<Long>,
            DataPresentation<Long>> validEntityNullRelationship = mock();

        return Stream.of(Arguments.of(validEntity, expectedEntity),
            Arguments.of(entityWith1RelationshipId, null),
            Arguments.of(entityWith2RelationshipId, null), Arguments.of(
                validEntityNullRelationship, expectedEntityNullRelationship));
    }
}
