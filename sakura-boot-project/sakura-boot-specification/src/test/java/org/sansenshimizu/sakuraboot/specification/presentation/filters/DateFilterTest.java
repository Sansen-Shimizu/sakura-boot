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

import java.time.LocalDate;
import java.util.List;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.DateFilter;

/**
 * The test class for the date filter that extends
 * {@link AbstractCommonFilterTest}.
 *
 * @author Malcolm Rozé
 * @see    DateFilterImpl
 * @see    AbstractCommonFilterTest
 * @since  0.1.0
 */
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
class DateFilterTest extends AbstractCommonFilterTest<LocalDate> {

    @Override
    public LocalDate getEqualValue() {

        return LocalDate.now();
    }

    @Override
    public List<LocalDate> getInValue() {

        return List.of(LocalDate.now());
    }

    @Override
    public LocalDate getNotEqualValue() {

        return LocalDate.now();
    }

    @Override
    public List<LocalDate> getNotInValue() {

        return List.of(LocalDate.now());
    }

    @Override
    public boolean isNullValue() {

        return true;
    }

    @Override
    public DateFilter<LocalDate> getBean() {

        final CommonFilter<LocalDate> commonFilter = super.getBean();
        return new DateFilterImpl(commonFilter.getEqual(), commonFilter.getIn(),
            commonFilter.getNotEqual(), commonFilter.getNotIn(),
            commonFilter.getIsNull(), LocalDate.now(), LocalDate.now());
    }

    @Override
    public DateFilter<LocalDate> getSecondBean() {

        return getBean();
    }
}
