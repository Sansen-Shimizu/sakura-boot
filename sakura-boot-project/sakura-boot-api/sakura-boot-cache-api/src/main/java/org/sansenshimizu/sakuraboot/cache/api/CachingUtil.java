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

package org.sansenshimizu.sakuraboot.cache.api;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;

/**
 * A basic CachingHelper class providing common cache operations.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a cachingHelper that inherits from {@link CachingUtil}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new cachingUtil class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourCachingUtil implements cachingUtil {
 *     // Your custom method.
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @see    org.springframework.cache.annotation.Caching
 * @see    Cacheable
 * @see    CacheEvict
 * @see    CachePut
 * @since  0.1.0
 */
@CacheConfig(cacheResolver = "basicCacheResolver")
public interface CachingUtil {

    /**
     * This method will cache the result of the given function with the key of
     * the given id argument.
     * The given function will only be called if the cache with the key of the
     * given id argument doesn't exist.
     *
     * @param  cacheNames The cache names use by the cache resolver to return
     *                    the necessary cache.
     *                    If multiple caches have the same name, value will be
     *                    used in different cache.
     * @param  callable   The function to call if there is no cache.
     * @param  key        The object that will be used for the key of the cache.
     * @return            The result of the given function or the value of the
     *                    cache.
     * @throws Throwable  If the given function throws an exception.
     */
    @Cacheable(key = "#key")
    @Nullable
    default Object caching(
        final String[] cacheNames, final Supplier<Object> callable,
        final Object key)
        throws Throwable {

        return callable.get();
    }

    /**
     * This method will always be call. Create a cache for the key of the given
     * id argument.
     *
     * @param  cacheNames The cache names use by the cache resolver to return
     *                    the necessary cache.
     *                    If multiple caches have the same name, value will be
     *                    used in different cache.
     * @param  object     The object to put in the cache.
     * @param  key        The object that will be used for the key of the cache.
     * @return            The same object.
     */
    @CachePut(key = "#key")
    @Nullable
    default Object putCache(
        final String[] cacheNames, @Nullable final Object object,
        final Object key) {

        return object;
    }

    /**
     * This method will always be call. Remove the cache for the key of the
     * given id argument.
     *
     * @param cacheNames The cache names use by the cache resolver to return the
     *                   necessary cache.
     *                   If multiple caches have the same name, value will be
     *                   used in different cache.
     * @param key        The object that will be used for the key of the cache.
     */
    @CacheEvict(key = "#key")
    default void removeCache(final String[] cacheNames, final Object key) {}

    /**
     * This method will always be call. Remove the cache for the all keys.
     *
     * @param cacheNames The cache names use by the cache resolver to return the
     *                   necessary cache.
     *                   If multiple caches have the same name, value will be
     *                   used in different cache.
     */
    @CacheEvict(allEntries = true)
    default void removeAllCache(final String[] cacheNames) {}

    /**
     * A functional interface for Supplier that can throw a {@link Throwable}.
     *
     * @param <T> The type of the value.
     */
    @FunctionalInterface
    interface Supplier<T> {

        /**
         * Retrieves the value of type T by calling the get() method of the
         * interface.
         *
         * @return           The value of type T retrieved by calling the get()
         *                   method.
         * @throws Throwable If an error occurs while retrieving the value.
         */
        @SuppressWarnings("java:S112")
        T get() throws Throwable;
    }
}
