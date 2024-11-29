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

package org.sansenshimizu.sakuraboot.cache.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * Java class for cache configuration.
 *
 * @param  caches        The list of the different {@link CacheSpecification}.
 * @param  activeL2Cache If the hibernate L2 cache needs to be active.
 * @author               Malcolm Rozé
 * @since                0.1.0
 */
@ConfigurationProperties("sakuraboot.cache")
public record CachesSpecification(
    @Nullable List<CacheSpecification> caches, boolean activeL2Cache) {

    /**
     * Java class for cache specification.
     * The specification will be used to configure the cache.
     *
     * @param  type                        The class of the object to be cached.
     * @param  name                        The name of the cache. (Optional)
     * @param  keyType                     The class of the key of the cache.
     *                                     (Optional)
     * @param  valueType                   The class of the value of the cache.
     *                                     (Optional)
     * @param  activeSpringCache           If the spring cache needs to be
     *                                     active. (Optional)
     * @param  relationships               The list of the relationship names
     *                                     that need to be cached. (Optional)
     * @param  secondLevelConfiguration    The second Level cache configuration.
     *                                     (Optional)
     * @param  springCacheConfiguration    The spring cache configuration.
     *                                     (Optional)
     * @param  springCacheAllConfiguration The spring cache configuration for
     *                                     the "all" key.
     *                                     (Optional)
     * @author                             Malcolm Rozé
     * @since                              0.1.0
     */
    public record CacheSpecification(
        Class<?> type, @Nullable String name, @Nullable Class<?> keyType,
        @Nullable Class<?> valueType, @Nullable Boolean activeSpringCache,
        @Nullable List<String> relationships,
        @Nullable javax.cache.configuration.Configuration<?,
            ?> secondLevelConfiguration,
        @Nullable javax.cache.configuration.Configuration<?,
            ?> springCacheConfiguration,
        @Nullable javax.cache.configuration.Configuration<?,
            ?> springCacheAllConfiguration) {

        /**
         * Create a simple {@code CacheSpecification}.
         *
         * @param  type              The class of the object to be cached.
         * @param  activeSpringCache If the spring cache needs to be active.
         * @return                   a simple {@code CacheSpecification}.
         */
        public static CacheSpecification simple(
            final Class<?> type, final boolean activeSpringCache) {

            return new CacheSpecification(type, null, null, null,
                activeSpringCache, null, null, null, null);
        }
    }

    /**
     * Holder for {@link CachesSpecification}.
     *
     * @param  cachesSpecification A {@link CachesSpecification}.
     * @author                     Malcolm Rozé
     * @since                      0.1.0
     */
    public record CachesSpecificationHolder(
        CachesSpecification cachesSpecification) {}
}
