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

package org.sansenshimizu.sakuraboot.test.cache.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.sansenshimizu.sakuraboot.cache.api.Cacheable;
import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base test interface for all Cacheable classes. This interface provides
 * common tests for testing {@link Cacheable}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link CacheableTest},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link CacheableTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourCacheableClassTest implements CacheableTest {
 *
 *     &#064;InjectMocks
 *     private YourCacheableClass yourCacheableClass;
 *
 *     &#064;Mock
 *     private DefaultCachingUtil cachingUtil;
 *
 *     &#064;Override
 *     public YourCacheableClass getCacheable() {
 *
 *         return yourCacheableClass;
 *     }
 *
 *     &#064;Override
 *     public DefaultCachingUtil getCachingUtil() {
 *
 *         return cachingUtil;
 *     }
 *
 *     &#064;Override
 *     public String[] getExpectedCacheNames() {
 *
 *         return new String[] {
 *             "cacheName"
 *         };
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @see    Cacheable
 * @since  0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface CacheableTest {

    /**
     * Get the class to test, that implements {@link Cacheable}. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link Cacheable}.
     */
    Cacheable getCacheable();

    /**
     * Get the expected cache names uses in test. Need to be the same cache
     * names from the class that implements {@link Cacheable}.
     *
     * @return An array of expected cache names.
     */
    String[] getExpectedCacheNames();

    /**
     * Get the {@link CachingUtil} for test. Need to be {@link Mock}.
     *
     * @return A {@link CachingUtil}.
     */
    CachingUtil getCachingUtil();

    @Test
    @DisplayName("GIVEN a Cacheable,"
        + " WHEN getting the cacheNames,"
        + " THEN the correct cacheNames should be returned")
    default void testGetCacheNames() {

        // GIVEN
        final String[] expectedCacheNames = getExpectedCacheNames();

        // WHEN
        final String[] cacheNames = getCacheable().getCacheNames();

        // THEN
        assertThat(cacheNames).isEqualTo(expectedCacheNames);
    }

    @Test
    @DisplayName("GIVEN a Cacheable,"
        + " WHEN getting the CachingUtil,"
        + " THEN the correct CachingUtil should be returned")
    default void testGetCacheUtil() {

        // GIVEN
        final CachingUtil expectedCachingUtil = getCachingUtil();

        // WHEN
        final CachingUtil cachingUtil = getCacheable().getCachingUtil();

        // THEN
        assertThat(cachingUtil).isEqualTo(expectedCachingUtil);
    }
}
