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

package org.sansenshimizu.sakuraboot.specification.presentation.filters;

import java.io.Serializable;
import java.util.List;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;

/**
 * The abstract test class for the common filter that implements
 * {@link FilterTest}.
 *
 * @param  <T> The value type.
 * @author     Malcolm Rozé
 * @see        CommonFilterImpl
 * @see        FilterTest
 * @since      0.1.0
 */
public abstract class AbstractCommonFilterTest<T extends Serializable>
    implements FilterTest {

    protected abstract T getEqualValue();

    protected abstract List<T> getInValue();

    protected abstract T getNotEqualValue();

    protected abstract List<T> getNotInValue();

    protected abstract boolean isNullValue();

    @Override
    public CommonFilter<T> getBean() {

        return new CommonFilterImpl<>(getEqualValue(), getInValue(),
            getNotEqualValue(), getNotInValue(), isNullValue());
    }

    @Override
    public CommonFilter<T> getSecondBean() {

        return new CommonFilterImpl<>(getEqualValue(), getInValue(),
            getNotEqualValue(), getNotInValue(), isNullValue());
    }
}
