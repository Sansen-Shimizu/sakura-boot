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

package org.sansenshimizu.sakuraboot.hypermedia.aop;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModel;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModelAssembler;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermedia;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermediaOnPage;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * The aspect test class for applying hypermedia method inside Hypermedia class
 * using BasicModelAssembler.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @see    HypermediaAspect
 * @since  0.1.0
 */
class HypermediaAspectTest implements AspectUtilTest {

    /**
     * The class of the DTO uses in testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static Class<DataPresentation<Long>> dtoClass;

    /**
     * A response entity use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ResponseEntity<?> response;

    /**
     * A response page of entity use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ResponseEntity<?> pageResponse;

    /**
     * A model use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static AbstractBasicModel<DataPresentation<Long>> model;

    /**
     * A response entity use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ResponseEntity<?> expectedResponse;

    /**
     * A page model use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static PagedModel<
        AbstractBasicModel<DataPresentation<Long>>> pageModel;

    /**
     * A response page model use for testing.
     */
    @SuppressWarnings("NotNullFieldNotInitialized")
    private static ResponseEntity<?> expectedPageResponse;

    /**
     * The mock {@link ProceedingJoinPoint}.
     */
    @Mock
    @Getter
    private ProceedingJoinPoint joinPoint;

    /**
     * The mock {@link Hypermedia} target.
     */
    @Mock
    private Hypermedia<DataPresentation<Long>,
        AbstractBasicModelAssembler<DataPresentation<Long>,
            AbstractBasicModel<DataPresentation<Long>>>> target;

    /**
     * The mock {@link ApplyHypermedia} annotation.
     */
    @Mock
    private ApplyHypermedia applyHypermediaAnnotation;

    /**
     * The mock {@link ApplyHypermediaOnPage} annotation.
     */
    @Mock
    private ApplyHypermediaOnPage applyHypermediaOnPageAnnotation;

    /**
     * The mock {@link AbstractBasicModelAssembler}.
     */
    @Mock
    private AbstractBasicModelAssembler<DataPresentation<Long>,
        AbstractBasicModel<DataPresentation<Long>>> modelAssembler;

    /**
     * The mock {@link PagedResourcesAssembler}.
     */
    @Mock
    private PagedResourcesAssembler<
        DataPresentation<Long>> pagedResourcesAssembler;

    /**
     * The mock map containing the mock {@link PagedResourcesAssembler}.
     */
    @Mock
    private Map<String, PagedResourcesAssembler<
        DataPresentation<Long>>> pagedResourcesAssemblers;

    /**
     * The {@link HypermediaAspect} to test.
     */
    @Getter
    private final HypermediaAspect<DataPresentation<Long>> aspect
        = new HypermediaAspect<>();

    @BeforeAll
    static void setUp() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder
            .setRequestAttributes(new ServletRequestAttributes(request));

        final DataPresentation<Long> dto = mock();
        @SuppressWarnings("unchecked")
        final Class<DataPresentation<Long>> castClass
            = (Class<DataPresentation<Long>>) dto.getClass();
        dtoClass = castClass;
        response = new ResponseEntity<>(dto, HttpStatus.OK);
        final Page<DataPresentation<Long>> pageDto
            = new PageImpl<>(List.of(dto));
        pageResponse = new ResponseEntity<>(pageDto, HttpStatus.OK);

