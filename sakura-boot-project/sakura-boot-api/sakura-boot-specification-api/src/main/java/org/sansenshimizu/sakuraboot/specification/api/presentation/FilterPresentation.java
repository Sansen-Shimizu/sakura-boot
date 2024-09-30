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

package org.sansenshimizu.sakuraboot.specification.api.presentation;

import java.io.Serializable;

import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.Filter;

/**
 * The basic interface for every filter exposed to the presentation layer.
 *
 * @param  <F> The {@link Filter} type for the ID filter.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
public interface FilterPresentation<F extends Filter> extends Serializable {

    /**
     * Getter for the distinct option.
     *
     * @return The distinct option.
     */
    @Nullable
    Boolean getDistinct();

    /**
     * Getter for the inclusive option.
     *
     * @return The inclusive option.
     */
    @Nullable
    Boolean getInclusive();

    /**
     * Getter for the id filter.
     *
     * @return The id filter.
     */
    @Nullable
    F getId();
}
