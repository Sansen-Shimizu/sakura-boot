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

package org.sansenshimizu.sakuraboot.mapper.dto.relationship.one;

import java.io.Serial;
import java.io.Serializable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.dto.AbstractBasicDto;

/**
 * The abstract base class for all DTO with a relational entity of type One to
 * Many or Many to Many.
 * The relation can be made by the ID of the entity or the entity itself. The
 * entity can be represented by a DTO or the actual entity.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create and use a concrete DTO class that inherits from
 * {@link AbstractBasicDto1RelationshipAnyToMany}, follow these steps:
 * </p>
 * <p>
 * Extend the {@link AbstractBasicDto1RelationshipAnyToMany} class using
 * {@link FullData}:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourDto
 *     extends AbstractBasicDto1RelationshipAnyToMany&lt;YourIdType,
 *         YourRelationEntity, YourRelationEntityIdType&gt; {
 *
 *     // Add your fields using {&#064;link FullData} if necessary
 *     private YourIdType id;
 *
 *     &#064;JsonInclude(Include.NON_EMPTY)
 *     private final Set&lt;YourRelationEntity&gt; relationships;
 *
 *     private final Set&lt;YourRelationEntityIdType&gt; relationshipsId;
 *
 *     &#064;NotNull(groups = DataPresentation.FullData.class)
 *     private final Object yourField;
 *
 *     &#064;JsonCreator
 *     public YourDto(
 *         &#064;JsonProperty("id") final YourIdType id,
 *         &#064;JsonProperty("yourField") final Object yourField,
 *         &#064;JsonProperty("relationships") final Set&lt;
 *             YourRelationEntity&gt; relationships,
 *         &#064;JsonProperty("relationshipsId") final Set&lt;
 *             YourRelationEntityIdType&gt; relationshipsId) {
 *
 *         this.id = id;
 *
 *         if (relationships == null) {
 *
 *             this.relationships = null;
 *         } else {
 *
 *             this.relationships = Collections.unmodifiableSet(relationships);
 *         }
 *
 *         if (relationshipsId == null) {
 *
 *             this.relationshipsId = null;
 *         } else {
 *
 *             this.relationshipsId
 *                 = Collections.unmodifiableSet(relationshipsId);
 *         }
 *         this.yourField = yourField;
 *     }
 *
 *     &#064;Override
 *     protected List&lt;Pair&lt;String, Object&gt;&gt; listFieldsForToString(
 *         final List&lt;Pair&lt;String, Object&gt;&gt; list) {
 *
 *         super.listFieldsForToString(list);
 *         list.add(Pair.of("relationships", getRelationships()));
 *         list.add(Pair.of("yourField", getYourField()));
 *         return list;
 *     }
 *     // Getter ...
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <I>  The ID of type Comparable and Serializable.
 * @param  <R>  The relational entity of type {@link DataPresentation}.
 * @param  <I2> The ID for the relational entity of type Comparable and
 *              Serializable.
 * @author      Malcolm Rozé
 * @see         AbstractBasicDto
 * @see         BasicDto1RelationshipAnyToMany
 * @since       0.1.0
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBasicDto1RelationshipAnyToMany<
    I extends Comparable<? super I> & Serializable,
    R extends DataPresentation<I2>,
    I2 extends Comparable<? super I2> & Serializable>
    extends AbstractBasicDto<I>
    implements BasicDto1RelationshipAnyToMany<I, R, I2> {

    @Serial
    private static final long serialVersionUID = -3504050964539183699L;
}
