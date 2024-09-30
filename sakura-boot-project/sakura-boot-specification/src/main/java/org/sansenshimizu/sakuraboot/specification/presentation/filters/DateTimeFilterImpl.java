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
import java.time.LocalDateTime;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.DateFilter;

/**
 * A filter for date time values.
 *
 * @author Malcolm Rozé
 * @see    CommonFilterImpl
 * @see    DateFilter
 * @since  0.1.0
 */
@SuperBuilder(toBuilder = true, builderMethodName = "dateTimeFilterBuilder")
@Getter
@EqualsAndHashCode(callSuper = true)
public class DateTimeFilterImpl extends CommonFilterImpl<LocalDateTime>
    implements DateFilter<LocalDateTime> {

    @Serial
    private static final long serialVersionUID = -8639547516801221956L;

    /**
     * Filters the data to include only values from this value.
     */
    @Nullable
    private final LocalDateTime from;

    /**
     * Filters the data to include only values to this value.
     */
    @Nullable
    private final LocalDateTime to;

    /**
     * This is a constructor for the DateFilter class.
     *
     * @param equal    Filters the data to include only values that
     *                 are equal to this value.
     * @param in       Filters the data to include only values that
     *                 are present in this list.
     * @param notEqual Filters the data to exclude values that are
     *                 equal to this value.
     * @param notIn    Filters the data to exclude values that are
     *                 present in this list.
     * @param isNull   Filters the data to include only values that
     *                 are null (or not null if {@code false}).
     * @param from     Filters the data to include only values before
     *                 this value.
     * @param to       Filters the data to include only values after
     *                 this value.
     */
    public DateTimeFilterImpl(
        @Nullable final LocalDateTime equal,
        @Nullable final List<LocalDateTime> in,
        @Nullable final LocalDateTime notEqual,
        @Nullable final List<LocalDateTime> notIn,
        @Nullable final Boolean isNull, @Nullable final LocalDateTime from,
        @Nullable final LocalDateTime to) {

        super(equal, in, notEqual, notIn, isNull);
        this.from = from;
        this.to = to;
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("from", from));
        list.add(Pair.of("to", to));
        return list;
    }
}
