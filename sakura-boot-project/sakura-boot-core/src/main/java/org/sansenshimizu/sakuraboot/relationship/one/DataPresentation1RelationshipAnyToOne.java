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

package org.sansenshimizu.sakuraboot.relationship.one;

import java.io.Serializable;

import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * The basic interface for every data exposed to the presentation layer with
 * relationship to another entity of type One To One or Many To One.
 *
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <R> The relational entity of type {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        DataPresentation
 * @since      0.1.0
 */
public interface DataPresentation1RelationshipAnyToOne<
    I extends Comparable<? super I> & Serializable,
    R extends DataPresentation<?>> extends DataPresentation<I> {

    /**
     * The getter for the relational entity.
     *
     * @return The relationship.
     */
    @Nullable
    R getRelationship();
}
