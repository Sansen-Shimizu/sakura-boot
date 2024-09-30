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

package org.sansenshimizu.sakuraboot.mapper.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * The abstract base class for all Data Transfer Objects (DTOs). This class
 * provides common fields and methods that DTOs can inherit from.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create and use a concrete DTO class that inherits from
 * {@link AbstractBasicDto}, follow these steps:
 * </p>
 * <p>
 * Extend the {@link AbstractBasicDto} class using {@link FullData}:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourDto extends AbstractBasicDto&lt;YourIdType&gt; {
 *
 *     // Add your fields using {&#064;link FullData} if necessary
 *     private YourIdType id;
 *
 *     &#064;NotNull(groups = DataPresentation.FullData.class)
 *     private final Object yourField;
 *
 *     &#064;JsonCreator
 *     public YourDto(
 *         &#064;JsonProperty("id") final YourIdType id,
 *         &#064;JsonProperty("yourField") final Object yourField) {
 *
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
 *     // Getter ...
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        DataPresentation
 * @since      0.1.0
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicDto<
    I extends Comparable<? super I> & Serializable>
    implements DataPresentation<I> {

    @Serial
    private static final long serialVersionUID = 6275514824666474574L;

    /**
     * Compares this object with the specified object for equality.
     * Two objects are considered equal if they have the same class and their
     * 'id' field values are equal.
     *
     * @param  obj The object to compare with this object.
     * @return     true if the given object is equal to this
     *             object, false otherwise.
     */
    @Override
    public final boolean equals(final Object obj) {

        if (obj == null) {

            return false;
        }

        if (getClass() != obj.getClass()) {

            return false;
        }
        final I id = getId();
        return this == obj
            || (id != null && id.equals(((DataPresentation<?>) obj).getId()));
    }

    /**
     * Returns a hash code value for this object based on it's 'id' field value.
     *
     * @return A hash code value for this object.
     */
    @Override
    public final int hashCode() {

        int notNull = 0;
        final I id = getId();

        if (id != null) {

            notNull = 1;
        }
        return Objects.hash(getId(), getClass().hashCode(), notNull);
    }

    /**
     * Compares this object with another {@link DataPresentation} object for
     * order.
     * The default implementation compares the 'id' field values of the
     * entities.
     *
     * @param  o The {@link DataPresentation} object to be compared.
     * @return   A negative integer, zero, or a positive integer as this object
     *           is less than, equal to, or greater than the specified object.
     */
    @Override
    public final int compareTo(final DataPresentation<I> o) {

        Objects.requireNonNull(o,
            "The object to be compared to mustn't be null.");

        if (o.getClass() != getClass()) {

            throw new ClassCastException(
                "The object to be compared to must be of the same type.");
        }
        Objects.requireNonNull(o.getId(),
            "The object to be compared must have an ID.");
        final I id = getId();

        if (id == null) {

            return -1;
        }
        return id.compareTo(o.getId());
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

        list.add(Pair.of("id", getId()));
        return list;
    }

    @Override
    public String toString() {

        return ToStringUtils.toStringPrintNullFields(getClass().getSimpleName(),
            listFieldsForToString(new ArrayList<>()));
    }
}
