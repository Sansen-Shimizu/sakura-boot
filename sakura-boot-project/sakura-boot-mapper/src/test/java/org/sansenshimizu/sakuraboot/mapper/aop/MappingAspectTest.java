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

package org.sansenshimizu.sakuraboot.mapper.aop;

import java.util.List;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;
import org.sansenshimizu.sakuraboot.mapper.api.annotations.Mapping;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * The aspect test class for mapping method inside Mappable class using
 * BasicMapper.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @see    MappingAspect
 * @since  0.1.0
 */
class MappingAspectTest implements AspectUtilTest {

    /**
     * An entity use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static DataPresentation<Long> entity;

    /**
     * A DTO use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static DataPresentation<Long> dto;

    /**
     * The class of the entity uses in test.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Class<DataPresentation<Long>> entityClass;

    /**
     * The class of the dto uses in test.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Class<DataPresentation<Long>> dtoClass;

    /**
     * A page of entity use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Page<DataPresentation<Long>> pageEntity;

    /**
     * A page of DTO use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Page<DataPresentation<Long>> pageDto;

    /**
     * The arguments pass to function to test mapping.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Object[] mapArgs;

    /**
     * The arguments pass to function to test mapping with page.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Object[] mapArgsWithPage;

    /**
     * The mock {@link ProceedingJoinPoint}.
     */
    @Mock
    @Getter
    private ProceedingJoinPoint joinPoint;

    /**
     * The mock {@link Mappable} target.
     */
    @Mock
    private Mappable<DataPresentation<Long>, DataPresentation<Long>> target;

    /**
     * The mock {@link Mapping} annotation.
     */
    @Mock
    private Mapping mappingAnnotation;

    /**
     * The mock {@link BasicMapper}.
     */
    @Mock
    private BasicMapper<DataPresentation<Long>, DataPresentation<Long>> mapper;

    /**
     * The {@link MappingAspect} to test.
     */
    @Getter
    private final MappingAspect<DataPresentation<Long>,
        DataPresentation<Long>> aspect = new MappingAspect<>();

    @BeforeAll
    static void setUp() {

        entity = mock();
        dto = mock();

        @SuppressWarnings("unchecked")
        final Class<DataPresentation<Long>> entityClassCast
            = (Class<DataPresentation<Long>>) entity.getClass();
        entityClass = entityClassCast;
        @SuppressWarnings("unchecked")
        final Class<DataPresentation<Long>> dtoClassCast
            = (Class<DataPresentation<Long>>) dto.getClass();
        dtoClass = dtoClassCast;

        pageEntity = new PageImpl<>(List.of(entity));
        pageDto = new PageImpl<>(List.of(dto));
        mapArgs = new Object[] {
            dto
        };
        mapArgsWithPage = new Object[] {
            pageDto
        };
    }

    @Test
    @DisplayName("GIVEN the mapping aspect method call,"
        + " WHEN mapping,"
        + " THEN the result should be the expected result")
    final void testMapping() throws Throwable {

        // GIVEN
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(mappingAnnotation.mapFirstArgument()).willReturn(false);
        mockJoinPointWithArgs(EXPECTED_VALUE);
        given(mappingAnnotation.mapResult()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result
                = aspect.mapping(joinPoint, target, mappingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the mapping aspect method call with annotation value,"
        + " WHEN mapping,"
        + " THEN the result should be the expected result")
    final void testMappingWithAnnotationValue() throws Throwable {

        // GIVEN
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(mappingAnnotation.mapFirstArgument()).willReturn(true);
        given(target.getDtoClass()).willReturn(dtoClass);
        mockJoinPointWithArgs(EXPECTED_VALUE);
        given(mappingAnnotation.mapResult()).willReturn(true);
        given(target.getEntityClassToMap()).willReturn(entityClass);
        mockForLog(() -> {

            // WHEN
            final Object result
                = aspect.mapping(joinPoint, target, mappingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the mapping aspect method call,"
        + " WHEN mapping the first arg and the return value,"
        + " THEN the result should be the expected result")
    final void testMappingFirstArgAndReturn() throws Throwable {

        // GIVEN
        given(joinPoint.getArgs()).willReturn(mapArgs);
        given(mappingAnnotation.mapFirstArgument()).willReturn(true);
        given(target.getDtoClass()).willReturn(dtoClass);
        given(target.getMapper()).willReturn(mapper);
        given(mapper.toEntity(any())).willReturn(entity);
        mockJoinPointWithArgs(entity);
        given(mappingAnnotation.mapResult()).willReturn(true);
        given(target.getEntityClassToMap()).willReturn(entityClass);
        given(mapper.toDto(any())).willReturn(dto);
        mockForLog(() -> {

            // WHEN
            final Object result
                = aspect.mapping(joinPoint, target, mappingAnnotation);

            // THEN
            assertThat(result).isEqualTo(dto);
        });
    }

    @Test
    @DisplayName("GIVEN the mapping aspect method call,"
        + " WHEN mapping the first arg and the return value with page with a "
        + "different type,"
        + " THEN the result should be the expected result")
    final void testMappingFirstArgAndReturnForPageDifferentType()
        throws Throwable {

        // GIVEN
        final Page<Integer> page = new PageImpl<>(List.of(EXPECTED_VALUE));
        final Object[] argsWithPage = {
            page
        };
        given(joinPoint.getArgs()).willReturn(argsWithPage);
        given(mappingAnnotation.mapFirstArgument()).willReturn(true);
        given(target.getDtoClass()).willReturn(dtoClass);
        mockJoinPointWithArgs(page);
        given(mappingAnnotation.mapResult()).willReturn(true);
        given(target.getEntityClassToMap()).willReturn(entityClass);
        mockForLog(() -> {

            // WHEN
            final Object result
                = aspect.mapping(joinPoint, target, mappingAnnotation);

            // THEN
            assertThat(result).isEqualTo(page);
        });
    }

    @Test
    @DisplayName("GIVEN the mapping aspect method call,"
        + " WHEN mapping the first arg and the return value with page,"
        + " THEN the result should be the expected result")
    final void testMappingFirstArgAndReturnForPage() throws Throwable {

        // GIVEN
        given(joinPoint.getArgs()).willReturn(mapArgsWithPage);
        given(mappingAnnotation.mapFirstArgument()).willReturn(true);
        given(target.getDtoClass()).willReturn(dtoClass);
        given(target.getMapper()).willReturn(mapper);
        given(mapper.toEntity(any())).willReturn(entity);
        mockJoinPointWithArgs(pageEntity);
        given(mappingAnnotation.mapResult()).willReturn(true);
        given(target.getEntityClassToMap()).willReturn(entityClass);
        given(mapper.toDto(any())).willReturn(dto);
        mockForLog(() -> {

            // WHEN
            final Object result
                = aspect.mapping(joinPoint, target, mappingAnnotation);

            // THEN
            assertThat(result).isEqualTo(pageDto);
        });
    }
}
