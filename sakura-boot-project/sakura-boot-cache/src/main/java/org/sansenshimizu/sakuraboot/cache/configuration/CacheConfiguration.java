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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.annotations.Cache;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;

/**
 * The class CacheConfiguration create a {@link CacheManager} that can be
 * configured with {@link CachesSpecification}. The
 * {@link CacheManager} will be of type
 * {@link TransactionAwareCacheManagerProxy} to allow caching work with
 * transactional services.
 *
 * @author Malcolm Rozé
 * @see    CachesSpecification
 * @see    BasicCacheResolver
 * @since  0.1.0
 */
@SuppressWarnings("java:S1166")
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({
    Caching.class, JCacheCacheManager.class
})
@ConditionalOnMissingBean(org.springframework.cache.CacheManager.class)
@EnableConfigurationProperties(CachesSpecification.class)
@EnableCaching
public class CacheConfiguration {

    /**
     * The string used to identify the persistence layer.
     */
    private static final String PERSISTENCE_STRING = "persistence";

    /**
     * The string used to identify the business layer.
     */
    private static final String BUSINESS_STRING = "business";

    /**
     * Creates a CacheManagerCustomizer that sets the transaction awareness of
     * the CacheManager to true.
     *
     * @return a CacheManagerCustomizer that sets the transaction awareness of
     *         the CacheManager to true
     */
    @Bean
    public
        CacheManagerCustomizer<AbstractTransactionSupportingCacheManager>
        cacheManagerCustomizer() {

        return cacheManager -> cacheManager.setTransactionAware(true);
    }

    /**
     * Creates a JCache Cache Manager based on the provided specifications and
     * customizers.
     *
     * @param  cachesSpecificationHolder the specifications for caches
     * @param  cacheConfiguration        the cache configuration provider
     * @param  cacheManagerCustomizers   the customizers for the cache manager
     * @param  <K>                       the key type for the cache
     * @param  <V>                       the value type for the cache
     * @return                           the created JCache Cache Manager
     */
    @Bean
    @ConditionalOnMissingBean
    /* @formatter:off */
    public <K, V> CacheManager jCacheCacheManager(
        final CachesSpecification.CachesSpecificationHolder
            cachesSpecificationHolder,
        /* @formatter:on */
        final ObjectProvider<
            javax.cache.configuration.Configuration<K, V>> cacheConfiguration,
        final ObjectProvider<JCacheManagerCustomizer> cacheManagerCustomizers) {

        final CacheManager jCacheCacheManager
            = Caching.getCachingProvider().getCacheManager();

        final CachesSpecification cachesSpecification
            = cachesSpecificationHolder.cachesSpecification();
        final List<CachesSpecification.CacheSpecification> cacheSpecifications
            = cachesSpecification.caches();

        if (cacheSpecifications != null) {

            cacheSpecifications
                .forEach(cacheSpecification -> createCache(cacheConfiguration,
                    cacheSpecification, jCacheCacheManager,
                    cachesSpecification.activeL2Cache(),
                    cachesSpecification.dtoPackage()));
        }

        cacheManagerCustomizers.orderedStream()
            .forEach(customizer -> customizer.customize(jCacheCacheManager));
        return jCacheCacheManager;
    }

