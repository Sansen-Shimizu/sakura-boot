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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CommonFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The test class for the common filter that extends
 * {@link AbstractCommonFilterTest}.
 *
 * @author Malcolm Rozé
 * @see    CommonFilterImpl
 * @see    AbstractCommonFilterTest
 * @since  0.1.0
 */
class CommonFilterTest extends AbstractCommonFilterTest<Integer> {

    @Override
    public Integer getEqualValue() {

        return 0;
    }

    @Override
    public List<Integer> getInValue() {

        return List.of(0);
    }

    @Override
    public Integer getNotEqualValue() {

        return 0;
    }

    @Override
    public List<Integer> getNotInValue() {

        return List.of(0);
    }

    @Override
    public boolean isNullValue() {

        return true;
    }

    @Test
    @DisplayName("GIVEN a CommonFilter constructor and null list,"
        + " WHEN creating a commonFilter,"
        + " THEN the list should be empty")
    final void testConstructorNullList() {

        // WHEN
        final CommonFilter<Integer> commonFilter
            = new CommonFilterImpl<>(null, null, null, null, null);

        // THEN
        assertThat(commonFilter.getIn()).isEmpty();
        assertThat(commonFilter.getNotIn()).isEmpty();
    }
}
