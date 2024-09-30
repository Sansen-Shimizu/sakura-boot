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
import java.time.temporal.Temporal;

import org.springframework.lang.Nullable;

/**
 * A filter interface that can be used for date values.
 *
 * @param  <T> The value type.
 * @author     Malcolm Rozé
 * @see        CommonFilter
 * @since      0.1.0
 */
public interface DateFilter<
    T extends Temporal & Comparable<? super T> & Serializable>
    extends CommonFilter<T> {

    /**
     * Filters the data to include only values from this value.
     *
     * @return The from value.
     */
    @Nullable
    T getFrom();

    /**
     * Filters the data to include only values to this value.
     *
     * @return The to value.
     */
    @Nullable
    T getTo();
}
