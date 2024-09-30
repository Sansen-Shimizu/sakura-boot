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
import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The base test interface for all dataPresentation. This interface provides
 * common tests for testing {@link DataPresentation}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete dataPresentation test class that inherits from
 * {@link DataPresentationTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DataPresentationTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourEntityTest
 *     implements DataPresentationTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        DataPresentation
 * @see        SuperTest
 * @see        BeanTest
 * @since      0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface DataPresentationTest<D extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperTest<I>, BeanTest<D> {

    @Override
    default List<String> includeFieldsEqAndHash() {

        return List.of("id");
    }

    @Test
    @DisplayName("GIVEN two DataPresentation with different IDs,"
        + " WHEN comparing them for equality,"
        + " THEN they shouldn't be equal")
    default void testEqualityDifferentId() {

        // GIVEN
        final D entity1 = getBean();
        final D entity2 = SerializationUtils.clone(getSecondBean());
        ReflectionTestUtils.setField(entity2, "id", getBiggerValidId());

        // WHEN
        final boolean areEqual = entity1.equals(entity2);

        // THEN
        assertThat(areEqual).isFalse();
    }

    @Test
    @DisplayName("GIVEN two DataPresentation objects with the same IDs,"
        + " WHEN comparing them for order,"
        + " THEN they should be considered equal")
    default void testCompareWithSameId() {

        // GIVEN
        final D entity1 = getBean();
        final D entity2 = getSecondBean();

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isZero();
    }

    @Test
    @DisplayName("GIVEN two DataPresentation objects with different IDs,"
        + " WHEN comparing them for order,"
        + " THEN the one with greater ID should be greater")
    default void testCompareWithGreaterId() throws Exception {

        // GIVEN
        final D entity1 = getBean();
        final D entity2 = getSecondBean();
        final Field id = entity2.getClass().getDeclaredField("id");
        id.setAccessible(true);
        final Object idValue = id.get(entity2);
        id.set(entity2, getBiggerValidId());

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isNegative();

        // RESET
        id.set(entity2, idValue);
    }

    @Test
    @DisplayName("GIVEN two DataPresentation objects with different IDs,"
        + " WHEN comparing them for order,"
        + " THEN the one with smaller ID should be smaller")
    default void testCompareWithSmallerId() throws Exception {

        // GIVEN
        final D entity1 = getBean();
        final D entity2 = getSecondBean();
        final Field id = entity1.getClass().getDeclaredField("id");
        id.setAccessible(true);
        final Object idValue = id.get(entity1);
        id.set(entity1, getBiggerValidId());

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isPositive();

        // RESET
        id.set(entity1, idValue);
    }

    @Test
    @DisplayName("GIVEN a DataPresentation object compared to itself,"
        + " WHEN comparing them for order,"
        + " THEN it should be equal")
    default void testCompareToItself() {

        // GIVEN
        final D entity1 = getBean();
        final D entity2 = getBean();

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isZero();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    @DisplayName("GIVEN a DataPresentation object compared to null,"
        + " WHEN comparing them for order,"
        + " THEN the non-null DataPresentation should be greater")
    default void testCompareToNull() {

        // GIVEN
        final D entity1 = getBean();

        // WHEN
        assertThatThrownBy(() -> entity1.compareTo(null))

            // THEN
            .isInstanceOfAny(NullPointerException.class);
    }

    @Test
    @DisplayName("GIVEN two DataPresentation objects with different types,"
        + " WHEN comparing them for order,"
        + " THEN the compared DataPresentation should be greater")
    default void testCompareToDifferentType() {

        // GIVEN
        final D entity1 = getBean();
        final DataPresentationForTest<I> differentEntity
            = new DataPresentationForTest<>(getValidId());

        // WHEN
        assertThatThrownBy(() -> entity1.compareTo(differentEntity))

            // THEN
            .isInstanceOfAny(ClassCastException.class);
    }

    @Test
    @DisplayName("GIVEN a DataPresentation object with the first one with "
        + "null ID,"
        + " WHEN comparing them for order,"
        + " THEN the one with null ID should be considered smaller")
    default void testCompareFirstNullId() throws Exception {

        // GIVEN
        final D entity1 = getBean();
        final D entity2 = getSecondBean();
        final Field id = entity1.getClass().getDeclaredField("id");
        id.setAccessible(true);
        final Object idValue = id.get(entity1);
        id.set(entity1, null);

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isNegative();

        // RESET
        id.set(entity1, idValue);
    }
}
