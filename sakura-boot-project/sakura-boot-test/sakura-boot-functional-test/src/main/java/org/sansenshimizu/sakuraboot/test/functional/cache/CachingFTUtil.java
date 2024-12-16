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

package org.sansenshimizu.sakuraboot.test.functional.cache;

import java.io.Serializable;

import javax.cache.Caching;

import org.springframework.cache.CacheManager;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.functional.BasicFTUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The interface for all the utility functional test function. This interface
 * provides common functions for testing caching class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link CachingFTUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link CachingFTUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourFTUtil //
 *     implements CachingFTUtil&lt;YourEntity, YourIdType&gt; {
 *
 *     private final GlobalSpecification globalSpecification;
 *
 *     private final CacheManager cacheManager;
 *
 *     &#064;Autowired
 *     public YourFTUtil(
 *         final GlobalSpecification globalSpecification,
 *         final CacheManager cacheManager) {
 *
 *         this.globalSpecification = globalSpecification;
 *         this.cacheManager = cacheManager;
 *     }
 *
 *     &#064;Override
 *     public GlobalSpecification getGlobalSpecification() {
 *
 *         return globalSpecification;
 *     }
 *
 *     &#064;Override
 *     public Optional&lt;YourEntity&gt; createValidation
 *     ErrorEntity(YourIdType id) {
 *
 *         return YourEntity.builder().id(id).build();
 *         // If your class don't have a builder you can use the constructor
 *     }
 *
 *     &#064;Override
 *     public CacheManager getCacheManager() {
 *
 *         return cacheManager;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicFTUtil
 * @since      0.1.0
 */
public interface CachingFTUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicFTUtil<E, I> {

    /**
     * Get the cache manager.
     *
     * @return the cacheManager
     */
    CacheManager getCacheManager();

    /**
     * Assert that the cache is created.
     */
    default void assertCacheCreated() {

        final javax.cache.CacheManager jCacheManager
            = Caching.getCachingProvider().getCacheManager();

        for (final String cacheName: getCacheNames()) {

            assertThat(jCacheManager.getCache(cacheName)).isNotNull();
            assertThat(jCacheManager.getCache(cacheName + "All")).isNotNull();
        }
    }

    /**
     * Return an array of string containing the name of the different cache.
     *
     * @return An array of cache name.
     */
    default String[] getCacheNames() {

        return new String[] {
            getEntityClass().getSimpleName()
        };
    }
}
