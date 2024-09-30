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
import java.util.Objects;

import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * The super interface for all unit tests. This interface has no test but can be
 * used to simplify the access to the {@link SuperTestUtil} interface.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link SuperTest},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourTest implements SuperTest&lt;YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperTestUtil
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SuperTest<I extends Comparable<? super I> & Serializable> {

    /**
     * Return a util class of type {@link SuperTestUtil}.
     *
     * @return A util class for testing.
     */
    @SuppressWarnings("EmptyMethod")
    SuperTestUtil<I> getUtil();

    /**
     * Get a valid ID from the {@link SuperTestUtil} interface.
     *
     * @return A valid ID.
     */
    default I getValidId() {

        return Objects.requireNonNull(getUtil().getValidId(),
            "The valid ID mustn't be null");
    }

    /**
     * Get another valid ID from the {@link SuperTestUtil} interface. (same ID
     * as {@link #getBiggerValidId()})
     *
     * @return A valid ID.
     */
    default I getOtherValidId() {

        return Objects.requireNonNull(getUtil().getBiggerValidId(),
            "The other valid ID mustn't be null");
    }

    /**
     * Get another valid ID that is bigger than the first valid ID from the
     * {@link SuperTestUtil} interface.
     * (same ID as {@link #getOtherValidId()})
     *
     * @return A big valid ID.
     */
    default I getBiggerValidId() {

        return Objects.requireNonNull(getUtil().getBiggerValidId(),
            "The bigger valid ID mustn't be null");
    }

    /**
     * Get an invalid ID from the {@link SuperTestUtil} interface.
     *
     * @return An invalid ID.
     */
    default I getInvalidId() {

        return Objects.requireNonNull(getUtil().getInvalidId(),
            "The invalid ID mustn't be null");
    }

    /**
     * A simple implementation of {@link DataPresentation} to have a different
     * type for testing.
     *
     * @param  <I> The ID of type Comparable and Serializable.
     * @param  id  The id if needed in the tests.
     * @author     Malcolm Rozé
     * @since      0.1.0
     */
    record DataPresentationForTest<I extends Comparable<
        ? super I> & Serializable>(@Nullable I id)
        implements DataPresentation<I> {

        @Override
        public int compareTo(final DataPresentation<I> o) {

            return Objects.requireNonNull(id)
                .compareTo(Objects.requireNonNull(o.getId()));
        }

        @Override
        @Nullable
        public I getId() {

            return id;
        }
    }
}
