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
 * provides common functions for integration testing basic class with data.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link SuperDataITUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperDataITUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourTestUtil //
 *     implements SuperITUtil&lt;YourEntity, YourIdType, YourDataType&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <D> The data of type {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        SuperITUtil
 * @see        BasicTestUtil
 * @since      0.1.0
 */
public interface SuperDataITUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends SuperITUtil<E, I>, BasicDataTestUtil<E, I, D> {

    /**
     * Get the different data with the given id.
     *
     * @param  ids The id to give to the new data.
     * @return     The created data.
     */
    default D getDifferentDataWithId(final DataCreatorHelper.EntityIds ids) {

        return DataCreatorHelper.updateId(ids,
            SerializationUtils.clone(getDifferentData()));
    }

    /**
     * Get the partial data with the given id.
     *
     * @param  id The id to give to the new entity.
     * @return    The created data.
     */
    default D getPartialDataWithId(@Nullable final I id) {

        final D partialData = SerializationUtils.clone(getPartialData());
        ReflectionTestUtils.setField(partialData, "id", id);
        return partialData;
    }
}
