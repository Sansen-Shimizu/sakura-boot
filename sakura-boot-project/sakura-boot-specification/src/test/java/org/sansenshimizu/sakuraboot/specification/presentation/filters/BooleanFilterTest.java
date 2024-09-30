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

import java.util.List;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.BooleanFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;

/**
 * The test class for the boolean filter that extends
 * {@link AbstractCommonFilterTest}.
 *
 * @author Malcolm Rozé
 * @see    BooleanFilterImpl
 * @see    AbstractCommonFilterTest
 * @since  0.1.0
 */
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
class BooleanFilterTest extends AbstractCommonFilterTest<Boolean> {

    @Override
    public Boolean getEqualValue() {

        return true;
    }

    @Override
    public List<Boolean> getInValue() {

        return List.of(true);
    }

    @Override
    public Boolean getNotEqualValue() {

        return true;
    }

    @Override
    public List<Boolean> getNotInValue() {

        return List.of(true);
    }

    @Override
    public boolean isNullValue() {

        return true;
    }

    @Override
    public BooleanFilter getBean() {

        final CommonFilter<Boolean> commonFilter = super.getBean();
        return new BooleanFilterImpl(commonFilter.getEqual(),
            commonFilter.getIn(), commonFilter.getNotEqual(),
            commonFilter.getNotIn(), commonFilter.getIsNull(), true);
    }

    @Override
    public BooleanFilter getSecondBean() {

        return getBean();
    }
}
