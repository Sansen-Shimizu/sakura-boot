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

package org.sansenshimizu.sakuraboot.test;

import java.io.Serializable;

import org.springframework.test.context.ActiveProfiles;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * The super interface for all integration tests. This interface has no test but
 * can be used to simplify the access to the {@link SuperITUtil} interface.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete integration test class that inherits from
 * {@link SuperIT}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperIT} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourIT implements SuperIT&lt;YourEntity, YourIdType&gt; {
 *
 *     private final YourUtil util = new YourUtil();
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperITUtil
 * @see        SuperTest
 * @since      0.1.0
 */
@ActiveProfiles("test")
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SuperIT<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperTest<I> {

    /**
     * Return a Util class of type {@link SuperITUtil}.
     *
     * @return A Util class for testing.
     */
    @Override
    SuperITUtil<E, I> getUtil();
}
