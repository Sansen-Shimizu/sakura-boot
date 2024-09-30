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
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;

/**
 * The abstract base class for all entities with a relational entity of type any
 * to One.
 * This class can be used to make an entity relational to another
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
 * {@link AbstractBasicEntity1RelationshipAnyToOne}, follow these steps:
 * </p>
 * <p>
 * Extend the {@link AbstractBasicEntity1RelationshipAnyToOne} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Entity
 * &#064;Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 * public class YourEntity
 *     extends AbstractBasicEntity1RelationshipAnyToOne&lt;YourIdType,
 *         YourRelationalEntity&gt; {
 *
 *     // For a Many to One relationship
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
 *     YourEntity() {}
 *     // Need an empty package-private constructor for JPA.
 *     // For a Many to One relationship
 *
 *     // For a One to One relationship
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
 *     YourEntity() {}
 *     // Need an empty package-private constructor for JPA.
 *     // For a One to One relationship
 *
 *     &#064;Override
 *     protected List&lt;Pair&lt;String, Object&gt;&gt; listFieldsForToString(
 *         final List&lt;Pair&lt;String, Object&gt;&gt; list) {
 *
 *         super.listFieldsForToString(list);
 *         list.add(Pair.of("relationship", getRelationship()));
 *         list.add(Pair.of("yourField", getYourField()));
 *         return list;
 *     }
 *     // Add your fields and getter method here ...
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <R> The relational entity of type {@link AbstractBasicEntity}.
 * @author     Malcolm Rozé
 * @see        AbstractBasicEntity
 * @see        DataPresentation1RelationshipAnyToOne
 * @since      0.1.0
 */
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicEntity1RelationshipAnyToOne<
    I extends Comparable<? super I> & Serializable,
    R extends AbstractBasicEntity<?>> extends AbstractBasicEntity<I>
    implements DataPresentation1RelationshipAnyToOne<I, R> {

    @Serial
    private static final long serialVersionUID = -7593892909961749468L;
}
