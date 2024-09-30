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

package org.sansenshimizu.sakuraboot.specification.api.presentation.filters;

import java.io.Serializable;
import java.util.List;

import org.springframework.lang.Nullable;

/**
 * A filter interface that can be used for collection-based values.
 *
 * @param  <T> The value type in the list.
 * @author     Malcolm Rozé
 * @see        Filter
 * @since      0.1.0
 */
public interface CollectionFilter<T extends Serializable> extends Filter {

    /**
     * Filters the data to include only values that are present in this list.
     *
     * @return The equal value.
     */
    List<T> getEqual();

    /**
     * Filters the data to exclude values that are present in this list.
     *
     * @return The not equal value.
     */
    List<T> getNotEqual();

    /**
     * Filters the data to include only values that are empty (or not empty if
     * {@code false}).
     *
     * @return The is empty value.
     */
    @Nullable
    Boolean getIsEmpty();

    /**
     * Filters the data to include only values that are members of this list.
     *
     * @return The is member value.
     */
    @Nullable
    T getIsMember();

    /**
     * Filters the data to include only values that aren't members of this list.
     *
     * @return The is not member value.
     */
    @Nullable
    T getIsNotMember();
}