    private static <K, V> void createCache(
        final ObjectProvider<
            javax.cache.configuration.Configuration<K, V>> cacheConfiguration,
        final CachesSpecification.CacheSpecification cacheSpecification,
        final CacheManager jCacheCacheManager, final boolean activeL2Cache,
        @Nullable final String dtoPackage) {

        if (activeL2Cache) {

            final javax.cache.configuration.Configuration<?,
                ?> actualSecondLevelConfiguration
                    = Objects.requireNonNullElseGet(
                        cacheSpecification.secondLevelConfiguration(),
                        () -> cacheConfiguration
                            .getObject(Pair.of(Object.class, Object.class)));
            createL2Cache(actualSecondLevelConfiguration, cacheSpecification,
                jCacheCacheManager);
            createL2CacheForField(actualSecondLevelConfiguration,
                cacheSpecification, jCacheCacheManager,
                cacheSpecification.relationships());
        }

        if (!Boolean.FALSE.equals(cacheSpecification.activeSpringCache())) {

            final javax.cache.configuration.Configuration<?,
                ?> actualSpringCacheConfiguration;
            final javax.cache.configuration.Configuration<?, ?> configuration
                = cacheSpecification.secondLevelConfiguration();

            if (configuration != null) {

                actualSpringCacheConfiguration = configuration;
            } else {

                final CacheType cacheType
                    = getCacheType(cacheSpecification, dtoPackage);
                actualSpringCacheConfiguration = cacheConfiguration.getObject(
                    Pair.of(cacheType.keyType(), cacheType.valueType()));
            }
            final javax.cache.configuration.Configuration<?,
                ?> actualSpringCacheAllConfiguration
                    = Objects.requireNonNullElseGet(
                        cacheSpecification.secondLevelConfiguration(),
                        () -> cacheConfiguration
                            .getObject(Pair.of(String.class, Page.class)));
            final String cacheName
                = Objects.requireNonNullElseGet(cacheSpecification.name(),
                    cacheSpecification.type()::getSimpleName);
            createSpringCache(actualSpringCacheConfiguration,
                actualSpringCacheAllConfiguration, cacheName,
                jCacheCacheManager);
        }
    }

    private static <K, V> void createL2Cache(
        final javax.cache.configuration.Configuration<K, V> cacheConfiguration,
        final CachesSpecification.CacheSpecification cacheSpecification,
        final CacheManager jCacheCacheManager) {

        if (cacheSpecification.type().isAnnotationPresent(Cache.class)
            || cacheSpecification.type().isAnnotationPresent(Cacheable.class)) {

            jCacheCacheManager.createCache(cacheSpecification.type().getName(),
                cacheConfiguration);
        }
    }

    private static <K, V> void createL2CacheForField(
        final javax.cache.configuration.Configuration<K, V> cacheConfiguration,
        final CachesSpecification.CacheSpecification cacheSpecification,
        final CacheManager jCacheCacheManager,
        @Nullable final List<String> relationships) {

        final List<String> names;

        if (relationships == null) {

            final List<Field> fields = FieldUtils.getFieldsListWithAnnotation(
                cacheSpecification.type(), Cache.class);
            fields.addAll(FieldUtils.getFieldsListWithAnnotation(
                cacheSpecification.type(), Cacheable.class));
            names = fields.stream().map(Field::getName).toList();
        } else {

            names = relationships;
        }
        names.forEach(name -> jCacheCacheManager.createCache(
            cacheSpecification.type().getName() + "." + name,
            cacheConfiguration));
    }

    private static CacheType getCacheType(
        final CachesSpecification.CacheSpecification cacheSpecification,
        @Nullable final String dtoPackage) {

        Class<?> keyType;
        Class<?> valueType;

        try {

            keyType
                = cacheSpecification.type().getDeclaredField("id").getType();
        } catch (final NoSuchFieldException e) {

            keyType = Serializable.class;
        }

        if (dtoPackage != null) {

            try {

                valueType = Class.forName(
                    cacheSpecification.type()
                        .getName()
                        .replace(PERSISTENCE_STRING, dtoPackage)
                        + "Dto",
                    false, Thread.currentThread().getContextClassLoader());
            } catch (final ClassNotFoundException e) {

                valueType = cacheSpecification.type();
            }
        } else {

            valueType = cacheSpecification.type();
        }
        return new CacheType(keyType, valueType);
    }

    private static <K, V, K2, V2> void createSpringCache(
        final javax.cache.configuration.Configuration<K, V> cacheConfiguration,
        final javax.cache.configuration.Configuration<K2,
            V2> cacheAllConfiguration,
        final String cacheName, final CacheManager jCacheCacheManager) {

        jCacheCacheManager.createCache(cacheName, cacheConfiguration);
        jCacheCacheManager.createCache(cacheName + "All",
            cacheAllConfiguration);
    }

