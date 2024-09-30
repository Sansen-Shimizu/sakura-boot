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

package org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.two;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.mapper.api.dto.relationship.one.BasicDto1RelationshipAnyToOne;
import org.sansenshimizu.sakuraboot.relationship.two.DataPresentation2RelationshipAnyToOneAndAnyToMany;

/**
 * The basic interface for all DTO with relationship to two others entities of
 * type One To One or Many To One and One To Many or Many to Many.
 *
 * @param  <I>  The ID of type Comparable and Serializable.
 * @param  <R>  The relational entity of type {@link DataPresentation}.
 * @param  <I2> The ID for the relational entity of type Comparable and
 *              Serializable.
 * @param  <R2> The second relational entity of type {@link DataPresentation}.
 * @param  <I3> The ID for the second relational entity of type Comparable and
 *              Serializable.
 * @author      Malcolm Rozé
 * @see         DataPresentation2RelationshipAnyToOneAndAnyToMany
 * @see         BasicDto1RelationshipAnyToOne
 * @see         BasicDto1RelationshipAnyToMany
 * @since       0.1.0
 */
public interface BasicDto2RelationshipAnyToOneAndAnyToMany<
    I extends Comparable<? super I> & Serializable,
    R extends DataPresentation<I2>,
    I2 extends Comparable<? super I2> & Serializable,
    R2 extends DataPresentation<I3>,
    I3 extends Comparable<? super I3> & Serializable>
    extends DataPresentation2RelationshipAnyToOneAndAnyToMany<I, R, R2>,
    BasicDto1RelationshipAnyToOne<I, R, I2>,
    BasicDto1RelationshipAnyToMany<I, R2, I3> {}
