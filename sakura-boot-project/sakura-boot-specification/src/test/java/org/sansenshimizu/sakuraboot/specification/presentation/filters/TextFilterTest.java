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

import lombok.Getter;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;
import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.TextFilter;

/**
 * The test class for the text filter that extends
 * {@link AbstractCommonFilterTest}.
 *
 * @author Malcolm Rozé
 * @see    TextFilterImpl
 * @see    AbstractCommonFilterTest
 * @since  0.1.0
 */
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
class TextFilterTest extends AbstractCommonFilterTest<String> {

    /**
     * String use to test value.
     */
    private static final String TEST_STRING = "test string";

    @SuppressWarnings("SuspiciousGetterSetter")
    @Override
    public String getEqualValue() {

        return TEST_STRING;
    }

    @Override
    public List<String> getInValue() {

        return List.of(TEST_STRING);
    }

    @SuppressWarnings({
        "SuspiciousGetterSetter", "java:S4144"
    })
    @Override
    public String getNotEqualValue() {

        return TEST_STRING;
    }

    @Override
    public List<String> getNotInValue() {

        return List.of(TEST_STRING);
    }

    @Override
    public boolean isNullValue() {

        return true;
    }

    @Override
    public TextFilter getBean() {

        final CommonFilter<String> commonFilter = super.getBean();
        return new TextFilterImpl(commonFilter.getEqual(), commonFilter.getIn(),
            commonFilter.getNotEqual(), commonFilter.getNotIn(),
            commonFilter.getIsNull(), TEST_STRING, TEST_STRING, TEST_STRING,
            TEST_STRING, true);
    }

    @Override
    public TextFilter getSecondBean() {

        return getBean();
    }
}
