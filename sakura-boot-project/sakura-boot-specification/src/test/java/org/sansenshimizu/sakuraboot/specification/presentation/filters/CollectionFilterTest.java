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

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.CollectionFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The test class for the collection filter that implements {@link FilterTest}.
 *
 * @author Malcolm Rozé
 * @see    CollectionFilterImpl
 * @see    FilterTest
 * @since  0.1.0
 */
class CollectionFilterTest implements FilterTest {

    /**
     * The equal value.
     */
    private static final List<Integer> EQUAL = List.of(0);

    /**
     * The not equal value.
     */
    private static final List<Integer> NOT_EQUAL = List.of(0);

    /**
     * The is empty value.
     */
    private static final Boolean IS_EMPTY = true;

    /**
     * The is member value.
     */
    private static final Integer IS_MEMBER = 0;

    /**
     * The is not member value.
     */
    private static final Integer IS_NOT_MEMBER = 0;

    @Override
    public CollectionFilter<Integer> getBean() {

        return new CollectionFilterImpl<>(EQUAL, NOT_EQUAL, IS_EMPTY, IS_MEMBER,
            IS_NOT_MEMBER);
    }

    @Override
    public CollectionFilter<Integer> getSecondBean() {

        return getBean();
    }

    @Test
    @DisplayName("GIVEN a CollectionFilter constructor and null list,"
        + " WHEN creating a commonFilter,"
        + " THEN the list should be empty")
    final void testConstructorNullList() {

        // WHEN
        final CollectionFilter<Integer> collectionFilter
            = new CollectionFilterImpl<>(null, null, null, null, null);

        // THEN
        assertThat(collectionFilter.getEqual()).isEmpty();
        assertThat(collectionFilter.getNotEqual()).isEmpty();
    }
}
