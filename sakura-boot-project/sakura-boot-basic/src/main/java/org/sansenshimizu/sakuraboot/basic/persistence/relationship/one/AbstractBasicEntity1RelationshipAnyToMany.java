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

package org.sansenshimizu.sakuraboot.basic.persistence.relationship.one;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.sansenshimizu.sakuraboot.basic.persistence.AbstractBasicEntity;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;

/**
 * The abstract base class for all entities with a relational entity of type any
 * to Many.
 * This class can be used to make an entity relational to another
 * {@link AbstractBasicEntity} of type ManyToMany.
 * It will be a unidirectional relation.
 * If a bidirectional relation is necessary, it must be added in the
 * {@link AbstractBasicEntity} entity.
 * Most of the time, bidirectional relations aren't necessary, and a
 * unidirectional
 * relationship is enough.
 * Also, OneToMany relation can be made by a ManyToOne unidirectional relation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create and use a concrete entity class that inherits from
 * {@link AbstractBasicEntity1RelationshipAnyToMany}, follow these steps:
 * </p>
 * <p>
 * Extend the {@link AbstractBasicEntity1RelationshipAnyToMany} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Entity
 * &#064;Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 * public class YourEntity
 *     extends AbstractBasicEntity1RelationshipManyToMany&lt;YourIdType,
 *         YourRelationalEntity&gt; {
 *
 *     &#064;Id
 *     &#064;GeneratedValue(strategy = GenerationType.IDENTITY)
 *     &#064;Column(nullable = false)
 *     private Long id;
 *
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
 *     YourEntity() {}
 *
 *     // Need a package-private constructor for JPA.
 *     &#064;Override
 *     protected List&lt;Pair&lt;String, Object&gt;&gt; listFieldsForToString(
 *         final List&lt;Pair&lt;String, Object&gt;&gt; list) {
 *
 *         super.listFieldsForToString(list);
 *         list.add(Pair.of("relationships", getRelationships()));
 *         list.add(Pair.of("yourField", getYourField()));
 *         return list;
 *     }
 *     // Add your fields and getter method here ..
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <R> The relational entity of type {@link AbstractBasicEntity}.
 * @author     Malcolm Rozé
 * @see        AbstractBasicEntity
 * @see        DataPresentation1RelationshipAnyToMany
 * @since      0.1.0
 */
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicEntity1RelationshipAnyToMany<
    I extends Comparable<? super I> & Serializable,
    R extends AbstractBasicEntity<?>> extends AbstractBasicEntity<I>
    implements DataPresentation1RelationshipAnyToMany<I, R> {

    @Serial
    private static final long serialVersionUID = 3639415918927207184L;
}
