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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * A basic filter for filtering all comparable type.
 *
 * @param  <T> The value type.
 * @author     Malcolm Rozé
 * @see        CommonFilter
 * @since      0.1.0
 */
@SuperBuilder(toBuilder = true, builderMethodName = "commonFilterBuilder")
@Getter
@EqualsAndHashCode
public class CommonFilterImpl<T extends Serializable>
    implements CommonFilter<T> {

    @Serial
    private static final long serialVersionUID = 8948861020276896468L;

    /**
     * Filters the data to include only values that are equal to this value.
     */
    @Nullable
    private T equal;

    /**
     * Filters the data to include only values that are present in this list.
     */
    @Singular("oneIn")
    private final List<T> in;

    /**
     * Filters the data to exclude values that are equal to this value.
     */
    @Nullable
    private final T notEqual;

    /**
     * Filters the data to exclude values that are present in this list.
     */
    @Singular("oneNotIn")
    private final List<T> notIn;

    /**
     * Filters the data to include only values that are null (or not null if
     * {@code false}).
     */
    @Nullable
    private final Boolean isNull;

    /**
     * This is a constructor for the CommonFilter class.
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
     */
    public CommonFilterImpl(
        @Nullable final T equal, @Nullable final List<T> in,
        @Nullable final T notEqual, @Nullable final List<T> notIn,
        @Nullable final Boolean isNull) {

        this.equal = equal;

        if (in == null) {

            this.in = Collections.emptyList();
        } else {

            this.in = Collections.unmodifiableList(in);
        }
        this.notEqual = notEqual;

        if (notIn == null) {

            this.notIn = Collections.emptyList();
        } else {

            this.notIn = Collections.unmodifiableList(notIn);
        }
        this.isNull = isNull;
    }

    /**
     * The list of fields that need to be present in the {@link #toString()}
     * method.
     *
     * @param  list The list of fields to print.
     * @return      The list of fields to print.
     */
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        list.add(Pair.of("equal", equal));
        list.add(Pair.of("in", in));
        list.add(Pair.of("notEqual", notEqual));
        list.add(Pair.of("notIn", notIn));
        list.add(Pair.of("isNull", isNull));
        return list;
    }

    @Override
    public String toString() {

        return ToStringUtils.toString(getClass().getSimpleName(),
            listFieldsForToString(new ArrayList<>()));
    }
}
