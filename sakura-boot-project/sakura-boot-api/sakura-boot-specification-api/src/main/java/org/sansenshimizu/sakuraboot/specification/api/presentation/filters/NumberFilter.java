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

import org.springframework.lang.Nullable;

/**
 * A filter interface that can be used for numeric values.
 *
 * @param  <N> The value type.
 * @author     Malcolm Rozé
 * @see        CommonFilter
 * @since      0.1.0
 */
public interface NumberFilter<N extends Number> extends CommonFilter<N> {

    /**
     * Filters the data to include only values greater than this value.
     *
     * @return The greater than value.
     */
    @Nullable
    N getGreaterThan();

    /**
     * Filters the data to include only values greater than or equal to this
     * value.
     *
     * @return The greater than or equal to value.
     */
    @Nullable
    N getGreaterThanOrEqual();

    /**
     * Filters the data to include only values less than this value.
     *
     * @return The less than value.
     */
    @Nullable
    N getLessThan();

    /**
     * Filters the data to include only values less than or equal to this value.
     *
     * @return The less than or equal to value.
     */
    @Nullable
    N getLessThanOrEqual();
}
