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

package org.sansenshimizu.sakuraboot.util;

import java.io.Serial;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The test class for the util class {@link ToStringUtils}. This class provides
 * common tests for testing {@link ToStringUtils}.
 *
 * @author Malcolm Rozé
 * @see    ToStringUtils
 * @since  0.1.0
 */
class ToStringUtilTest {

    /**
     * The name of a class.
     */
    private static final String CLASS_STRING = "classString";

    /**
     * A field name.
     */
    private static final String FIELD_NAME = "fieldName";

    /**
     * A field value.
     */
    private static final String FIELD_VALUE = "fieldValue";

    /**
     * A field name for collection.
     */
    private static final String FIELD_COLLECTION_NAME = "fieldCollectionName";

    /**
     * A field value for collection.
     */
    private static final List<String> FIELD_COLLECTION_VALUE
        = List.of(FIELD_VALUE);

    /**
     * A field value for a big collection.
     */
    private static final List<String> FIELD_BIG_COLLECTION_VALUE
        = List.of(FIELD_VALUE, FIELD_VALUE, FIELD_VALUE, FIELD_VALUE);

    /**
     * A field name for an array.
     */
    private static final String FIELD_ARRAY_NAME = "fieldArrayName";

    /**
     * A field value for an array.
     */
    private static final String[] FIELD_ARRAY_VALUE = {
        FIELD_VALUE
    };

    /**
     * A field value for a big array.
     */
    private static final String[] FIELD_BIG_ARRAY_VALUE = {
        FIELD_VALUE, FIELD_VALUE, FIELD_VALUE, FIELD_VALUE
    };

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling objectToString with a null object,"
        + " THEN the result should be equal to null as string")
    final void testObjectToStringWithNull() {

        // GIVEN
        final String expectedResult = "null";

        // WHEN
        final String result = ToStringUtils.objectToString(null);

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with no specific argument,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithNoField() {

        // GIVEN
        final String expectedResult = CLASS_STRING + "{}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING, List.of());

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with null field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithNullField() {

        // GIVEN
        final String expectedResult = CLASS_STRING + "{}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_NAME, null)));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with null field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithPrintNullField() {

        // GIVEN
        final String expectedResult
            = CLASS_STRING + "{" + FIELD_NAME + "=null}";

        // WHEN
        final String result = ToStringUtils.toStringPrintNullFields(
            CLASS_STRING, List.of(Pair.of(FIELD_NAME, null)));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with hibernate proxy field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithHibernateProxyField() {

        // GIVEN
        final int id = 1;
        final HibernateProxy proxy = new SimpleHibernateProxyForTest(id);
        final String fieldValue
            = "SimpleHibernateProxyForTestProxy{id=" + id + "}";
        final String expectedResult
            = CLASS_STRING + "{" + FIELD_NAME + "=" + fieldValue + "}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_NAME, proxy)));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with an empty collection field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithEmptyCollectionField() {

        // GIVEN
        final String expectedResult = CLASS_STRING + "{}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_COLLECTION_NAME, List.of())));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with an empty array field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithEmptyArrayField() {

        // GIVEN
        final String expectedResult = CLASS_STRING + "{}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_ARRAY_NAME, new String[] {})));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with some field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithField() {

        // GIVEN
        final String expectedResult = CLASS_STRING
            + "{"
            + FIELD_NAME
            + "="
            + FIELD_VALUE
            + ", "
            + FIELD_COLLECTION_NAME
            + "=["
            + FIELD_VALUE
            + "], "
            + FIELD_ARRAY_NAME
            + "=["
            + FIELD_VALUE
            + "]}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_NAME, FIELD_VALUE),
                Pair.of(FIELD_COLLECTION_NAME, FIELD_COLLECTION_VALUE),
                Pair.of(FIELD_ARRAY_NAME, FIELD_ARRAY_VALUE)));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with some big field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithBigField() {

        // GIVEN
        final String expectedResult = CLASS_STRING
            + "{"
            + FIELD_COLLECTION_NAME
            + "=["
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", ...], "
            + FIELD_ARRAY_NAME
            + "=["
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", ...]}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_COLLECTION_NAME, FIELD_BIG_COLLECTION_VALUE),
                Pair.of(FIELD_ARRAY_NAME, FIELD_BIG_ARRAY_VALUE)));

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("GIVEN a ToStringUtil,"
        + " WHEN calling toString with some big field,"
        + " THEN the result should be equal to the expected value")
    final void testToStringWithBigFieldAllPrint() {

        // GIVEN
        final String expectedResult = CLASS_STRING
            + "{"
            + FIELD_COLLECTION_NAME
            + "=["
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + "], "
            + FIELD_ARRAY_NAME
            + "=["
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + ", "
            + FIELD_VALUE
            + "]}";

        // WHEN
        final String result = ToStringUtils.toString(CLASS_STRING,
            List.of(Pair.of(FIELD_COLLECTION_NAME, FIELD_BIG_COLLECTION_VALUE),
                Pair.of(FIELD_ARRAY_NAME, FIELD_BIG_ARRAY_VALUE)),
            4);

        // THEN
        assertThat(result).isEqualTo(expectedResult);
    }

    /**
     * A simple implementation of {@link HibernateProxy} to test the case the
     * object is being a hibernate proxy.
     *
     * @author Malcolm Rozé
     * @since  0.1.0
     */
    static final class SimpleHibernateProxyForTest implements HibernateProxy {

        /**
         * The id if needed in the tests.
         */
        private final Object id;

        /**
         * Constructor to give an ID to the object.
         *
         * @param id The id if needed in the tests.
         */
        SimpleHibernateProxyForTest(final Object id) {

            this.id = id;
        }

        @SuppressWarnings("ReadResolveAndWriteReplaceProtected")
        @Serial
        @Override
        public Object writeReplace() {

            throw new UnsupportedOperationException(
                "Unimplemented method 'writeReplace'");
        }

        @Override
        public LazyInitializer getHibernateLazyInitializer() {

            return new SimpleLazyInitializerForTest(id);
        }
    }

    /**
     * A simple implementation of {@link LazyInitializer} to test the case a
     * dataPresentation object is being a hibernate proxy. Only implement the
     * getPersistentClass method.
     *
     * @author Malcolm Rozé
     * @since  0.1.0
     */
    static final class SimpleLazyInitializerForTest implements LazyInitializer {

        /**
         * Message to indicate this implementation only implement the
         * getPersistentClass.
         */
        private static final String UNIMPLEMENTED_MESSAGE
            = "Unimplemented method";

        /**
         * The identifier.
         */
        private Object identifier;

        SimpleLazyInitializerForTest(final Object identifier) {

            this.identifier = identifier;
        }

        @Override
        public void initialize() throws HibernateException {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public Object getIdentifier() {

            return identifier;
        }

        @Override
        public void setIdentifier(final Object id) {

            identifier = id;
        }

        @Override
        public String getEntityName() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public Class<?> getPersistentClass() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public boolean isUninitialized() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public Object getImplementation() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public Object getImplementation(
            final SharedSessionContractImplementor session)
            throws HibernateException {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void setImplementation(final Object target) {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public Class<?> getImplementationClass() {

            return SimpleHibernateProxyForTest.class;
        }

        @Override
        public String getImplementationEntityName() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public boolean isReadOnlySettingAvailable() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public boolean isReadOnly() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void setReadOnly(final boolean readOnly) {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public SharedSessionContractImplementor getSession() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void setSession(final SharedSessionContractImplementor session)
            throws HibernateException {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void unsetSession() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void setUnwrap(final boolean unwrap) {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public boolean isUnwrap() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }
    }
}
