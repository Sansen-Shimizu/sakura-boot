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

package org.sansenshimizu.sakuraboot.test;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.lang.Nullable;
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * The interface for all the utility integration test function. This interface
 * provides common functions for integration testing basic class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link SuperITUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperITUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourITUtil //
 *     implements SuperITUtil&lt;YourEntity, YourIdType&gt; {
 *
 *     &#064;Override
 *     public YourIdType getValidId() {
 *
 *         return ANY_VALUE; // ANY_VALUE can be 0L, "a" or any other value of
 *                           // the type YourIdType.
 *     }
 *
 *     &#064;Override
 *     public YourIdType getBiggerValidId() {
 *
 *         return ANY_BIGGER_VALUE;
 *         // ANY_BIGGER_VALUE must be bigger than ANY_VALUE can be 1L, "b"
 *         // or any other bigger value of the type YourIdType.
 *     }
 *
 *     &#064;Override
 *     public YourIdType getInvalidId() {
 *
 *         return ANY_OTHER_VALUE;
 *         // ANY_OTHER_VALUE must be different than ANY_VALUE and
 *         // ANY_BIGGER_VALUE can be -1L, "z"
 *         // or any other value of the type YourIdType.
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicTestUtil
 * @since      0.1.0
 */
public interface SuperITUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends BasicTestUtil<E, I> {

    /**
     * Get a different entity with the given ids from the other entity.
     *
     * @param  otherEntity The other entity from which to get the ids.
     * @return             The created entity.
     */
    default E getDifferentEntityWithId(final E otherEntity) {

        return DataCreatorHelper.updateId(otherEntity,
            SerializationUtils.clone(getDifferentEntity()),
            getGlobalSpecification());
    }

    /**
     * Get the second entity with the given id.
     *
     * @param  id The id to give to the new entity.
     * @return    The created entity.
     */
    default E getPartialEntityWithId(@Nullable final I id) {

        final E partialData = SerializationUtils.clone(getPartialEntity());
        ReflectionTestUtils.setField(partialData, "id", id);
        return partialData;
    }
}
