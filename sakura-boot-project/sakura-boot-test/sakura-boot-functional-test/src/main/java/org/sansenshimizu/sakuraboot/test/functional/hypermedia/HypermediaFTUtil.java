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

package org.sansenshimizu.sakuraboot.test.functional.hypermedia;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.functional.BasicFTUtil;

/**
 * The interface for all the utility integration test function. This interface
 * provides common functions for testing hypermedia class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link HypermediaFTUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link HypermediaFTUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourFTUtil //
 *     implements HypermediaFTUtil&lt;YourEntity, YourIdType&gt; {
 *
 *     private final GlobalSpecification globalSpecification;
 *
 *     &#064;Autowired
 *     public YourFTUtil(
 *         final GlobalSpecification globalSpecification) {
 *
 *         this.globalSpecification = globalSpecification;
 *     }
 *
 *     &#064;Override
 *     public GlobalSpecification getGlobalSpecification() {
 *
 *         return globalSpecification;
 *     }
 *
 *     &#064;Override
 *     public Optional&lt;YourEntity&gt; createValidation
 *     ErrorEntity(YourIdType id) {
 *
 *         return YourEntity.builder().id(id).build();
 *         // If your class don't have a builder you can use the constructor
 *     }
 *
 *     &#064;Override
 *     public String getPath() {
 *
 *         return "api/pathName";
 *     }
 *
 *     &#064;Override
 *     public String entityCollectionName() {
 *
 *         return "entitiesName";
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicFTUtil
 * @since      0.1.0
 */
public interface HypermediaFTUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicFTUtil<E, I> {

    /**
     * Return the additional links in the model.
     *
     * @param  body The expected body of the response.
     * @param  path The path use by the controller.
     * @return      the other links.
     */
    default String addOtherLink(final Object body, final String path) {

        return "";
    }

    /**
     * Get the collection name used by the model.
     *
     * @return The collection name.
     */
    String entityCollectionName();

    /**
     * Get the name of the relationship, if any.
     *
     * @return The relationship name.
     */
    default String relationshipName() {

        return "";
    }

    /**
     * Get the name of the second relationship, if any.
     *
     * @return The second relationship name.
     */
    default String secondRelationshipName() {

        return "";
    }
}
