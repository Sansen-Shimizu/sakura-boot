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

package org.sansenshimizu.sakuraboot.test.mapper.dto;

import java.io.Serializable;

import org.sansenshimizu.sakuraboot.mapper.dto.AbstractBasicDto;
import org.sansenshimizu.sakuraboot.test.BasicTest;
import org.sansenshimizu.sakuraboot.test.DataPresentationTest;
import org.sansenshimizu.sakuraboot.test.mapper.MapperTest;

/**
 * The base test class for all AbstractBasicDto. This class provides common
 * tests for testing {@link AbstractBasicDto}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete AbstractBasicDto test class that inherits from
 * {@link AbstractBasicDtoTest}, follow these steps:
 * </p>
 * <p>
 * Extends the {@link AbstractBasicDtoTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourDtoTest //
 *     extends AbstractBasicDtoTest&lt;YourDto, YourIdType&gt; {
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
 * @param  <D> The {@link AbstractBasicDtoTest} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        AbstractBasicDtoTest
 * @see        DataPresentationTest
 * @see        BasicTest
 * @since      0.1.0
 */
public abstract class AbstractBasicDtoTest<D extends AbstractBasicDto<I>,
    I extends Comparable<? super I> & Serializable>
    implements DataPresentationTest<D, I>, MapperTest<D, I> {

    @Override
    public D getBean() {

        return getUtil().getData();
    }

    @Override
    public D getSecondBean() {

        return getUtil().getSecondData();
    }

    @Override
    public boolean toStringIncludeNullValue() {

        return false;
    }
}
