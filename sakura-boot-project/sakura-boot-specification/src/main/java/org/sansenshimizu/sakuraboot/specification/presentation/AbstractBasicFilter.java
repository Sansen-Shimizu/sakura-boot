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

package org.sansenshimizu.sakuraboot.specification.presentation;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;

import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.Filter;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * A base class representing a generic filter for queries parameters. Can
 * contain multiple {@link Filter} class for filtering different types.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a filter that inherits from {@link AbstractBasicFilter}, follow
 * these steps:
 * </p>
 * <p>
 * Create a new filter class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFilter
 *     extends AbstractBasicFilter&lt;CommonFilter&lt;YourIdType&gt;&gt; {
 *
 *     private final Boolean distinct;
 *
 *     private final Boolean inclusive;
 *
 *     private final CommonFilterImpl&lt;YourIdType&gt; id;
 *
 *     private final BooleanFilterImpl yourField;
 *
 *     public YourFilter(
 *         final Boolean distinct, final Boolean inclusive,
 *         final CommonFilterImpl&lt;YourIdType&gt; id,//
 *         final BooleanFilterImpl yourField) {
 *
 *         this.distinct = distinct;
 *         this.inclusive = inclusive;
 *         this.id = id;
 *         this.yourField = yourField;
 *     }
 *
 *     &#064;Override
 *     protected List&lt;Pair&lt;String, Object&gt;&gt; listFieldsForToString(
 *         final List&lt;Pair&lt;String, Object&gt;&gt; list) {
 *
 *         super.listFieldsForToString(list);
 *         list.add(Pair.of("yourField", getYourField()));
 *         return list;
 *     }
 *     // getter, builder, equal and hashCode...
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <F> The {@link Filter} type for the ID filter.
 * @author     Malcolm Rozé
 * @see        FilterPresentation
 * @see        Filter
 * @since      0.1.0
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicFilter<F extends Filter>
    implements FilterPresentation<F> {

    @Serial
    private static final long serialVersionUID = 5874411780135235644L;

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {

            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {

            return false;
        }
        final FilterPresentation<?> castObject = (FilterPresentation<?>) obj;
        return Objects.equals(getDistinct(), castObject.getDistinct())
            && Objects.equals(getInclusive(), castObject.getInclusive())
            && Objects.equals(getId(), castObject.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getDistinct(), getInclusive(), getId(),
            getClass().hashCode());
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

        return ToStringUtils.getListFieldsForToString(this, list,
            excludedFieldsForToString());
    }

    /**
     * The list of fields that need to be excluded in the {@link #toString()}
     * method.
     *
     * @return The list of fields to exclude.
     */
    protected List<String> excludedFieldsForToString() {

        return List.of();
    }

    @Override
    public String toString() {

        return ToStringUtils.toString(getClass().getSimpleName(),
            listFieldsForToString(new ArrayList<>()));
    }
}
