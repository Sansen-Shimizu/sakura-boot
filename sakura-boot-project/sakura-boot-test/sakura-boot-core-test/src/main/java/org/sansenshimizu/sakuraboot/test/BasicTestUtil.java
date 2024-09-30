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

/**
 * The interface for all the utility test function. This interface provides
 * common functions for testing basic class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link BasicTestUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link BasicTestUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourTestUtil //
 *     implements BasicTestUtil&lt;YourEntity, YourIdType&gt; {
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
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperTestUtil
 * @since      0.1.0
 */
public interface BasicTestUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperTestUtil<I> {

    /**
     * Get the entity class.
     *
     * @return The entity class.
     */
    default Class<E> getEntityClass() {

        return BeanCreatorHelper.findBeanClassFromInterface(getClass(),
            BasicTestUtil.class.getTypeName());
    }

    /**
     * Get the entity.
     *
     * @return The entity.
     */
    default E getEntityWithoutId() {

        return DataCreatorHelper.getDataWithoutId(getEntityClass());
    }

    /**
     * Get the entity without ID.
     *
     * @return The entity without ID.
     */
    default E getEntity() {

        return DataCreatorHelper.getData(getEntityClass(), getValidId());
    }

    /**
     * Get the second entity.
     *
     * @return The second entity.
     */
    default E getSecondEntity() {

        return DataCreatorHelper.getSecondData(getEntityClass(), getValidId());
    }

    /**
     * Get the different entity.
     *
     * @return The different entity.
     */
    default E getDifferentEntity() {

        return DataCreatorHelper.getDifferentData(getEntityClass(),
            getInvalidId());
    }

    /**
     * Get the partial entity.
     *
     * @return The partial entity.
     */
    default E getPartialEntity() {

        return DataCreatorHelper.getPartialData(getEntityClass(), getValidId());
    }
}
