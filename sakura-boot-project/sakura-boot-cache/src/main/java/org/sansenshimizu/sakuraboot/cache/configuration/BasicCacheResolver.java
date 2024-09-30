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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;

/**
 * The class BasicCacheResolver implements {@link CacheResolver} and is used to
 * provide the necessary cache for the {@link CachingUtil} class.
 *
 * @author Malcolm Rozé
 * @see    CacheResolver
 * @see    CacheConfiguration
 * @see    CachingUtil
 * @since  0.1.0
 */
@RequiredArgsConstructor
public class BasicCacheResolver implements CacheResolver {

    /**
     * The {@link CacheManager}.
     */
    private final ObjectProvider<CacheManager> cacheManagers;

    @Override
    public Collection<? extends Cache> resolveCaches(
        final CacheOperationInvocationContext<?> context) {

        final Collection<Cache> caches = new ArrayList<>();

        if (context.getTarget() instanceof CachingUtil) {

            for (final String cacheName: (String[]) context.getArgs()[0]) {

                cacheManagers.orderedStream()
                    .map(cacheManager -> cacheManager.getCache(cacheName))
                    .filter(Objects::nonNull)
                    .forEach(caches::add);
            }
        }
        return caches;
    }
}
