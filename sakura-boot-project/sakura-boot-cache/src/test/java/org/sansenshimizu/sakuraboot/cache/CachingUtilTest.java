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

package org.sansenshimizu.sakuraboot.cache;

import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * The test class for all CachingUtil. This class provides common tests for
 * testing {@link CachingUtil} with the {@link DefaultCachingUtil}
 * implementation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a specific cachingUtilTest for your specific {@link CachingUtil}
 * that inherits from {@link CachingUtilTest}, follow these steps:
 * </p>
 * <p>
 * Create a new caching util test class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourCachingUtilTest extends CachingUtilTest {
 *     // Add your test or override the getter to customize the existing tests.
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * <b>NOTE:</b> This test class already have all the methods to test the
 * caching. All the getters are protected so you can customize the tests if
 * needed.
 * </p>
 *
 * @author Malcolm Rozé
 * @see    CachingUtil
 * @see    DefaultCachingUtil
 * @since  0.1.0
 */
@Getter
public class CachingUtilTest {

    /**
     * The cache names use for this test.
     */
    public static final String[] CACHE_NAMES = {
        "cache"
    };

    /**
     * A key use in test.
     */
    public static final int KEY = 0;

    /**
     * The expected result use for testing.
     */
    public static final Object EXPECTED_RESULT = new Object();

    /**
     * The tested {@link DefaultCachingUtil} of type {@link CachingUtil}.
     */
    private final CachingUtil cachingUtil = new DefaultCachingUtil();

    @Test
    @DisplayName("GIVEN an expected result in a supplier,"
        + " WHEN caching,"
        + " THEN the cachingUtil should return the expected result")
    final void testCaching() throws Throwable {

        // GIVEN
        final CachingUtil.Supplier<Object> supplier = () -> EXPECTED_RESULT;

        // WHEN
        final Object result
            = getCachingUtil().caching(CACHE_NAMES, supplier, KEY);

        // THEN
        assertThat(result).isEqualTo(EXPECTED_RESULT);
    }

    @Test
    @DisplayName("GIVEN an expected result,"
        + " WHEN putCache,"
        + " THEN the cachingUtil should return the expected result")
    final void testPutCache() {

        // WHEN
        final Object result
            = getCachingUtil().putCache(CACHE_NAMES, EXPECTED_RESULT, KEY);

        // THEN
        assertThat(result).isEqualTo(EXPECTED_RESULT);
    }

    @Test
    @DisplayName("GIVEN nothing,"
        + " WHEN removeCache,"
        + " THEN nothing should be done")
    final void testRemoveCache() {

        // WHEN
        assertDoesNotThrow(
            () -> getCachingUtil().removeCache(CACHE_NAMES, KEY));
    }

    @Test
    @DisplayName("GIVEN nothing,"
        + " WHEN removeAllCache,"
        + " THEN nothing should be done")
    final void testRemoveAllCache() {

        // WHEN
        assertDoesNotThrow(() -> getCachingUtil().removeAllCache(CACHE_NAMES));
    }
}
