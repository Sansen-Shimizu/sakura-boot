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

package org.sansenshimizu.sakuraboot.specification.presentation.filters;

import java.io.Serial;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;

/**
 * A filter for numeric values.
 *
 * @param  <N> The value type.
 * @author     Malcolm Rozé
 * @see        CommonFilterImpl
 * @see        NumberFilter
 * @since      0.1.0
 */
@SuperBuilder(toBuilder = true, builderMethodName = "numberFilterBuilder")
@Getter
@EqualsAndHashCode(callSuper = true)
public class NumberFilterImpl<N extends Number> extends CommonFilterImpl<N>
    implements NumberFilter<N> {

    @Serial
    private static final long serialVersionUID = -8323359252544779001L;

    /**
     * Filters the data to include only values greater than this value.
     */
    @Nullable
    private final N greaterThan;

    /**
     * Filters the data to include only values greater than or equal to this
     * value.
     */
    @Nullable
    private final N greaterThanOrEqual;

    /**
     * Filters the data to include only values less than this value.
     */
    @Nullable
    private final N lessThan;

    /**
     * Filters the data to include only values less than or equal to this value.
     */
    @Nullable
    private final N lessThanOrEqual;

    /**
     * This is a constructor for the NumberFilter class.
     *
     * @param equal              Filters the data to include only values that
     *                           are equal to this value.
     * @param in                 Filters the data to include only values that
     *                           are present in this list.
     * @param notEqual           Filters the data to exclude values that are
     *                           equal to this value.
     * @param notIn              Filters the data to exclude values that are
     *                           present in this list.
     * @param isNull             Filters the data to include only values that
     *                           are null (or not null if {@code false}).
     * @param greaterThan        Filters the data to include only values greater
     *                           than this value.
     * @param greaterThanOrEqual Filters the data to include only values greater
     *                           than or equal to this value.
     * @param lessThan           Filters the data to include only values less
     *                           than this value.
     * @param lessThanOrEqual    Filters the data to include only values less
     *                           than or equal to this value.
     */
    public NumberFilterImpl(
        @Nullable final N equal, @Nullable final List<N> in,
        @Nullable final N notEqual, @Nullable final List<N> notIn,
        @Nullable final Boolean isNull, @Nullable final N greaterThan,
        @Nullable final N greaterThanOrEqual, @Nullable final N lessThan,
        @Nullable final N lessThanOrEqual) {

        super(equal, in, notEqual, notIn, isNull);
        this.greaterThan = greaterThan;
        this.greaterThanOrEqual = greaterThanOrEqual;
        this.lessThan = lessThan;
        this.lessThanOrEqual = lessThanOrEqual;
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("greaterThan", greaterThan));
        list.add(Pair.of("greaterThanOrEqual", greaterThanOrEqual));
        list.add(Pair.of("lessThan", lessThan));
        list.add(Pair.of("lessThanOrEqual", lessThanOrEqual));
        return list;
    }
}
