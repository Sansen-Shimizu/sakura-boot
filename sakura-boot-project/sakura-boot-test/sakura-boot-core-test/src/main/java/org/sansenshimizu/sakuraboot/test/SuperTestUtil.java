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

import org.springframework.lang.Nullable;

/**
 * The interface for all the utility test function.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link SuperTestUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperTestUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourTestUtil implements SuperTestUtil&lt;YourIdType&gt; {
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
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
public interface SuperTestUtil<I extends Comparable<? super I> & Serializable> {

    /**
     * A valid ID use in test.
     *
     * @return A valid ID.
     */
    @Nullable
    I getValidId();

    /**
     * Another valid ID use in test that is bigger than the first valid ID.
     *
     * @return A big valid ID.
     */
    @Nullable
    I getBiggerValidId();

    /**
     * An invalid ID use in test.
     *
     * @return An invalid ID.
     */
    @Nullable
    I getInvalidId();
}
