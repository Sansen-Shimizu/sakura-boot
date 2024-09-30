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

package org.sansenshimizu.sakuraboot.cache.aop;

import java.io.Serial;
import java.lang.annotation.Annotation;
import java.util.Objects;

import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.cache.api.Cacheable;
import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;
import org.sansenshimizu.sakuraboot.cache.api.annotations.Caching;
import org.sansenshimizu.sakuraboot.cache.api.annotations.PutCache;
import org.sansenshimizu.sakuraboot.cache.api.annotations.RemoveCache;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The aspect test class for caching method inside Cacheable class using
 * CachingUtil.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @see    CachingAspect
 * @since  0.1.0
 */
class CachingAspectTest implements AspectUtilTest {

    /**
     * The cache names use in test.
     */
    private static final String[] CACHE_NAMES = {
        "test"
    };

    /**
     * The string use for testing cache name concatenation.
     */
    private static final String CONCAT_STRING = "Concat";

    /**
     * The mock {@link ProceedingJoinPoint}.
     */
    @Mock
    @Getter
    private ProceedingJoinPoint joinPoint;

    /**
     * The mock {@link MethodSignature}.
     */
    @Mock
    private MethodSignature signature;

    /**
     * The mock {@link Cacheable} target.
     */
    @Mock
    private Cacheable target;

    /**
     * The mock {@link Caching} annotation.
     */
    @Mock
    private Caching cachingAnnotation;

    /**
     * The mock {@link PutCache} annotation.
     */
    @Mock
    private PutCache putCacheAnnotation;

    /**
     * The mock {@link RemoveCache} annotation.
     */
    @Mock
    private RemoveCache removeCacheAnnotation;

    /**
     * The mock {@link CachingUtil}.
     */
    @Mock
    private CachingUtil cachingUtil;

    /**
     * The {@link CachingAspect} to test.
     */
    @Getter
    private final CachingAspect aspect = new CachingAspect();

