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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.LazyInitializationException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.lang.Nullable;

/**
 * Util class for toString methods.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@UtilityClass
public class ToStringUtils {

    /**
     * The maximum element of a container to show.
     */
    private final int BIG_CONTAINER_SIZE = 3;

    /**
     * The ellipse to show if there is too much element.
     */
    private final String BIG_CONTAINER_ELLIPSE = "...";

    /**
     * This method help print nice toString for an object and remove the null
     * fields. If a field is a container and is bigger than three elements, it
     * will print an ellipse.
     *
     * @param  className The name of the class to print.
     * @param  fields    The fields of the class to print.
     * @return           A pretty print toString.
     */
    public String toString(
        final String className, final Collection<Pair<String, Object>> fields) {

        return toString(className, fields, BIG_CONTAINER_SIZE);
    }

    /**
     * This method help print nice toString for an object and remove the null
     * fields.
     *
     * @param  className        The name of the class to print.
     * @param  fields           The fields of the object to print.
     * @param  maxContainerSize The maximum number of elements to print for a
     *                          container. (-1 for printing all the elements)
     * @return                  A pretty print toString.
     */
    public String toString(
        final String className, final Collection<Pair<String, Object>> fields,
        final int maxContainerSize) {

        final Stream<Pair<String, Object>> stream = fields.stream()
            .filter(pair -> pair.getRight() != null
                && isNotEmptyCollection(pair.getRight())
                && isNotEmptyArray(pair.getRight()));
        return className + formatFields(stream, maxContainerSize);
    }

    private boolean isNotEmptyCollection(@Nullable final Object object) {

        return !(object instanceof final Collection<?> collection
            && !(collection instanceof PersistentCollection<?>))
            || !collection.isEmpty();
    }

    private boolean isNotEmptyArray(@Nullable final Object object) {

        return !(object instanceof final Object[] array) || array.length != 0;
    }

    /**
     * This method help print nice toString for an object and keep the null
     * fields. If a field is a container and is bigger than three elements, it
     * will print an ellipse.
     *
     * @param  className The name of the class to print.
     * @param  fields    The fields of the class to print.
     * @return           A pretty print toString.
     */
    public String toStringPrintNullFields(
        final String className, final Collection<Pair<String, Object>> fields) {

        return toStringPrintNullFields(className, fields, BIG_CONTAINER_SIZE);
    }

    /**
     * This method help print nice toString for an object and keep the null
     * fields.
     *
     * @param  className        The name of the class to print.
     * @param  fields           The fields of the object to print.
     * @param  maxContainerSize The maximum number of elements to print for a
     *                          container. (-1 for printing all the elements)
     * @return                  A pretty print toString.
     */
    public String toStringPrintNullFields(
        final String className, final Collection<Pair<String, Object>> fields,
        final int maxContainerSize) {

        return className + formatFields(fields.stream(), maxContainerSize);
    }

    private String formatFields(
        final Stream<Pair<String, Object>> fields, final int maxContainerSize) {

        return fields
            .map(pair -> pair.getLeft()
                + "="
                + objectToString(pair.getRight(), maxContainerSize))
            .collect(Collectors.joining(", ", "{", "}"));
    }

    /**
     * Method to print the toString of an object, including container. If a
     * field is a container and is bigger than three elements, it will print an
     * ellipse.
     *
     * @param  object The object to be convert toString.
     * @return        The toString of the given object.
     */
    public String objectToString(@Nullable final Object object) {

        return objectToString(object, BIG_CONTAINER_SIZE);
    }

    /**
     * Method to print the toString of an object, including container. If a
     * field is a container and is bigger than three elements, it will print an
     * ellipse.
     *
     * @param  object           The object to be convert toString.
     * @param  maxContainerSize The maximum number of elements to print for a
     *                          container. (-1 for printing all the elements)
     * @return                  The toString of the given object.
     */
    public String objectToString(
        @Nullable final Object object, final int maxContainerSize) {

        return switch (object) {

            case final Object[] array -> arrayToString(array, maxContainerSize);
            case final Collection<?> collection
                -> collectionToString(collection, maxContainerSize);
            case null, default -> objectToStringAux(object);
        };
    }

    private
        String arrayToString(final Object[] array, final int maxContainerSize) {

        return formatContainer(Arrays.stream(array), maxContainerSize,
            array.length);
    }

    private String collectionToString(
        final Collection<?> collection, final int maxContainerSize) {

        if (collection instanceof final PersistentCollection<
            ?> persistentCollection && !persistentCollection.wasInitialized()) {

            return persistentCollection.render();
        }
        return formatContainer(collection.stream(), maxContainerSize,
            collection.size());
    }

    private String formatContainer(
        final Stream<?> container, final int maxContainerSize, final int size) {

        final String suffix;

        if (size > maxContainerSize) {

            suffix = ", " + BIG_CONTAINER_ELLIPSE + "]";
        } else {

            suffix = "]";
        }

        return container.limit(maxContainerSize)
            .map(ToStringUtils::objectToString)
            .collect(Collectors.joining(", ", "[", suffix));
    }

    private String objectToStringAux(@Nullable final Object object) {

        return switch (object) {

            case final HibernateProxy proxy -> {

                try {

                    yield proxy.getHibernateLazyInitializer()
                        .getImplementationClass()
                        .getSimpleName()
                        + "Proxy{id="
                        + proxy.getHibernateLazyInitializer()
                            .getIdentifier()
                            .toString()
                        + "}";
                } catch (final LazyInitializationException e) {

                    yield "UninitializedProxy";
                }
            }
            case null, default -> {

                if (object instanceof final PersistentCollection<
                    ?> persistentCollection
                    && !persistentCollection.wasInitialized()) {

                    yield persistentCollection.render();
                }
                yield Objects.requireNonNullElse(object, "null").toString();
            }
        };
    }

    /**
     * Helper method to get the list of fields for toString. Only get the fields
     * present in the class and not in the superclass.
     *
     * @param  object         The object to get the list of fields.
     * @param  list           The list of fields to print.
     * @param  excludedFields The list of fields to exclude
     * @return                The list of fields for toString.
     */
    public List<Pair<String, Object>> getListFieldsForToString(
        final Object object, final List<Pair<String, Object>> list,
        final Collection<String> excludedFields) {

        list.addAll(Arrays.stream(object.getClass().getDeclaredFields())
            .filter(field -> !excludedFields.contains(field.getName()))
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .filter(RelationshipUtils::isNotRelationWithMappedBy)
            .map((final Field field) -> {

                try {

                    field.setAccessible(true);
                    return Pair.of(field.getName(), field.get(object));
                } catch (final IllegalAccessException e) {

                    throw new RuntimeException(e);
                }
            })
            .toList());
        return list;
    }
}
