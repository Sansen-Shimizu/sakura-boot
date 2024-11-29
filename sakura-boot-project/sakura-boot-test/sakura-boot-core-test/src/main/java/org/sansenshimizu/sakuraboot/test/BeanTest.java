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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.lang.Nullable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import org.sansenshimizu.sakuraboot.util.RelationshipUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The base test interface for all bean class.
 * This interface provides common tests for testing getter, equals, hashCode,
 * and toString of java bean class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete bean test class that inherits from {@link BeanTest},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link BeanTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourBeanTest implements BeanTest&lt;YourBean&gt; {
 *
 *     public static Object EXPECTED_FIELD = "value";
 *
 *     public static Object BAD_FIELD = "bad value";
 *
 *     &#064;Override
 *     public YourClass getBean() {
 *
 *         return YourClass(EXPECTED_FIELD);
 *     }
 *
 *     &#064;Override
 *     public YourClass createSecondBean() {
 *
 *         return YourClass(BAD_FIELD);
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <T> The bean to test.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
public interface BeanTest<T> {

    /**
     * The string for the serialVersionUID field.
     */
    String SERIAL_VERSION_UID = "serialVersionUID";

    /**
     * Create a bean with fields.
     *
     * @return The created bean.
     */
    T getBean();

    /**
     * Create a second bean with fields.
     *
     * @return The created bean.
     */
    T getSecondBean();

    /**
     * If the getter methods of the bean class must be tested, this function
     * must return false.
     *
     * @return {@code false} if the getter methods of the bean need to be
     *         tested, {@code true} otherwise.
     */
    default boolean disabledGetterTest() {

        return false;
    }

    /**
     * If the equals and hash code methods of the bean class must be tested,
     * this function must return false.
     *
     * @return {@code false} if the equals and hash code methods of the bean
     *         need to be tested, {@code true} otherwise.
     */
    default boolean disabledEqAndHashTest() {

        return false;
    }

    /**
     * If the toString method of the bean class must be tested, this function
     * must return false.
     *
     * @return {@code false} if the toString method of the bean needs to be
     *         tested, {@code true} otherwise.
     */
    default boolean disabledToStringTest() {

        return false;
    }

    /**
     * Get the name of all the fields that need to be tested on getter methods.
     * Exclude all other fields.
     * If the list is empty, test all the fields by reflection.
     *
     * @return A list of the field names to be tested.
     */
    default List<String> includeFieldsGetter() {

        return List.of();
    }

    /**
     * Get the name of all the fields that need to be tested on equals, hashCode
     * methods.
     * Exclude all other fields.
     * If the list is empty, test all the fields by reflection.
     *
     * @return A list of the field names to be tested.
     */
    default List<String> includeFieldsEqAndHash() {

        return List.of();
    }

    /**
     * Get the name of all the fields that need to be tested on toString method.
     * Exclude all other fields.
     * If the list is empty, test all the fields by reflection.
     *
     * @return A list of the field names to be tested.
     */
    default List<String> includeFieldsToString() {

        return List.of();
    }

    @Nullable
    private Object invokeGetter(final T bean, final String fieldName) {

        Object value;

        try {

            value = ReflectionTestUtils.invokeGetterMethod(bean, fieldName);
        } catch (final IllegalArgumentException e) {

            value = ReflectionTestUtils.invokeMethod(bean,
                "is"
                    + Character.toUpperCase(fieldName.charAt(0))
                    + fieldName.substring(1));
        }
        return value;
    }

    private void testGetterHelper(final T bean, final String fieldName) {

        final Object expectedValue
            = ReflectionTestUtils.getField(bean, fieldName);

        // WHEN
        final Object valueFirstCall = invokeGetter(bean, fieldName);
        final Object valueSecondCall = invokeGetter(bean, fieldName);

        // THEN
        if (valueFirstCall instanceof final Collection<?> collection
            && !collection.isEmpty()) {

            assertThatThrownBy(collection::clear)
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        }

        if (valueFirstCall instanceof final Object[] array
            && array.length > 0) {

            Arrays.fill(array, null);
            assertThat(valueFirstCall).isNotEqualTo(expectedValue);
        }
        assertThat(expectedValue).isEqualTo(valueSecondCall);
    }

    @DisabledGetterTest
    @Test
    @DisplayName("GIVEN one bean objects,"
        + " WHEN calling Getter,"
        + " THEN they should be equal to the field value")
    default void testGetter() {

        // GIVEN
        final T bean = getBean();

        // WHEN
        if (includeFieldsGetter().isEmpty()) {

            ReflectionUtils.doWithFields(bean.getClass(),
                field -> testGetterHelper(bean, field.getName()),
                field -> !SERIAL_VERSION_UID.equals(field.getName()));
        } else {

            includeFieldsGetter()
                .forEach(fieldName -> testGetterHelper(bean, fieldName));
        }
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN two bean objects with the same fields,"
        + " WHEN comparing them for equality,"
        + " THEN they should be equal")
    default void testEqualitySameFields() {

        // GIVEN
        final T bean1 = getBean();
        final T bean2 = getSecondBean();

        // WHEN
        final boolean areEqual = bean1.equals(bean2);

        // THEN
        assertThat(areEqual).isTrue();
    }

    private void testEqualityDifferentFieldsHelper(
        final T bean1, final T bean2, final String fieldName) {

        final Object lastValue = ReflectionTestUtils.getField(bean1, fieldName);
        ReflectionTestUtils.setField(bean1, fieldName, null);

        // WHEN
        final boolean areEqual = bean1.equals(bean2);

        // THEN
        assertThat(areEqual).isFalse();

        // RESET
        ReflectionTestUtils.setField(bean1, fieldName, lastValue);
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN two bean objects with different fields,"
        + " WHEN comparing them for equality,"
        + " THEN they shouldn't be equal")
    default void testEqualityDifferentFields() {

        // GIVEN
        final T bean1 = getBean();
        final T bean2 = getSecondBean();

        if (includeFieldsEqAndHash().isEmpty()) {

            ReflectionUtils.doWithFields(bean1.getClass(),
                field -> testEqualityDifferentFieldsHelper(bean1, bean2,
                    field.getName()),
                field -> !SERIAL_VERSION_UID.equals(field.getName()));
        } else {

            includeFieldsEqAndHash()
                .forEach(fieldName -> testEqualityDifferentFieldsHelper(bean1,
                    bean2, fieldName));
        }
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN a bean object compared to itself,"
        + " WHEN comparing for equality,"
        + " THEN it should be equal")
    default void testEqualityToItself() {

        // GIVEN
        final T bean1 = getBean();
        final T bean2 = getBean();

        // WHEN
        final boolean areEqual = bean1.equals(bean2);

        // THEN
        assertThat(areEqual).isTrue();
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN a bean object compared to null,"
        + " WHEN comparing for equality,"
        + " THEN they shouldn't be equal")
    default void testEqualityToNull() {

        // GIVEN
        final T bean1 = getBean();

        // WHEN
        @SuppressWarnings({
            "ConstantValue", "java:S2159"
        })
        final boolean areEqual = bean1.equals(null);

        // THEN
        // noinspection ConstantValue
        assertThat(areEqual).isFalse();
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN two bean objects with different types,"
        + " WHEN comparing them for equality,"
        + " THEN they shouldn't be equal")
    default void testEqualityToDifferentType() {

        // GIVEN
        final T bean1 = getBean();
        final Object differentObject = new Object();

        // WHEN
        final boolean areEqual = bean1.equals(differentObject);

        // THEN
        assertThat(areEqual).isFalse();
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN two bean objects with the same Fields,"
        + " WHEN comparing their hash codes,"
        + " THEN they should have the same hash code")
    default void testCompareHashSameFields() {

        // GIVEN
        final T bean1 = getBean();
        final T bean2 = getSecondBean();

        // WHEN
        final int hashCode1 = bean1.hashCode();
        final int hashCode2 = bean2.hashCode();

        // THEN
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    private void testCompareHashDifferentFieldsHelper(
        final T bean1, final T bean2, final String fieldName) {

        final Object lastValue = ReflectionTestUtils.getField(bean1, fieldName);
        ReflectionTestUtils.setField(bean1, fieldName, null);

        // WHEN
        final int hashCode1 = bean1.hashCode();
        final int hashCode2 = bean2.hashCode();

        // THEN
        assertThat(hashCode1).isNotEqualTo(hashCode2);

        // RESET
        ReflectionTestUtils.setField(bean1, fieldName, lastValue);
    }

    @DisabledEqAndHashTest
    @Test
    @DisplayName("GIVEN two bean objects with different fields,"
        + " WHEN comparing their hash codes,"
        + " THEN they should have different hash codes")
    default void testCompareHashDifferentFields() {

        // GIVEN
        final T bean1 = getBean();
        final T bean2 = getSecondBean();

        if (includeFieldsEqAndHash().isEmpty()) {

            ReflectionUtils.doWithFields(bean1.getClass(),
                field -> testCompareHashDifferentFieldsHelper(bean1, bean2,
                    field.getName()),
                field -> !SERIAL_VERSION_UID.equals(field.getName()));
        } else {

            includeFieldsEqAndHash().forEach(
                fieldName -> testCompareHashDifferentFieldsHelper(bean1, bean2,
                    fieldName));
        }
    }

    /**
     * Returns a boolean value indicating whether null values should be included
     * in the toString
     * representation of the bean.
     *
     * @return {@code true} if null values should be included, {@code false}
     *         otherwise.
     */
    default boolean toStringIncludeNullValue() {

        return true;
    }

    private void testToStringHelper(
        final T bean, final String fieldName, final String toString) {

        final Object fieldValue = ReflectionTestUtils.getField(bean, fieldName);

        if (!toStringIncludeNullValue() && fieldValue == null) {

            return;
        }
        final String regexCollection = "\\[.*]";

        if (fieldValue instanceof final Object[] array && array.length > 0) {

            assertThat(toString).containsAnyOf(Arrays.stream(array)
                .map(Object::toString)
                .toArray(String[]::new));
            assertThat(toString).containsPattern(
                ".*" + fieldName + "=" + regexCollection + ".*");
        } else if (fieldValue instanceof final Collection<?> collection
            && !collection.isEmpty()) {

            assertThat(toString).containsAnyOf(collection.stream()
                .map(Object::toString)
                .toArray(String[]::new));
            assertThat(toString).containsPattern(
                ".*" + fieldName + "=" + regexCollection + ".*");
        } else {

            final String expectedToString = fieldName + "=" + fieldValue;

            assertThat(toString).contains(expectedToString);
        }
    }

    @DisabledToStringTest
    @Test
    @DisplayName("GIVEN a bean object,"
        + " WHEN calling toString,"
        + " THEN the correct toString should be printed")
    default void testToString() {

        // GIVEN
        final T bean = getBean();

        // WHEN
        final String toString = bean.toString();

        // THEN
        if (includeFieldsToString().isEmpty()) {

            ReflectionUtils.doWithFields(bean.getClass(),
                field -> testToStringHelper(bean, field.getName(), toString),
                field -> !SERIAL_VERSION_UID.equals(field.getName())
                    && !Modifier.isStatic(field.getModifiers())
                    && RelationshipUtils.isNotRelationWithMappedBy(field));
        } else {

            includeFieldsToString().forEach(
                fieldName -> testToStringHelper(bean, fieldName, toString));
        }
    }

    /**
     * Annotation to disable the getter test.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @DisabledIf("disabledGetterTest")
    @interface DisabledGetterTest {}

    /**
     * Annotation to disable the equals and hashCode test.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @DisabledIf("disabledEqAndHashTest")
    @interface DisabledEqAndHashTest {}

    /**
     * Annotation to disable the toString test.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @DisabledIf("disabledToStringTest")
    @interface DisabledToStringTest {}
}
