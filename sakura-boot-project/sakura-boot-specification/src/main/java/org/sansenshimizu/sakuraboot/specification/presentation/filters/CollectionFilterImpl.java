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

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CollectionFilter;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * A filter for collection-based values.
 *
 * @param  <T> The value type in the list.
 * @author     Malcolm Rozé
 * @see        CollectionFilter
 * @since      0.1.0
 */
@SuperBuilder(toBuilder = true, builderMethodName = "collectionFilterBuilder")
@Getter
@EqualsAndHashCode
public class CollectionFilterImpl<T extends Serializable>
    implements CollectionFilter<T> {

    @Serial
    private static final long serialVersionUID = 8308411804853623371L;

    /**
     * Filters the data to include only values that are present in this list.
     */
    @Singular("oneEqual")
    private final List<T> equal;

    /**
     * Filters the data to exclude values that are present in this list.
     */
    @Singular("oneNotEqual")
    private final List<T> notEqual;

    /**
     * Filters the data to include only values that are empty (or not empty if
     * {@code false}).
     */
    @Nullable
    private final Boolean isEmpty;

    /**
     * Filters the data to include only values that are members of this list.
     */
    @Nullable
    private final T isMember;

    /**
     * Filters the data to include only values that aren't members of this list.
     */
    @Nullable
    private final T isNotMember;

    /**
     * This is a constructor for the CollectionFilter class.
     *
     * @param equal       Filters the data to include only values that are
     *                    present in this list.
     * @param notEqual    Filters the data to exclude values that are present in
     *                    this list.
     * @param isEmpty     Filters the data to include only values that are empty
     *                    (or not empty if {@code false}).
     * @param isMember    Filters the data to include only values that are
     *                    members of this list.
     * @param isNotMember Filters the data to include only values that aren't
     *                    members of this list.
     */
    public CollectionFilterImpl(
        @Nullable final List<T> equal, @Nullable final List<T> notEqual,
        @Nullable final Boolean isEmpty, @Nullable final T isMember,
        @Nullable final T isNotMember) {

        if (equal == null) {

            this.equal = Collections.emptyList();
        } else {

            this.equal = Collections.unmodifiableList(equal);
        }

        if (notEqual == null) {

            this.notEqual = Collections.emptyList();
        } else {

            this.notEqual = Collections.unmodifiableList(notEqual);
        }
        this.isEmpty = isEmpty;
        this.isMember = isMember;
        this.isNotMember = isNotMember;
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
        list.add(Pair.of("notEqual", notEqual));
        list.add(Pair.of("isEmpty", isEmpty));
        list.add(Pair.of("isMember", isMember));
        list.add(Pair.of("isNotMember", isNotMember));
        return list;
    }

    @Override
    public String toString() {

        return ToStringUtils.toString(getClass().getSimpleName(),
            listFieldsForToString(new ArrayList<>()));
    }
}
