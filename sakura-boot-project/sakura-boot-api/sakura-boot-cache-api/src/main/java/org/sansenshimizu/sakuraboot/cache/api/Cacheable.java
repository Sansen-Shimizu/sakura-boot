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

/**
 * Interface for all class that add cache support needs to implement.
 * <p>
 * To create a class that implements {@link Cacheable}, follow these steps:
 * </p>
 * <p>
 * Create a new class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourClass implements Cacheable {
 *
 *     private final DefaultCachingUtil cachingUtil;
 *
 *     public YourClass(final DefaultCachingUtil cachingUtil) {
 *
 *         this.cachingUtil = cachingUtil;
 *     }
 *
 *     &#064;Override
 *     public String[] getCacheNames() {
 *
 *         return new String[] {
 *             "cacheName" // Generally the name of the entity if use in a
 *                         // service.
 *         };
 *     }
 *
 *     &#064;Override
 *     public CachingUtil getCachingUtil() {
 *
 *         return cachingUtil;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
public interface Cacheable {

    /**
     * Get the cache names use in this class.
     *
     * @return An array of cache names.
     */
    String[] getCacheNames();

    /**
     * The {@link CachingUtil} use to perform the caching.
     *
     * @return A CachingUtil.
     */
    CachingUtil getCachingUtil();
}