    @Test
    @DisplayName("GIVEN the caching aspect method call,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testCaching() throws Throwable {

        // GIVEN
        given(cachingAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(cachingAnnotation.concatToCacheName()).willReturn("");
        given(cachingAnnotation.key()).willReturn("");
        given(cachingAnnotation.value()).willReturn("");
        BDDMockito.<Class<? extends Annotation>>given(
            cachingAnnotation.annotationType()).willReturn(Caching.class);
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(target.getCachingUtil()).willReturn(cachingUtil);
        mockJoinPoint(EXPECTED_VALUE);
        given(cachingUtil.caching(any(), any(), any())).willAnswer(
            invocation -> invocation.getArgument(1, CachingUtil.Supplier.class)
                .get());
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().caching(joinPoint, target, cachingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the caching aspect method call with null parameter,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testCachingWithNullParameter() throws Throwable {

        // GIVEN
        given(cachingAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(cachingAnnotation.concatToCacheName()).willReturn("");
        given(cachingAnnotation.key()).willReturn("");
        given(cachingAnnotation.value()).willReturn("");
        BDDMockito.<Class<? extends Annotation>>given(
            cachingAnnotation.annotationType()).willReturn(Caching.class);
        given(joinPoint.getArgs()).willReturn(new Object[] {
            null
        });
        given(target.getCachingUtil()).willReturn(cachingUtil);
        mockJoinPoint(EXPECTED_VALUE);
        given(cachingUtil.caching(any(), any(), any())).willAnswer(
            invocation -> invocation.getArgument(1, CachingUtil.Supplier.class)
                .get());
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().caching(joinPoint, target, cachingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the caching aspect method call with annotation value,"
        + " WHEN caching,"
        + " THEN the result should be the expected result")
    final void testCachingWithAnnotationValue() throws Throwable {

        // GIVEN
        given(cachingAnnotation.specificsCacheNames()).willReturn(CACHE_NAMES);
        given(cachingAnnotation.concatToCacheName()).willReturn(CONCAT_STRING);
        given(cachingAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito.<Class<? extends Annotation>>given(
            cachingAnnotation.annotationType()).willReturn(Caching.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(getParameterName());
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(target.getCachingUtil()).willReturn(cachingUtil);
        mockJoinPoint(EXPECTED_VALUE);
        given(cachingUtil.caching(any(), any(), any())).willAnswer(
            invocation -> invocation.getArgument(1, CachingUtil.Supplier.class)
                .get());
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().caching(joinPoint, target, cachingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        }, true);
    }

    @Test
    @DisplayName("GIVEN the caching aspect method call with annotation value,"
        + " WHEN caching with a null SpEL,"
        + " THEN the result should be the expected result")
    final void testCachingWithAnnotationValueAndNullSpelExpression()
        throws Throwable {

        // GIVEN
        given(cachingAnnotation.specificsCacheNames()).willReturn(CACHE_NAMES);
        given(cachingAnnotation.concatToCacheName()).willReturn(CONCAT_STRING);
        given(cachingAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito.<Class<? extends Annotation>>given(
            cachingAnnotation.annotationType()).willReturn(Caching.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(new String[] {});
        given(joinPoint.getArgs()).willReturn(new Object[] {});
        given(target.getCachingUtil()).willReturn(cachingUtil);
        mockJoinPoint(EXPECTED_VALUE);
        given(cachingUtil.caching(any(), any(), any())).willAnswer(
            invocation -> invocation.getArgument(1, CachingUtil.Supplier.class)
                .get());
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().caching(joinPoint, target, cachingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        }, true);
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call,"
        + " WHEN putCache,"
        + " THEN the result should be the expected result")
    final void testPutCache() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(putCacheAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(putCacheAnnotation.key()).willReturn("");
        given(putCacheAnnotation.value()).willReturn("");
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(EXPECTED_VALUE);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call with null parameter,"
        + " WHEN putCache,"
        + " THEN the result should be the expected result")
    final void testPutCacheWithNullParameter() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(putCacheAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(putCacheAnnotation.key()).willReturn("");
        given(putCacheAnnotation.value()).willReturn("");
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(joinPoint.getArgs()).willReturn(new Object[] {
            null
        });
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(EXPECTED_VALUE);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
        });
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call with annotation value,"
        + " WHEN putCache,"
        + " THEN the result should be the expected result")
    final void testPutCacheWithAnnotationValue() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(putCacheAnnotation.specificsCacheNames()).willReturn(CACHE_NAMES);
        given(putCacheAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(getParameterName());
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(EXPECTED_VALUE);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(true);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
            verify(cachingUtil, times(1)).removeAllCache(any());
        }, true);
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call with annotation value,"
        + " WHEN putCache with a null SpEL,"
        + " THEN the result should be the expected result")
    final void testPutCacheWithAnnotationValueAndNullSpelExpression()
        throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(putCacheAnnotation.specificsCacheNames()).willReturn(CACHE_NAMES);
        given(putCacheAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(new String[] {});
        given(joinPoint.getArgs()).willReturn(new Object[] {});
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(EXPECTED_VALUE);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(true);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
            verify(cachingUtil, times(1)).removeAllCache(any());
        }, true);
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call to cache"
        + " DataPresentation,"
        + " WHEN putCache,"
        + " THEN the result should be the expected result")
    final void testPutCacheDataPresentation() throws Throwable {

        // GIVEN
        final DataPresentation<Integer> expectedDataPresentation
            = new TestDataPresentation(EXPECTED_VALUE);
        mockJoinPoint(expectedDataPresentation);
        given(putCacheAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(putCacheAnnotation.key()).willReturn("");
        given(putCacheAnnotation.value()).willReturn("");
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(expectedDataPresentation);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedDataPresentation);
        });
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call to cache "
        + "DataPresentation with null ID,"
        + " WHEN putCache,"
        + " THEN the result should be the expected result")
    final void testPutCacheDataPresentationWithNullId() throws Throwable {

        // GIVEN
        final DataPresentation<Integer> expectedDataPresentation
            = new TestDataPresentation(null);
        mockJoinPoint(expectedDataPresentation);
        given(putCacheAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(putCacheAnnotation.key()).willReturn("");
        given(putCacheAnnotation.value()).willReturn("");
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(joinPoint.getArgs()).willReturn(new Object[] {
            expectedDataPresentation
        });
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(expectedDataPresentation);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedDataPresentation);
        });
    }

    @Test
    @DisplayName("GIVEN the putCache aspect method call to cache "
        + "DataPresentation,"
        + " WHEN putCache,"
        + " THEN the result should be the expected result")
    final void testPutCacheDataPresentationAndNullSpelExpression()
        throws Throwable {

        // GIVEN
        final DataPresentation<Integer> expectedDataPresentation
            = new TestDataPresentation(EXPECTED_VALUE);
        mockJoinPoint(expectedDataPresentation);
        given(putCacheAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(putCacheAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito.<Class<? extends Annotation>>given(
            putCacheAnnotation.annotationType()).willReturn(PutCache.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(new String[] {});
        given(joinPoint.getArgs()).willReturn(new Object[] {});
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(cachingUtil.putCache(any(), any(), any()))
            .willReturn(expectedDataPresentation);
        given(putCacheAnnotation.refreshEntityCache()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().putCache(joinPoint, target, putCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(expectedDataPresentation);
        }, true);
    }

    @Test
    @DisplayName("GIVEN the removeCache aspect method call,"
        + " WHEN removeCache,"
        + " THEN the result should be the expected result")
    final void testRemoveCache() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(removeCacheAnnotation.specificsCacheNames())
            .willReturn(new String[] {});
        given(target.getCacheNames()).willReturn(CACHE_NAMES);
        given(removeCacheAnnotation.key()).willReturn("");
        given(removeCacheAnnotation.value()).willReturn("");
        BDDMockito
            .<Class<? extends Annotation>>given(
                removeCacheAnnotation.annotationType())
            .willReturn(RemoveCache.class);
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(removeCacheAnnotation.refreshEntityCache()).willReturn(false);
        mockForLog(() -> {

            // WHEN
            final Object result = getAspect().removeCache(joinPoint, target,
                removeCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
            verify(cachingUtil, times(1)).removeAllCache(any());
        });
    }

    @Test
    @DisplayName("GIVEN the removeCache aspect method call with annotation "
        + "value,"
        + " WHEN removeCache,"
        + " THEN the result should be the expected result")
    final void testRemoveCacheWithAnnotationValue() throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(removeCacheAnnotation.specificsCacheNames())
            .willReturn(CACHE_NAMES);
        given(removeCacheAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito
            .<Class<? extends Annotation>>given(
                removeCacheAnnotation.annotationType())
            .willReturn(RemoveCache.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(getParameterName());
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(removeCacheAnnotation.refreshEntityCache()).willReturn(true);
        mockForLog(() -> {

            // WHEN
            final Object result = getAspect().removeCache(joinPoint, target,
                removeCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
            verify(cachingUtil, times(1)).removeCache(any(), any());
            verify(cachingUtil, times(1)).removeAllCache(any());
        }, true);
    }

    @Test
    @DisplayName("GIVEN the removeCache aspect method call with annotation "
        + "value,"
        + " WHEN removeCache with a null SpEL,"
        + " THEN the result should be the expected result")
    final void testRemoveCacheWithAnnotationValueAndNullSpelExpression()
        throws Throwable {

        // GIVEN
        mockJoinPoint(EXPECTED_VALUE);
        given(removeCacheAnnotation.specificsCacheNames())
            .willReturn(CACHE_NAMES);
        given(removeCacheAnnotation.key()).willReturn(SPEL_EXPRESSION);
        BDDMockito
            .<Class<? extends Annotation>>given(
                removeCacheAnnotation.annotationType())
            .willReturn(RemoveCache.class);
        given(joinPoint.getSignature()).willReturn(signature);
        given(signature.getParameterNames()).willReturn(new String[] {});
        given(joinPoint.getArgs()).willReturn(new Object[] {});
        given(target.getCachingUtil()).willReturn(cachingUtil);
        given(removeCacheAnnotation.refreshEntityCache()).willReturn(true);
        mockForLog(() -> {

            // WHEN
            final Object result = getAspect().removeCache(joinPoint, target,
                removeCacheAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
            verify(cachingUtil, times(1)).removeCache(any(), any());
            verify(cachingUtil, times(1)).removeAllCache(any());
        }, true);
    }

    private record TestDataPresentation(@Nullable Integer id)
        implements DataPresentation<Integer> {

        @Serial
        private static final long serialVersionUID = -4563739589697889249L;

        @Override
        public int compareTo(final DataPresentation<Integer> o) {

            return Objects.requireNonNull(getId())
                .compareTo(Objects.requireNonNull(o.getId()));
        }

        @Override
        public Integer getId() {

            return id;
        }
    }
}
