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

package org.sansenshimizu.sakuraboot;

import java.io.Serializable;

import org.springframework.lang.Nullable;

/**
 * The basic interface for every data exposed to the presentation layer.
 *
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
public interface DataPresentation<
    I extends Comparable<? super I> & Serializable>
    extends Serializable, Comparable<DataPresentation<I>> {

    /**
     * The getter for the id that each DataPresentation need to have.
     *
     * @return The id of this DataPresentation.
     */
    @Nullable
    I getId();

    /**
     * A marker interface that can be used to represent a full data object. This
     * interface can be used as a parameter in validation annotations to specify
     * that certain fields should be validated when performing full data
     * operations.
     * <p>
     * <b>Example:</b>
     * </p>
     * <blockquote>
     *
     * <pre>
     *
     * &#064;NotNull(groups = DataPresentation.FullDto.class)
     * private Object yourField;
     * </pre>
     *
     * </blockquote>
     *
     * @author Malcolm Rozé
     * @since  0.1.0
     */
    interface FullData {}
}
