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

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;

/**
 * The test class for the number filter that extends
 * {@link AbstractCommonFilterTest}.
 *
 * @author Malcolm Rozé
 * @see    NumberFilterImpl
 * @see    AbstractCommonFilterTest
 * @since  0.1.0
 */
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
class NumberFilterTest extends AbstractCommonFilterTest<Double> {

    @Override
    public Double getEqualValue() {

        return 0.0;
    }

    @Override
    public List<Double> getInValue() {

        return List.of(0.0);
    }

    @Override
    public Double getNotEqualValue() {

        return 0.0;
    }

    @Override
    public List<Double> getNotInValue() {

        return List.of(0.0);
    }

    @Override
    public boolean isNullValue() {

        return true;
    }

    @Override
    public NumberFilter<Double> getBean() {

        final CommonFilter<Double> commonFilter = super.getBean();
        return new NumberFilterImpl<>(commonFilter.getEqual(),
            commonFilter.getIn(), commonFilter.getNotEqual(),
            commonFilter.getNotIn(), commonFilter.getIsNull(), 0.0, 0.0, 0.0,
            0.0);
    }

    @Override
    public NumberFilter<Double> getSecondBean() {

        return getBean();
    }
}