        model = mock();
        expectedResponse = new ResponseEntity<>(model, HttpStatus.OK);
        pageModel = PagedModel.of(List.of(model),
            new PagedModel.PageMetadata(1, 1, 1));
        expectedPageResponse = new ResponseEntity<>(pageModel, HttpStatus.OK);
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia,"
        + " THEN the result should be the expected result")
    final void testApplyHypermedia() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermedia(joinPoint, target,
                applyHypermediaAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia on response,"
        + " THEN the result should be the expected result")
    final void testApplyHypermediaOnResponse() throws Throwable {

        // GIVEN
        final ResponseEntity<?> responseEntity
            = new ResponseEntity<>(EXPECTED_VALUE, HttpStatus.OK);
        mockJoinPoint(responseEntity);
        given(target.getDataClass()).willReturn(dtoClass);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermedia(joinPoint, target,
                applyHypermediaAnnotation);

            // THEN
            assertThat(result).isEqualTo(responseEntity);

            if (result instanceof final ResponseEntity<?> castResult) {

                assertThat(responseEntity.getStatusCode())
                    .isEqualTo(castResult.getStatusCode());
                assertThat(responseEntity.getBody())
                    .isEqualTo(castResult.getBody());
            }
        });
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia on response entity,"
        + " THEN the result should be the expected result")
    final void testApplyHypermediaOnResponseEntity() throws Throwable {

        // GIVEN
        mockJoinPoint(response);
        given(target.getDataClass()).willReturn(dtoClass);
        given(target.getModelAssembler()).willReturn(modelAssembler);
        given(modelAssembler.toModel(any())).willReturn(model);
        given(target.getDataClass()).willReturn(dtoClass);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermedia(joinPoint, target,
                applyHypermediaAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedResponse);

            if (result instanceof final ResponseEntity<?> castResult) {

                assertThat(castResult.getStatusCode())
                    .isEqualTo(expectedResponse.getStatusCode());
                assertThat(castResult.getBody())
                    .isEqualTo(expectedResponse.getBody());
            }
        });
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia on page,"
        + " THEN the result should be the expected result")
    final void testApplyHypermediaOnPage() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermediaOnPage(joinPoint,
                target, applyHypermediaOnPageAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia on response with no page,"
        + " THEN the result should be the expected result")
    final void testApplyHypermediaOnResponseNoPage() throws Throwable {

        // GIVEN
        final ResponseEntity<?> responseEntity
            = new ResponseEntity<>(EXPECTED_VALUE, HttpStatus.OK);
        mockJoinPoint(responseEntity);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermediaOnPage(joinPoint,
                target, applyHypermediaOnPageAnnotation);

            // THEN
            assertThat(result).isEqualTo(responseEntity);

            if (result instanceof final ResponseEntity<?> castResult) {

                assertThat(castResult.getStatusCode())
                    .isEqualTo(responseEntity.getStatusCode());
                assertThat(castResult.getBody())
                    .isEqualTo(responseEntity.getBody());
            }
        });
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia on response on page,"
        + " THEN the result should be the expected result")
    final void testApplyHypermediaOnResponseOnPage() throws Throwable {

        // GIVEN
        final Page<Integer> page = new PageImpl<>(List.of(EXPECTED_VALUE));
        final ResponseEntity<?> responseEntity
            = new ResponseEntity<>(page, HttpStatus.OK);
        mockJoinPoint(responseEntity);
        given(target.getDataClass()).willReturn(dtoClass);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermediaOnPage(joinPoint,
                target, applyHypermediaOnPageAnnotation);

            // THEN
            assertThat(result).isEqualTo(responseEntity);

            if (result instanceof final ResponseEntity<?> castResult) {

                assertThat(castResult.getStatusCode())
                    .isEqualTo(responseEntity.getStatusCode());
                assertThat(castResult.getBody())
                    .isEqualTo(responseEntity.getBody());
            }
        });
    }

    @Test
    @DisplayName("GIVEN the hypermedia aspect method call,"
        + " WHEN applying hypermedia on response entity on page,"
        + " THEN the result should be the expected result")
    final void testApplyHypermediaOnResponseEntityOnPage() throws Throwable {

        // GIVEN
        given(target.getModelAssembler()).willReturn(modelAssembler);
        given(target.getPagedResourcesAssembler())
            .willReturn(pagedResourcesAssemblers);
        given(pagedResourcesAssemblers.get(ArgumentMatchers.<String>any()))
            .willReturn(pagedResourcesAssembler);
        given(applyHypermediaOnPageAnnotation.pagedResourcesAssembler())
            .willReturn(Hypermedia.DEFAULT_PAGED_NAME);
        given(applyHypermediaOnPageAnnotation.value())
            .willReturn(Hypermedia.DEFAULT_PAGED_NAME);
        BDDMockito
            .<Class<? extends Annotation>>given(
                applyHypermediaOnPageAnnotation.annotationType())
            .willReturn(ApplyHypermediaOnPage.class);
        given(pagedResourcesAssembler.toModel(any(),
            ArgumentMatchers.<
                RepresentationModelAssembler<DataPresentation<Long>,
                    AbstractBasicModel<DataPresentation<Long>>>>any()))
            .willReturn(pageModel);
        mockJoinPoint(pageResponse);
        given(target.getDataClass()).willReturn(dtoClass);
        mockForLog(() -> {

            // WHEN
            final Object result = aspect.applyHypermediaOnPage(joinPoint,
                target, applyHypermediaOnPageAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedPageResponse);

            if (result instanceof final ResponseEntity<?> castResult) {

                assertThat(castResult.getStatusCode())
                    .isEqualTo(expectedPageResponse.getStatusCode());
                assertThat(castResult.getBody())
                    .isEqualTo(expectedPageResponse.getBody());
            }
        });
    }
}
