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
 * A filter interface that can be used for text {@code String} values.
 *
 * @author Malcolm Rozé
 * @see    CommonFilter
 * @since  0.1.0
 */
public interface TextFilter extends CommonFilter<String> {

    /**
     * Filters the data to include only values that contain this text.
     *
     * @return The contains value.
     */
    @Nullable
    String getContains();

    /**
     * Filters the data to exclude values that contain this text.
     *
     * @return The notContains value.
     */
    @Nullable
    String getNotContains();

    /**
     * Filters the data to include only values that start with this text.
     *
     * @return The startWith value.
     */
    @Nullable
    String getStartWith();

    /**
     * Filters the data to include only values that end with this text.
     *
     * @return The endWith value.
     */
    @Nullable
    String getEndWith();

    /**
     * Specifies whether text filtering is case-sensitive {@code true} or
     * case-insensitive {@code false}.
     *
     * @return The caseSensitive value.
     */
    @Nullable
    Boolean getCaseSensitive();
}
