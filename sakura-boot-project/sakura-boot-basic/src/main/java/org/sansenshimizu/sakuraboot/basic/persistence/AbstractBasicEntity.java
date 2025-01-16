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

package org.sansenshimizu.sakuraboot.basic.persistence;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.MappedSuperclass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.proxy.HibernateProxy;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * The abstract base class for all entities.
 * It is possible to have relationships between entities.
 * Unidirectional relation is recommended.
 * Most of the time, bidirectional relations aren't necessary, and a
 * unidirectional relationship is enough.
 * Also, OneToMany relation is not recommended, and it can be made by a
 * ManyToOne unidirectional relation if used with the specification module.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create and use a concrete entity class that inherits from
 * {@link AbstractBasicEntity}, follow these steps:
 * </p>
 * <p>
 * Extend the {@link AbstractBasicEntity} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Entity
 * &#064;Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 * public class YourEntity extends AbstractBasicEntity&lt;YourIdType&gt; {
 *
 *     &#064;Id
 *     &#064;GeneratedValue(strategy = GenerationType.AUTO)
 *     &#064;Column(nullable = false)
 *     private Long id;
 *
 *     // For a One to One relationship
 *     &#064;OneToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourRelationalEntity relationship;
 *
 *     // For a Many to One relationship
 *     &#064;ManyToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourRelationalEntity relationship;
 *
 *     // For a Many to Many relationship
 *     &#064;ManyToMany(cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     &#064;JoinTable(
 *         name = "join_table",
 *         joinColumns = @JoinColumn(name = "parent_id"),
 *         inverseJoinColumns = @JoinColumn(name = "relational_id"))
 *     &#064;Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 *     private Set&lt;YourRelationalEntity&gt; relationships;
 *
 *     // Getter for a Many to Many relationship
 *     public Set&lt;YourRelationalEntity&gt; getRelationships() {
 *
 *         if (relationships == null) {
 *
 *             return null;
 *         }
 *         return Collections.unmodifiableSet(relationships);
 *     }
 *
 *     YourEntity() {}
 *     // Need an empty package-private constructor for JPA.
 *     // Add your fields and getter method here ...
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
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicEntity<
    I extends Comparable<? super I> & Serializable>
    implements DataPresentation<I> {

    @Serial
    private static final long serialVersionUID = 6521211660620834199L;

    /**
     * Compares this object with the specified object for equality. Two
     * objects are considered equal if they have the same class and their
     * 'id' field values are equal.
     *
     * @param  obj The object to compare with this object.
     * @return     true if the given object is equal to this object, false
     *             otherwise.
     */
    @Override
    public final boolean equals(final Object obj) {

        if (obj == null) {

            return false;
        }
        final Class<?> thisEffectiveClass;

        if (this instanceof final HibernateProxy hibernateProxy) {

            thisEffectiveClass = hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass();
        } else {

            thisEffectiveClass = getClass();
        }
        final Class<?> oEffectiveClass;

        if (obj instanceof final HibernateProxy hibernateProxy) {

            oEffectiveClass = hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass();
        } else {

            oEffectiveClass = obj.getClass();
        }

        if (thisEffectiveClass != oEffectiveClass) {

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

        if (this instanceof final HibernateProxy hibernateProxy) {

            return Objects.hash(getId(),
                hibernateProxy.getHibernateLazyInitializer()
                    .getPersistentClass()
                    .hashCode(),
                notNull);
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
        final Class<?> thisEffectiveClass;

        if (this instanceof final HibernateProxy hibernateProxy) {

            thisEffectiveClass = hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass();
        } else {

            thisEffectiveClass = getClass();
        }
        final Class<?> oEffectiveClass;

        if (o instanceof final HibernateProxy hibernateProxy) {

            oEffectiveClass = hibernateProxy.getHibernateLazyInitializer()
                .getPersistentClass();
        } else {

            oEffectiveClass = o.getClass();
        }

        if (oEffectiveClass != thisEffectiveClass) {

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
