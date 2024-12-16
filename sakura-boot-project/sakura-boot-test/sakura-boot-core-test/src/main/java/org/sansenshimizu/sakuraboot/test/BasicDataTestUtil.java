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

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * The interface for all the utility test function. This interface
 * provides common functions for testing basic class with data.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from
 * {@link BasicDataTestUtil}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicDataTestUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourTestUtil //
 *     implements //
 *     BasicDataTestUtil&lt;YourEntity, YourIdType, YourDataType&gt; {
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
 * @param  <E> The entity of type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <D> The data of type {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        BasicTestUtil
 * @since      0.1.0
 */
public interface BasicDataTestUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends BasicTestUtil<E, I> {

    /**
     * Get the data class.
     *
     * @return The data class.
     */
    default Class<D> getDataClass() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            BasicDataTestUtil.class.getTypeName(), 2);
    }

    /**
     * Get the data without ID.
     *
     * @return The data without ID.
     */
    default D getDataWithoutId() {

        return DataCreatorHelper.getDataWithoutId(getDataClass(),
            getGlobalSpecification());
    }

    /**
     * Get the data.
     *
     * @return The data.
     */
    default D getData() {

        return DataCreatorHelper.getData(getDataClass(), getValidId());
    }

    /**
     * Get the second data.
     *
     * @return The second data.
     */
    default D getSecondData() {

        return DataCreatorHelper.getSecondData(getDataClass(), getValidId());
    }

    /**
     * Get the different data.
     *
     * @return The different data.
     */
    default D getDifferentData() {

        return DataCreatorHelper.getDifferentData(getDataClass(),
            getInvalidId());
    }

    /**
     * Get the partial data.
     *
     * @return The partial data.
     */
    default D getPartialData() {

        return DataCreatorHelper.getPartialData(getDataClass(), getValidId());
    }
}
