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

package org.sansenshimizu.sakuraboot.basic.persistence.relationship.two;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.sansenshimizu.sakuraboot.basic.persistence.AbstractBasicEntity;
import org.sansenshimizu.sakuraboot.basic.persistence.relationship.one.AbstractBasicEntity1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOne;

/**
 * The abstract base class for all entities with two relational entities of
 * types any to One.
 * This class can be used to make an entity relational to two other
 * {@link AbstractBasicEntity} of type ManyToOne.
 * It will be a unidirectional relation.
 * If a bidirectional relation is necessary, it must be added in the
 * {@link AbstractBasicEntity} entity.
 * Most of the time, bidirectional relations aren't necessary, and a
 * unidirectional relationship is enough.
 * Also, OneToMany relation can be made by a ManyToOne unidirectional relation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create and use a concrete entity class that inherits from
 * {@link AbstractBasicEntity2RelationshipAnyToOne}, follow these steps:
 * </p>
 * <p>
 * Extend the {@link AbstractBasicEntity2RelationshipAnyToOne} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Entity
 * &#064;Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 * public class YourEntity
 *     extends AbstractBasicEntity2RelationshipAnyToOne&lt;YourIdType,
 *         YourRelationalEntity, YourSecondRelationalEntity&gt; {
 *
 *     // For two Many to One relationship
 *     &#064;Id
 *     &#064;GeneratedValue(strategy = GenerationType.IDENTITY)
 *     &#064;Column(nullable = false)
 *     private Long id;
 *
 *     &#064;ManyToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourRelationalEntity relationship;
 *
 *     &#064;ManyToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourSecondRelationalEntity secondRelationship;
 *
 *     YourEntity() {}
 *     // Need an empty package-private constructor for JPA.
 *     // For two Many to One relationship
 *
 *     // For two One to One relationship
 *     &#064;Id
 *     &#064;GeneratedValue(strategy = GenerationType.IDENTITY)
 *     &#064;Column(nullable = false)
 *     private Long id;
 *
 *     &#064;OneToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourRelationalEntity relationship;
 *
 *     &#064;OneToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourSecondRelationalEntity secondRelationship;
 *
 *     YourEntity() {}
 *     // Need an empty package-private constructor for JPA.
 *     // For two One to One relationship
 *
 *     // For one One to One and one Many to One relationship
 *     &#064;Id
 *     &#064;GeneratedValue(strategy = GenerationType.IDENTITY)
 *     &#064;Column(nullable = false)
 *     private Long id;
 *
 *     &#064;OneToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourRelationalEntity relationship;
 *
 *     &#064;ManyToOne(fetch = FetchType.LAZY, cascade = {
 *         CascadeType.PERSIST, CascadeType.MERGE
 *     })
 *     private YourSecondRelationalEntity secondRelationship;
 *
 *     protected YourEntity() {}
 *     // Need an empty protected constructor for JPA.
 *     // For one One to One and one Many to One relationship
 *
 *     &#064;Override
 *     protected List&lt;Pair&lt;String, Object&gt;&gt; listFieldsForToString(
 *         final List&lt;Pair&lt;String, Object&gt;&gt; list) {
 *
 *         super.listFieldsForToString(list);
 *         list.add(Pair.of("relationship", getRelationship()));
 *         list.add(Pair.of("secondRelationship", getSecondRelationship()));
 *         list.add(Pair.of("yourField", getYourField()));
 *         return list;
 *     }
 *     // Add your fields and getter method here ...
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <I>  The ID of type Comparable and Serializable.
 * @param  <R>  The relational entity of type {@link AbstractBasicEntity}.
 * @param  <R2> The relational entity of type {@link AbstractBasicEntity}.
 * @author      Malcolm Rozé
 * @see         AbstractBasicEntity1RelationshipAnyToOne
 * @see         DataPresentation2RelationshipAnyToOne
 * @since       0.1.0
 */
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicEntity2RelationshipAnyToOne<
    I extends Comparable<? super I> & Serializable,
    R extends AbstractBasicEntity<?>, R2 extends AbstractBasicEntity<?>>
    extends AbstractBasicEntity1RelationshipAnyToOne<I, R>
    implements DataPresentation2RelationshipAnyToOne<I, R, R2> {

    @Serial
    private static final long serialVersionUID = 71358362999993229L;
}
