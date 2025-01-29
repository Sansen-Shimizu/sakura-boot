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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.exceptions.BadRequestException;
import org.sansenshimizu.sakuraboot.exceptions.NotFoundException;

/**
 * Utility class for reflection.
 *
 * @author Malcolm Rozé
 * @since  0.1.1
 */
@UtilityClass
public class ReflectionUtils {

    /**
     * Check if a field exists from a class.
     * Get the field from the superclass if the field is not found in the class.
     *
     * @param  clazz     The class to check the field from.
     * @param  fieldName The name of the field.
     * @return           {@code true} if the field exists, {@code false}
     *                   otherwise.
     */
    public boolean isFieldExists(final Class<?> clazz, final String fieldName) {

        Class<?> currentClass = clazz;

        while (currentClass != null) {

            try {

                currentClass.getDeclaredField(fieldName);
                return true;
            } catch (final NoSuchFieldException e) {

                currentClass = currentClass.getSuperclass();
            }
        }

        return false;
    }

    /**
     * Get a field from a class and make it accessible.
     * Get the field from the superclass if the field is not found in the class.
     *
     * @param  clazz               The class to get the field from.
     * @param  fieldName           The name of the field.
     * @return                     A {@link Field}.
     * @throws BadRequestException If the field is not found.
     */
    public Field getField(final Class<?> clazz, final String fieldName) {

        Class<?> currentClass = clazz;

        while (currentClass != null) {

            try {

                final Field field = currentClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (final NoSuchFieldException e) {

                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NotFoundException(fieldName);
    }

    private Class<?> getParameterClass(
        final ParameterizedType parameterizedType, final int i,
        final Map<String, Class<?>> genericTypeInfo) {

        final Class<?> parameterClass
            = getClassInfo(parameterizedType.getActualTypeArguments()[i], null)
                .clazz();

        if (!genericTypeInfo.isEmpty()) {

            return genericTypeInfo.values()
                .stream()
                .filter(parameterClass::isAssignableFrom)
                .findFirst()
                .orElse(parameterClass);
        }
        return parameterClass;
    }

    @Nullable
    private <T> Class<T> getGenericClass(
        final String interfaceTypeName, final int typeIndex,
        final Map<String, Class<?>> genericTypeInfo, final Type interfaceType) {

        if (interfaceType instanceof final ParameterizedType parameterizedType
            && parameterizedType
                .getRawType() instanceof final Class<?> interfaceClass) {

            try {

                final Map<String,
                    Class<?>> actualGenericTypeInfo = IntStream
                        .range(0, interfaceClass.getTypeParameters().length)
                        .collect(HashMap::new,
                            (map, i) -> map.put(
                                interfaceClass.getTypeParameters()[i].getName(),
                                getParameterClass(parameterizedType, i,
                                    genericTypeInfo)),
                            Map::putAll);

                actualGenericTypeInfo.forEach(genericTypeInfo::putIfAbsent);

                return findGenericTypeFromInterface(interfaceClass,
                    interfaceTypeName, typeIndex, genericTypeInfo);
            } catch (final NotFoundException ignored) { /* ignore */ }
        }
        return null;
    }

    private <T> Class<T> findGenericTypeFromInterface(
        final Class<?> clazz, final String interfaceTypeName,
        final int typeIndex, final Map<String, Class<?>> genericTypeInfo) {

        for (final Type interfaceType: clazz.getGenericInterfaces()) {

            /*@formatter:off*/
            if (interfaceType
                instanceof final ParameterizedType parameterizedType
                && parameterizedType.getRawType()
                .getTypeName()
                .equals(interfaceTypeName)) {
                /*@formatter:on*/

                final Type beanType
                    = parameterizedType.getActualTypeArguments()[typeIndex];

                if (beanType instanceof final Class<?> beanClass) {

                    @SuppressWarnings("unchecked")
                    final Class<T> result = (Class<T>) beanClass;
                    return result;
                }

                if (beanType instanceof final TypeVariable<?> typeVariable
                    && genericTypeInfo.containsKey(typeVariable.getName())) {

                    @SuppressWarnings("unchecked")
                    final Class<T> result = (Class<T>) genericTypeInfo
                        .get(typeVariable.getName());
                    return result;
                }
            }

            final Class<T> interfaceClass = getGenericClass(interfaceTypeName,
                typeIndex, genericTypeInfo, interfaceType);

            if (interfaceClass != null) {

                return interfaceClass;
            }
        }

        final Class<T> superClass = getGenericClass(interfaceTypeName,
            typeIndex, genericTypeInfo, clazz.getGenericSuperclass());

        if (superClass != null) {

            return superClass;
        }

        throw new NotFoundException("No such interface "
            + interfaceTypeName
            + " in "
            + clazz.getName()
            + " or can't access generic types.");
    }

    /**
     * Find bean class from interface.
     *
     * @param  <T>               the type of the bean.
     * @param  clazz             the class of the bean.
     * @param  interfaceTypeName the interface type name.
     * @param  typeIndex         index of the type from the interface.
     * @return                   the bean class retrieved from the interface.
     */
    public <T> Class<T> findGenericTypeFromInterface(
        final Class<?> clazz, final String interfaceTypeName,
        final int typeIndex) {

        return findGenericTypeFromInterface(clazz, interfaceTypeName, typeIndex,
            new HashMap<>());
    }

    /**
     * Find bean class from interface.
     *
     * @param  <T>               the type of the bean.
     * @param  clazz             the class of the bean.
     * @param  interfaceTypeName the interface type name.
     * @return                   the bean class retrieved from the interface.
     */
    public <T> Class<T> findGenericTypeFromInterface(
        final Class<?> clazz, final String interfaceTypeName) {

        return findGenericTypeFromInterface(clazz, interfaceTypeName, 0);
    }

    /**
     * Get the actual class from the type.
     * If reflection can't find the actual class, return a raw class or
     * upper-bound or lower-bound class.
     * If the raw class is returned, return also the parameters type.
     *
     * @param  type      The type to get the class from.
     * @param  classInfo The actual class information that can help to find the
     *                   class.
     * @return           The class information of the given type.
     */
    public ClassInfo getClassInfo(
        final Type type, @Nullable final ClassInfo classInfo) {

        final ClassInfo result;

        switch (type) {

            case final Class<?> classType -> result
                = new ClassInfo(classType, new String[] {}, new ClassInfo[] {});

            case final TypeVariable<?> typeVariable -> {

                if (classInfo != null) {

                    for (int i = 0; i < classInfo.parametersType().length;
                        i++) {

                        if (classInfo.parametersTypeName()[i]
                            .equals(typeVariable.getName())) {

                            return classInfo.parametersType()[i];
                        }
                    }
                }
                result = getClassInfo(typeVariable.getBounds()[0], classInfo);
            }
            case final ParameterizedType parameterizedType -> {

                final ClassInfo rawClassInfo
                    = getClassInfo(parameterizedType.getRawType(), classInfo);

                if (classInfo != null
                    && classInfo.clazz() == rawClassInfo.clazz()) {

                    return classInfo;
                }
                final String[] typeNames
                    = Arrays.stream(rawClassInfo.clazz().getTypeParameters())
                        .map(TypeVariable::getName)
                        .toArray(String[]::new);
                final ClassInfo[] parametersType
                    = Arrays.stream(parameterizedType.getActualTypeArguments())
                        .map(t -> getClassInfo(t, rawClassInfo))
                        .toArray(ClassInfo[]::new);

                result = new ClassInfo(rawClassInfo.clazz(),
                    ArrayUtils.addAll(rawClassInfo.parametersTypeName(),
                        typeNames),
                    ArrayUtils.addAll(rawClassInfo.parametersType(),
                        parametersType));
            }
            case final WildcardType wildcardType -> {

                final Type actualType;

                if (wildcardType.getLowerBounds().length > 0) {

                    actualType = wildcardType.getLowerBounds()[0];
                } else {

                    actualType = wildcardType.getUpperBounds()[0];
                }
                result = getClassInfo(actualType, classInfo);
            }
            default -> result = new ClassInfo(Object.class, new String[] {},
                new ClassInfo[] {});
        }
        return result;
    }

    /**
     * Class information with the actual class and parameters type.
     *
     * @param clazz              The class.
     * @param parametersTypeName The parameters type name.
     * @param parametersType     The parameters type.
     */
    @SuppressWarnings("java:S6218")
    public record ClassInfo(
        Class<?> clazz, String[] parametersTypeName,
        ClassInfo[] parametersType) {}
}
