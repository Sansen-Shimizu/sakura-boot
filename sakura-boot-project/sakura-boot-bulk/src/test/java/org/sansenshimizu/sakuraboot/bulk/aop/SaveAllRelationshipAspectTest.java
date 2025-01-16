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

package org.sansenshimizu.sakuraboot.bulk.aop;

import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.SaveWithRelationship;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.SaveAllService;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * The aspect test class for relationship in service class.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @since  0.1.2
 */
class SaveAllRelationshipAspectTest implements AspectUtilTest {

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
     * The {@link SaveAllRelationshipAspect} to test.
     */
    @Getter
    private final SaveAllRelationshipAspect<DataPresentation<Long>, Long> aspect
        = new SaveAllRelationshipAspect<>(new GlobalSpecification("persistence",
            "business", "business", "business", "presentation"));

    @ParameterizedTest
    @MethodSource("getParams")
    @DisplayName("GIVEN the save all relationship aspect method call,"
        + " WHEN save all with relationship,"
        + " THEN the result should be the expected result")
    final void testSaveAllWithRelationship(
        final DataPresentation<Long> param, final Object expectedResult)
        throws Throwable {

        // GIVEN
        final SaveAllService<DataPresentation<Long>, Long> saveAllTarget
            = mock();
        mockJoinPoint(expectedResult);

        mockForLog(() -> {

            final Object result = aspect.saveAllWithRelationship(joinPoint,
                List.of(param), saveAllTarget, saveAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedResult);
        });
    }

    private static Stream<Arguments> getParams() {

        return Stream.of(Arguments.of(Mockito.<DataPresentation<Long>>mock(),
            Mockito.<DataPresentation<Long>>mock()));
    }
}
