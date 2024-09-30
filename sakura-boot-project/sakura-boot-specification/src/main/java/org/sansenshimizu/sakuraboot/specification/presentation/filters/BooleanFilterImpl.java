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

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.BooleanFilter;

/**
 * A filter for boolean values.
 *
 * @author Malcolm Rozé
 * @see    CommonFilterImpl
 * @see    BooleanFilter
 * @since  0.1.0
 */
@SuperBuilder(toBuilder = true, builderMethodName = "booleanFilterBuilder")
@Getter
@EqualsAndHashCode(callSuper = true)
public class BooleanFilterImpl extends CommonFilterImpl<Boolean>
    implements BooleanFilter {

    @Serial
    private static final long serialVersionUID = -5677706393411482440L;

    /**
     * Filters the data to include only {@code true} values.
     */
    @Nullable
    private final Boolean isTrue;

    /**
     * This is a constructor for the BooleanFilter class.
     *
     * @param equal    Filters the data to include only values that are equal to
     *                 this value.
     * @param in       Filters the data to include only values that are present
     *                 in this list.
     * @param notEqual Filters the data to exclude values that are equal to this
     *                 value.
     * @param notIn    Filters the data to exclude values that are present in
     *                 this list.
     * @param isNull   Filters the data to include only values that are null (or
     *                 not null if {@code false}).
     * @param isTrue   Filters the data to include only {@code true} values.
     */
    public BooleanFilterImpl(
        @Nullable final Boolean equal, @Nullable final List<Boolean> in,
        @Nullable final Boolean notEqual, @Nullable final List<Boolean> notIn,
        @Nullable final Boolean isNull, @Nullable final Boolean isTrue) {

        super(equal, in, notEqual, notIn, isNull);
        this.isTrue = isTrue;
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("isTrue", isTrue));
        return list;
    }
}
