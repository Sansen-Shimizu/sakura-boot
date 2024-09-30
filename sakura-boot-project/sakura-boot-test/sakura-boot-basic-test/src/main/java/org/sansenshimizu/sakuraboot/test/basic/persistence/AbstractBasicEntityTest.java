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

package org.sansenshimizu.sakuraboot.test.basic.persistence;

import java.io.Serial;
import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.sansenshimizu.sakuraboot.basic.persistence.AbstractBasicEntity;
import org.sansenshimizu.sakuraboot.test.BasicTest;
import org.sansenshimizu.sakuraboot.test.DataPresentationTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base test class for all AbstractBasicEntity. This class provides common
 * tests for testing {@link AbstractBasicEntity}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete AbstractBasicEntity test class that inherits from
 * {@link AbstractBasicEntityTest}, follow these steps:
 * </p>
 * <p>
 * Extends the {@link AbstractBasicEntityTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourEntityTest
 *     extends AbstractBasicEntityTest&lt;YourEntity, YourIdType&gt; {
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
 * @param  <E> The {@link AbstractBasicEntity} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        AbstractBasicEntity
 * @see        DataPresentationTest
 * @see        BasicTest
 * @since      0.1.0
 */
public abstract class AbstractBasicEntityTest<E extends AbstractBasicEntity<I>,
    I extends Comparable<? super I> & Serializable>
    implements DataPresentationTest<E, I>, BasicTest<E, I> {

    @Override
    public E getBean() {

        return getUtil().getEntity();
    }

    @Override
    public E getSecondBean() {

        return getUtil().getSecondEntity();
    }

    @Test
    @DisplayName("GIVEN two hibernate proxies of AbstractBasicEntity "
        + "objects with the same ID,"
        + " WHEN comparing them for equality,"
        + " THEN they should be equal")
    final void testEqualityWithHibernateProxySameId() {

        // GIVEN
        final BasicEntityHibernateProxyForTest<I> entity1
            = new BasicEntityHibernateProxyForTest<>(getValidId());
        final BasicEntityHibernateProxyForTest<I> entity2
            = new BasicEntityHibernateProxyForTest<>(getValidId());

        // WHEN
        final boolean areEqual = entity1.equals(entity2);

        // THEN
        assertThat(areEqual).isTrue();
    }

    @Test
    @DisplayName("GIVEN two hibernate proxies of AbstractBasicEntity "
        + "objects with different IDs,"
        + " WHEN comparing them for equality,"
        + " THEN they shouldn't be equal")
    final void testEqualityHibernateProxyDifferentId() {

        // GIVEN
        final BasicEntityHibernateProxyForTest<I> entity1
            = new BasicEntityHibernateProxyForTest<>(getValidId());
        final BasicEntityHibernateProxyForTest<I> entity2
            = new BasicEntityHibernateProxyForTest<>(getBiggerValidId());

        // WHEN
        final boolean areEqual = entity1.equals(entity2);

        // THEN
        assertThat(areEqual).isFalse();
    }

    @Test
    @DisplayName("GIVEN two Hibernate proxies of AbstractBasicEntity "
        + "objects with same IDs,"
        + " WHEN comparing their hash codes,"
        + " THEN they should have the same hash codes")
    final void testCompareHashWithHibernateProxySameId() {

        // GIVEN
        final BasicEntityHibernateProxyForTest<I> entity1
            = new BasicEntityHibernateProxyForTest<>(getValidId());
        final BasicEntityHibernateProxyForTest<I> entity2
            = new BasicEntityHibernateProxyForTest<>(getValidId());

        // WHEN
        final int hashCode1 = entity1.hashCode();
        final int hashCode2 = entity2.hashCode();

        // THEN
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("GIVEN two Hibernate proxies of AbstractBasicEntity "
        + "objects with different IDs,"
        + " WHEN comparing their hash codes,"
        + " THEN they should have different hash codes")
    final void testCompareHashWithHibernateProxyDifferentId() {

        // GIVEN
        final BasicEntityHibernateProxyForTest<I> entity1
            = new BasicEntityHibernateProxyForTest<>(getValidId());
        final BasicEntityHibernateProxyForTest<I> entity2
            = new BasicEntityHibernateProxyForTest<>(getOtherValidId());

        // WHEN
        final int hashCode1 = entity1.hashCode();
        final int hashCode2 = entity2.hashCode();

        // THEN
        assertThat(hashCode1).isNotEqualTo(hashCode2);
    }

    @Test
    @DisplayName("GIVEN two hibernate proxies of AbstractBasicEntity "
        + "objects with the same ID,"
        + " WHEN comparing them for order,"
        + " THEN they should be considered equal")
    final void testCompareWithHibernateProxySameId() {

        // GIVEN
        final BasicEntityHibernateProxyForTest<I> entity1
            = new BasicEntityHibernateProxyForTest<>(getValidId());
        final BasicEntityHibernateProxyForTest<I> entity2
            = new BasicEntityHibernateProxyForTest<>(getValidId());

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isZero();
    }

    @Test
    @DisplayName("GIVEN two hibernate proxies of AbstractBasicEntity "
        + "objects with different IDs,"
        + " WHEN comparing them for order,"
        + " THEN the one with greater ID should be greater")
    final void testCompareHibernateProxyDifferentId() {

        // GIVEN
        final BasicEntityHibernateProxyForTest<I> entity1
            = new BasicEntityHibernateProxyForTest<>(getValidId());
        final BasicEntityHibernateProxyForTest<I> entity2
            = new BasicEntityHibernateProxyForTest<>(getBiggerValidId());

        // WHEN
        final int compareResult = entity1.compareTo(entity2);

        // THEN
        assertThat(compareResult).isNegative();
    }

    /**
     * A simple implementation of {@link AbstractBasicEntity} and
     * {@link HibernateProxy} to test the case the object is being a hibernate
     * proxy.
     *
     * @param  <I> The ID of type Comparable and Serializable.
     * @author     Malcolm Rozé
     * @since      0.1.0
     */
    @SuppressWarnings("java:S2057")
    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static final class BasicEntityHibernateProxyForTest<
        I extends Comparable<? super I> & Serializable>
        extends AbstractBasicEntity<I>
        implements HibernateProxy {

        /**
         * The id if needed in the tests.
         */
        private final I id;

        /**
         * The {@link SimpleLazyInitializerForTest}.
         */
        @SuppressWarnings("java:S1948")
        private final LazyInitializer hibernateLazyInitializer
            = new SimpleLazyInitializerForTest();

        @SuppressWarnings("ReadResolveAndWriteReplaceProtected")
        @Serial
        @Override
        public Object writeReplace() {

            throw new UnsupportedOperationException(
                "Unimplemented method 'writeReplace'");
        }
    }

    /**
     * A simple implementation of {@link LazyInitializer} to test the case a
     * dataPresentation object is being a hibernate proxy.
     * Only implement the getPersistentClass method.
     *
     * @author Malcolm Rozé
     * @since  0.1.0
     */
    @SuppressWarnings("java:S2972")
    @Getter
    static final class SimpleLazyInitializerForTest implements LazyInitializer {

        /**
         * Message to indicate this simple implementation only implements the
         * getPersistentClass.
         */
        private static final String UNIMPLEMENTED_MESSAGE
            = "Unimplemented method";

        /**
         * The persistent class use in test.
         */
        private final Class<?> persistentClass
            = BasicEntityHibernateProxyForTest.class;

        @Override
        public void initialize() throws HibernateException {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public Object getIdentifier() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void setIdentifier(final Object id) {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public String getEntityName() {

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

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
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
        public boolean isUnwrap() {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }

        @Override
        public void setUnwrap(final boolean unwrap) {

            throw new UnsupportedOperationException(UNIMPLEMENTED_MESSAGE);
        }
    }
}
