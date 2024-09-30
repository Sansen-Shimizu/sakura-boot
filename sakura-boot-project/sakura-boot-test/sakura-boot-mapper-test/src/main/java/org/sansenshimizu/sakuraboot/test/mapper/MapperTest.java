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

package org.sansenshimizu.sakuraboot.test.mapper;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.BasicDataTestUtil;
import org.sansenshimizu.sakuraboot.test.SuperTest;

/**
 * The super interface for all basic unit tests. This interface has no test but
 * can be used to simplify the access to the {@link BasicDataTestUtil}
 * interface.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link MapperTest}, follow
 * these steps:
 * </p>
 * <p>
 * Implements the {@link MapperTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourTest implements MapperTest&lt;YourDto, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
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
 * @param  <D> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperTest
 * @see        BasicDataTestUtil
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface MapperTest<D extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperTest<I> {

    /**
     * Return a util class of type {@link BasicDataTestUtil}.
     *
     * @return A util class for testing.
     */
    @Override
    BasicDataTestUtil<?, I, D> getUtil();
}
