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

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.TextFilter;

/**
 * A filter for text {@code String} values.
 *
 * @author Malcolm Rozé
 * @see    CommonFilterImpl
 * @see    TextFilter
 * @since  0.1.0
 */
@SuperBuilder(toBuilder = true, builderMethodName = "textFilterBuilder")
@Getter
@EqualsAndHashCode(callSuper = true)
public class TextFilterImpl extends CommonFilterImpl<String>
    implements TextFilter {

    @Serial
    private static final long serialVersionUID = -8208985656891631679L;

    /**
     * Filters the data to include only values that contain this text.
     */
    @Nullable
    private final String contains;

    /**
     * Filters the data to exclude values that contain this text.
     */
    @Nullable
    private final String notContains;

    /**
     * Filters the data to include only values that start with this text.
     */
    @Nullable
    private final String startWith;

    /**
     * Filters the data to include only values that end with this text.
     */
    @Nullable
    private final String endWith;

    /**
     * Specifies whether text filtering is case-sensitive {@code true} or
     * case-insensitive {@code false}.
     */
    @Nullable
    private final Boolean caseSensitive;

    /**
     * This is a constructor for the TextFilter class.
     *
     * @param equal         Filters the data to include only values that are
     *                      equal to this value.
     * @param in            Filters the data to include only values that are
     *                      present in this list.
     * @param notEqual      Filters the data to exclude values that are equal to
     *                      this value.
     * @param notIn         Filters the data to exclude values that are present
     *                      in this list.
     * @param isNull        Filters the data to include only values that are
     *                      null (or not null if {@code false}).
     * @param contains      Filters the data to include only values that contain
     *                      this text.
     * @param notContains   Filters the data to exclude values that contain this
     *                      text.
     * @param startWith     Filters the data to include only values that start
     *                      with this text.
     * @param endWith       Filters the data to include only values that end
     *                      with this text.
     * @param caseSensitive Specifies whether text filtering is case-sensitive
     *                      {@code true} or case-insensitive {@code false}.
     */
    public TextFilterImpl(
        @Nullable final String equal, @Nullable final List<String> in,
        @Nullable final String notEqual, @Nullable final List<String> notIn,
        @Nullable final Boolean isNull, @Nullable final String contains,
        @Nullable final String notContains, @Nullable final String startWith,
        @Nullable final String endWith, @Nullable final Boolean caseSensitive) {

        super(equal, in, notEqual, notIn, isNull);
        this.contains = contains;
        this.notContains = notContains;
        this.startWith = startWith;
        this.endWith = endWith;
        this.caseSensitive = caseSensitive;
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("contains", contains));
        list.add(Pair.of("notContains", notContains));
        list.add(Pair.of("startWith", startWith));
        list.add(Pair.of("endWith", endWith));
        list.add(Pair.of("caseSensitive", caseSensitive));
        return list;
    }
}
