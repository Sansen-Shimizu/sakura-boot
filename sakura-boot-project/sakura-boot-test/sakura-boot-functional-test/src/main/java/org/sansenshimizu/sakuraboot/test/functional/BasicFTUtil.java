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

package org.sansenshimizu.sakuraboot.test.functional;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.SuperITUtil;

/**
 * The interface for all the utility functional test function. This interface
 * provides common functions for testing basic class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link BasicFTUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicFTUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourITUtil //
 *     implements BasicFTUtil&lt;YourEntity, YourIdType&gt; {
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
 *     public String getPath() {
 *
 *         return "api/pathName";
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperITUtil
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BasicFTUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperITUtil<E, I> {

    @Override
    @Nullable
    default I getValidId() {

        return null;
    }

    @Override
    @Nullable
    default I getBiggerValidId() {

        return null;
    }

    @Override
    @Nullable
    default I getInvalidId() {

        return null;
    }

    /**
     * Create a new entity with the given id.
     * Must be an entity with value with validation error.
     * If the entity has no validation return {@code Optional.empty()}.
     *
     * @param  id The id to give to the new entity.
     * @return    The created entity.
     */
    default Optional<E> createValidationErrorEntity(final @Nullable I id) {

        return Optional.empty();
    }

    /**
     * Get the path to use in this test.
     *
     * @return The path.
     */
    String getPath();
}
