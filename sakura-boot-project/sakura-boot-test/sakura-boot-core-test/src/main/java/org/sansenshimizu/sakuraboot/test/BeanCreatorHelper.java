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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * The bean creator helper used to create all kinds of beans with every field
 * set to random values using reflection.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
public final class BeanCreatorHelper {

    /**
     * The message for no constructor found for class error.
     */
    private static final String NO_CONSTRUCTOR_FOUND_FOR_CLASS
        = "no constructor found for class ";

    /**
     * The Random instance.
     */
    private static final Random RANDOM
        = new Random(Instant.now().toEpochMilli());

    /**
     * The Bean holder map with the class name as key.
     */
    private static final Map<String, BeanHolder<?>> BEAN_HOLDERS
        = new ConcurrentHashMap<>();

    /**
     * The String size for random strings.
     */
    private static final int STRING_SIZE = 8;

    private BeanCreatorHelper() {

        throw new UnsupportedOperationException(
            "This class is not meant to be instantiated.");
    }

    /**
     * Get an empty bean for the given bean class.
     *
     * @param  <T>       The type of the bean.
     * @param  beanClass the data class of the data.
     * @return           the empty bean.
     */
    public static <T> T getEmptyBean(final Class<T> beanClass) {

        try {

            return createBeanFromConstructorWithNullFields(beanClass);
        } catch (final InstantiationException e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * Get a bean for the given bean class.
     *
     * @param  <T>       The type of the bean.
     * @param  beanClass the data class of the data.
     * @return           the bean.
     */
    public static <T extends Serializable> T getBean(final Class<T> beanClass) {

        return getBeanHolder(beanClass).bean();
    }

    /**
     * Get a second bean for the given bean class.
     *
     * @param  <T>       The type of the bean.
     * @param  beanClass the data class of the data.
     * @return           the second bean.
     */
    public static <
        T extends Serializable> T getSecondBean(final Class<T> beanClass) {

        return getBeanHolder(beanClass).secondBean();
    }

    static <T extends Serializable> BeanHolder<T> getBeanHolder(
        final Class<T> beanClass) {

        if (!BEAN_HOLDERS.containsKey(beanClass.getName())) {

            createBeans(beanClass);
        }
        @SuppressWarnings("unchecked")
        final BeanHolder<T> beanHolder
            = (BeanHolder<T>) BEAN_HOLDERS.get(beanClass.getName());
        return beanHolder;
    }

    private static <
        T extends Serializable> void createBeans(final Class<T> beanClass) {

        final T bean;

        try {

            bean = createBeanFromConstructor(beanClass);
        } catch (final InstantiationException e) {

            throw new RuntimeException(
                NO_CONSTRUCTOR_FOUND_FOR_CLASS + beanClass.getName(), e);
        }
        final Pair<T, T> pair = Pair.of(bean, SerializationUtils.clone(bean));
        BEAN_HOLDERS.put(beanClass.getName(),
            new BeanHolder<>(pair.getLeft(), pair.getRight()));
    }

    /**
     * Create a bean from a constructor retrieved from the class with null
     * fields.
     *
     * @param  <T>       the type of the bean.
     * @param  beanClass the bean class of the bean.
     * @return           the bean created.
     */
    static <
        T> T createBeanFromConstructorWithNullFields(final Class<T> beanClass)
            throws InstantiationException {

        @SuppressWarnings("unchecked")
        final T bean = (T) createBeanFromConstructor(
            ReflectionUtils.getClassInfo(beanClass, null), true,
            new ArrayList<>(), new ArrayList<>());
        return bean;
    }

    /**
     * Create a bean from a constructor retrieved from the class.
     *
     * @param  <T>       the type of the bean.
     * @param  beanClass the bean class of the bean.
     * @return           the bean created.
     */
    static <T> T createBeanFromConstructor(final Class<T> beanClass)
        throws InstantiationException {

        @SuppressWarnings("unchecked")
        final T bean = (T) createBeanFromConstructor(
            ReflectionUtils.getClassInfo(beanClass, null), false,
            new ArrayList<>(), new ArrayList<>());
        return bean;
    }

    /**
     * Create a bean from a constructor retrieved from the
     * {@link ReflectionUtils.ClassInfo}.
     *
     * @param  classInfo        the {@link ReflectionUtils.ClassInfo} with the
     *                          information of the bean.
     * @param  nullField        if the fields should be set to null.
     * @param  origins          the origin classes that need to create this
     *                          bean.
     * @param  originsInstances the instances of the origin classes.
     * @return                  the bean created.
     */
    private static Object createBeanFromConstructor(
        final ReflectionUtils.ClassInfo classInfo, final boolean nullField,
        final List<Class<?>> origins, final List<Object> originsInstances)
        throws InstantiationException {

        final Class<?> clazz = classInfo.clazz();

        try {

            final Constructor<?> noArgConstructor
                = clazz.getDeclaredConstructor();
            noArgConstructor.setAccessible(true);
            final Object bean = noArgConstructor.newInstance();

            if (!nullField) {

                origins.add(clazz);
                originsInstances.add(bean);
                populateFields(clazz, new Object[] {
                    bean
                }, origins, originsInstances);
                originsInstances.remove(bean);
                origins.remove(clazz);
            }
            return bean;
        } catch (final NoSuchMethodException | IllegalAccessException
            | InvocationTargetException e) {

            for (final Constructor<?> constructor: clazz
                .getDeclaredConstructors()) {

                try {

                    constructor.setAccessible(true);
                    final Type[] parameterTypes
                        = constructor.getGenericParameterTypes();
                    final Object[] args = new Object[parameterTypes.length];

                    for (int i = 0; i < parameterTypes.length; i++) {

                        if (nullField) {

                            args[i] = null;
                        } else {

                            origins.add(clazz);
                            args[i] = generateValueForType(parameterTypes[i],
                                classInfo, origins, originsInstances);
                            origins.remove(clazz);
                        }
                    }
                    return constructor.newInstance(args);
                } catch (final InstantiationException | IllegalAccessException
                    | InvocationTargetException ignored) { /* ignore */ }
            }
        }

        throw new InstantiationException(
            NO_CONSTRUCTOR_FOUND_FOR_CLASS + clazz.getName());
    }

    @Nullable
    private static Object generateValueForDate(final Class<?> clazz) {

        if (clazz == LocalTime.class) {

            return LocalTime.now().truncatedTo(ChronoUnit.MICROS);
        }

        if (clazz == LocalDate.class) {

            return LocalDate.now();
        }

        if (clazz == LocalDateTime.class) {

            return LocalDateTime.now().truncatedTo(ChronoUnit.MICROS);
        }

        if (clazz == ZonedDateTime.class) {

            return ZonedDateTime.now(ZoneId.of("UTC"))
                .truncatedTo(ChronoUnit.MICROS);
        }

        if (clazz == OffsetDateTime.class) {

            return OffsetDateTime.now(ZoneId.of("UTC"))
                .truncatedTo(ChronoUnit.MICROS);
        }

        if (clazz == Instant.class) {

            return Instant.now().truncatedTo(ChronoUnit.MICROS);
        }
        return null;
    }

    private static Object generateCollectionForType(
        final ReflectionUtils.ClassInfo classInfo, final List<Class<?>> origins,
        final List<Object> originsInstances) {

        final Class<?> clazz = classInfo.clazz();
        final Object componentValue;

        if (origins.contains(clazz)) {

            componentValue = originsInstances.stream()
                .filter(clazz::isInstance)
                .findFirst()
                .orElse(null);
        } else {

            origins.add(classInfo.parametersType()[0].clazz());
            componentValue
                = generateValueForType(classInfo.parametersType()[0].clazz(),
                    classInfo.parametersType()[0], origins, originsInstances);
            origins.remove(classInfo.parametersType()[0].clazz());
        }
        final Object value;

        if (clazz == Set.class) {

            if (componentValue == null) {

                value = new HashSet<>();
            } else {

                final Set<Object> newSet = new HashSet<>();
                newSet.add(componentValue);
                value = newSet;
            }
        } else {

            if (clazz == Map.class) {

                final Object secondComponentValue;

                if (componentValue == null) {

                    value = new HashMap<>();
                } else {

                    origins.add(classInfo.parametersType()[1].clazz());
                    secondComponentValue = generateValueForType(
                        classInfo.parametersType()[1].clazz(),
                        classInfo.parametersType()[1], origins,
                        originsInstances);
                    origins.remove(classInfo.parametersType()[1].clazz());

                    final Map<Object, Object> newMap = new HashMap<>();
                    newMap.put(componentValue, secondComponentValue);
                    value = newMap;
                }
            } else {

                if (componentValue == null) {

                    value = new ArrayList<>();
                } else {

                    final List<Object> newList = new ArrayList<>();
                    newList.add(componentValue);
                    value = newList;
                }
            }
        }
        return value;
    }

    /**
     * Generates a value of the given type.
     *
     * @param  type             the type of the value to generate.
     * @param  parentClassInfo  the actual types of the constructor parameters.
     * @param  origins          the original classes to check infinite
     *                          recursion.
     * @param  originsInstances the instances of the origin classes.
     * @return                  the generated value.
     */
    @Nullable
    private static Object generateValueForType(
        final Type type,
        @Nullable final ReflectionUtils.ClassInfo parentClassInfo,
        final List<Class<?>> origins, final List<Object> originsInstances) {

        final ReflectionUtils.ClassInfo classInfo
            = ReflectionUtils.getClassInfo(type, parentClassInfo);
        final Class<?> clazz = classInfo.clazz();

        if (clazz == boolean.class || clazz == Boolean.class) {

            return RANDOM.nextBoolean();
        }

        if (clazz == byte.class || clazz == Byte.class) {

            final byte[] bytes = new byte[1];
            RANDOM.nextBytes(bytes);
            return bytes[0];
        }

        if (clazz == short.class || clazz == Short.class) {

            return (short) RANDOM.nextInt(Short.MAX_VALUE + 1);
        }

        if (clazz == int.class || clazz == Integer.class) {

            return RANDOM.nextInt();
        }

        if (clazz == long.class || clazz == Long.class) {

            return RANDOM.nextLong();
        }

        if (clazz == float.class || clazz == Float.class) {

            return RANDOM.nextFloat();
        }

        if (clazz == double.class || clazz == Double.class) {

            return RANDOM.nextDouble();
        }

        if (clazz == char.class || clazz == Character.class) {

            return RandomStringUtils.randomAlphabetic(1).charAt(0);
        }

        if (clazz == String.class) {

            return RandomStringUtils.randomAlphabetic(STRING_SIZE);
        }

        if (clazz == UUID.class) {

            return UUID.randomUUID();
        }

        if (Temporal.class.isAssignableFrom(clazz)) {

            return generateValueForDate(clazz);
        }

        if (clazz.isArray()) {

            final Class<?> componentType = clazz.getComponentType();

            if (origins.contains(componentType)) {

                return originsInstances.stream()
                    .filter(componentType::isInstance)
                    .findFirst()
                    .orElse(null);
            }
            final Object array = Array.newInstance(componentType, 1);
            origins.add(componentType);
            Array.set(array, 0, generateValueForType(componentType,
                parentClassInfo, origins, originsInstances));
            origins.remove(componentType);
            return array;
        }

        if (Collection.class.isAssignableFrom(clazz)
            || Map.class.isAssignableFrom(clazz)) {

            return generateCollectionForType(classInfo, origins,
                originsInstances);
        }

        try {

            if (!origins.isEmpty()
                && origins.subList(0, origins.size() - 1).contains(clazz)) {

                return originsInstances.stream()
                    .filter(clazz::isInstance)
                    .findFirst()
                    .orElse(null);
            }
            return createBeanFromConstructor(classInfo, false, origins,
                originsInstances);
        } catch (final InstantiationException ex) {

            return null;
        }
    }

    /**
     * Populates the fields of the given objects with values of the specified
     * type.
     *
     * @param clazz            the type of the objects
     * @param objects          the objects to populate
     * @param origins          the original classes to check infinite recursion.
     * @param originsInstances the instances of the origin classes.
     */
    private static void populateFields(
        final Class<?> clazz, final Object[] objects,
        final List<Class<?>> origins, final List<Object> originsInstances) {

        org.springframework.util.ReflectionUtils.doWithFields(clazz,
            (final Field field) -> {

                if (field.trySetAccessible()) {

                    final Object value
                        = generateValueForType(field.getGenericType(), null,
                            origins, originsInstances);

                    for (final Object o: objects) {

                        field.set(o, value);
                    }
                }
            }, field -> !Modifier.isFinal(field.getModifiers()));
    }

    /**
     * A bean holder with two beans.
     *
     * @param bean       the first bean.
     * @param secondBean the second bean.
     * @param <T>        the type of the beans.
     */
    public record BeanHolder<T>(T bean, T secondBean) {}
}