    /**
     * Creates a CachesSpecification based on the entities retrieved from the
     * EntityManager.
     *
     * @param  appContext          the ApplicationContext.
     * @param  cachesSpecification the {@link CachesSpecification}.
     * @return                     a {@code CachesSpecificationHolder} based on
     *                             the retrieved entities
     */
    @Bean
    @ConditionalOnMissingBean
    public
        CachesSpecification.CachesSpecificationHolder cachesSpecificationHolder(
            final ApplicationContext appContext,
            final CachesSpecification cachesSpecification) {

        final CachesSpecification actualCachesSpecification;
        final List<CachesSpecification.CacheSpecification> cacheSpecifications
            = cachesSpecification.caches();

        if (cacheSpecifications != null && !cacheSpecifications.isEmpty()) {

            actualCachesSpecification = cachesSpecification;
        } else {

            try {

                actualCachesSpecification = new CachesSpecification(
                    new EntityScanner(appContext).scan(Entity.class)
                        .stream()
                        .map(CacheConfiguration::getCacheSpecification)
                        .toList(),
                    cachesSpecification.activeL2Cache(),
                    Objects.requireNonNullElse(cachesSpecification.dtoPackage(),
                        BUSINESS_STRING));
            } catch (final ClassNotFoundException e) {

                throw new BeanInstantiationException(
                    CachesSpecification.CachesSpecificationHolder.class,
                    "Factory method 'cachesSpecificationHolder' threw "
                        + "exception with a message: "
                        + e.getMessage(),
                    e);
            }
        }
        return new CachesSpecification.CachesSpecificationHolder(
            actualCachesSpecification);
    }

    private static CachesSpecification.CacheSpecification getCacheSpecification(
        final Class<?> entity) {

        try {

            final Class<?> serviceClass = Class.forName(
                entity.getName().replace(PERSISTENCE_STRING, BUSINESS_STRING)
                    + "Service");
            final boolean activeSpringCache
                = org.sansenshimizu.sakuraboot.cache.api.Cacheable.class
                    .isAssignableFrom(serviceClass);
            return CachesSpecification.CacheSpecification.simple(entity,
                activeSpringCache);
        } catch (final ClassNotFoundException e) {

            return CachesSpecification.CacheSpecification.simple(entity, false);
        }
    }

    /**
     * Creates a javax.cache.configuration.Configuration object with the given
     * key and value types, sets the expiry policy factory to a
     * CreatedExpiryPolicy with a duration of 1 minute, and returns the
     * configuration object.
     *
     * @param  cacheType a Pair object containing the key and value types for
     *                   the configuration
     * @param  <K>       the type of the key for the configuration
     * @param  <V>       the type of the value for the configuration
     * @return           a {@code javax.cache.configuration.Configuration}
     *                   object with the given key and value types, and a
     *                   CreatedExpiryPolicy with a duration of 1 minute
     */
    @Bean
    @ConditionalOnMissingBean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public <
        K, V> javax.cache.configuration.Configuration<K, V>
        jCacheCacheConfiguration(final Pair<Class<K>, Class<V>> cacheType) {

        return new MutableConfiguration<K, V>()
            .setTypes(cacheType.getFirst(), cacheType.getSecond())
            .setExpiryPolicyFactory(CreatedExpiryPolicy
                .factoryOf(new Duration(TimeUnit.MINUTES, 1)));
    }

    /**
     * Creates a {@link BasicCacheResolver} bean if it is missing in the
     * application context.
     *
     * @param  cacheManagers the ObjectProvider for
     *                       {@code org.springframework.cache.CacheManager}
     * @return               the created BasicCacheResolver bean
     */
    @Bean
    @ConditionalOnMissingBean(CacheResolver.class)
    public BasicCacheResolver basicCacheResolver(
        final ObjectProvider<
            org.springframework.cache.CacheManager> cacheManagers) {

        return new BasicCacheResolver(cacheManagers);
    }

    private record CacheType(Class<?> keyType, Class<?> valueType) {}
}
