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

package org.sansenshimizu.sakuraboot.test.mapper.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base test interface for all Mappable classes. This interface provides
 * common tests for testing {@link Mappable}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link MappableTest},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link MappableTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourMappableClassTest
 *     implements MappableTest&lt;YourEntity, YourDto&gt; {
 *
 *     &#064;InjectMocks
 *     private YourMappableClass yourMappableClass;
 *
 *     &#064;Mock
 *     private BasicMapper&lt;YourEntity, YourDto&gt; mapper;
 *
 *     &#064;Override
 *     public YourCacheableClass getCacheable() {
 *
 *         return yourCacheableClass;
 *     }
 *
 *     &#064;Override
 *     public BasicMapper&lt;YourEntity, YourDto&gt; getMapper() {
 *
 *         return mapper;
 *     }
 *
 *     &#064;Override
 *     public Class&lt;YourEntity&gt; getExpectedEntityClass();
 *
 *     {
 *
 *         return YourEntity.class;
 *     }
 *
 *     &#064;Override
 *     public Class&lt;YourDto&gt; getExpectedDtoClass();
 *
 *     {
 *
 *         return YourDto.class;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <D> The DTO type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        Mappable
 * @since      0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface MappableTest<E extends DataPresentation<?>,
    D extends DataPresentation<?>> {

    /**
     * Get the class to test, that implements {@link Mappable}. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link Mappable}.
     */
    Mappable<E, D> getMappable();

    /**
     * Get the {@link BasicMapper} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicMapper}.
     */
    BasicMapper<E, D> getMapper();

    /**
     * Get the expected entity class uses in test. Need to be the same entity
     * class from the class that implements {@link Mappable}.
     *
     * @return An entity class.
     */
    Class<E> getExpectedEntityClass();

    /**
     * Get the expected DTO class uses in test. Need to be the same DTO
     * class from the class that implements {@link Mappable}.
     *
     * @return A DTO class.
     */
    Class<D> getExpectedDtoClass();

    @Test
    @DisplayName("GIVEN a Mappable,"
        + " WHEN getting the mapper,"
        + " THEN the correct mapper should be returned")
    default void testGetMapper() {

        // GIVEN
        final BasicMapper<E, D> expectedMapper = getMapper();

        // WHEN
        final BasicMapper<E, D> mapper = getMappable().getMapper();

        // THEN
        assertThat(mapper).isEqualTo(expectedMapper);
    }

    @Test
    @DisplayName("GIVEN a Mappable,"
        + " WHEN getting the entity class,"
        + " THEN the correct entity class should be returned")
    default void testGetEntityClass() {

        // GIVEN
        final Class<E> expectedEntityClass = getExpectedEntityClass();

        // WHEN
        final Class<E> entityClass = getMappable().getEntityClass();

        // THEN
        assertThat(entityClass).isEqualTo(expectedEntityClass);
    }

    @Test
    @DisplayName("GIVEN a Mappable,"
        + " WHEN getting the DTO class,"
        + " THEN the correct DTO class should be returned")
    default void testGetDtoClass() {

        // GIVEN
        final Class<D> expectedDtoClass = getExpectedDtoClass();

        // WHEN
        final Class<D> dtoClass = getMappable().getDtoClass();

        // THEN
        assertThat(dtoClass).isEqualTo(expectedDtoClass);
    }
}
