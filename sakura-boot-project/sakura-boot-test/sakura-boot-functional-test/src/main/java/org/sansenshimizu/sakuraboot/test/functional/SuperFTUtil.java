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

package org.sansenshimizu.sakuraboot.test.functional;

import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import org.atteo.evo.inflector.English;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.test.SuperITUtil;

/**
 * The interface for all the utility functional test function. This interface
 * provides common functions for testing basic class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link SuperFTUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperFTUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourFTUtil //
 *     implements SuperFTUtil&lt;YourEntity, YourIdType&gt; {
 *
 *     private final GlobalSpecification globalSpecification;
 *
 *     &#064;Autowired
 *     public YourFTUtil(final GlobalSpecification globalSpecification) {
 *
 *         this.globalSpecification = globalSpecification;
 *     }
 *
 *     &#064;Override
 *     public GlobalSpecification getGlobalSpecification() {
 *
 *         return globalSpecification;
 *     }
 *
 *     &#064;Override
 *     public Optional&lt;YourEntity&gt; createValidationErrorEntity(YourIdType id) {
 *
 *         return YourEntity.builder().id(id).build();
 *         // If your class don't have a builder you can use the constructor
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperITUtil
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SuperFTUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends SuperITUtil<E, I> {

    /**
     * Pattern for camel case.
     */
    Pattern CAMEL_CASE_PATTERN = Pattern.compile(
        "([\\p{Lower}\\d])(\\p{Upper})", Pattern.UNICODE_CHARACTER_CLASS);

    @Override
    @Nullable
    default I getValidId() {

        return null;
    }

    @Override
    @Nullable
    default I getBiggerValidId() {

        return null;
    }

    @Override
    @Nullable
    default I getInvalidId() {

        return null;
    }

    @Override
    GlobalSpecification getGlobalSpecification();

    @Override
    default String getEntityPackageName() {

        return getGlobalSpecification().entityPackage();
    }

    @Override
    default String getServicePackageName() {

        return getGlobalSpecification().servicePackage();
    }

    @Override
    default String getDtoPackageName() {

        return getGlobalSpecification().dtoPackage();
    }

    @Override
    default String getMapperPackageName() {

        return getGlobalSpecification().mapperPackage();
    }

    @Override
    default String getControllerPackageName() {

        return getGlobalSpecification().controllerPackage();
    }

    /**
     * Create a new entity with the given id.
     * Must be an entity with value with validation error.
     * If the entity has no validation return {@code Optional.empty()}.
     *
     * @param  id The id to give to the new entity.
     * @return    The created entity.
     */
    default Optional<E> createValidationErrorEntity(final @Nullable I id) {

        return Optional.empty();
    }

    /**
     * Get the path to use in this test.
     *
     * @return The path.
     */
    default String getPath() {

        return "api/"
            + English.plural(
                CAMEL_CASE_PATTERN.matcher(getEntityClass().getSimpleName())
                    .replaceAll("$1-$2")
                    .toLowerCase(Locale.ENGLISH));
    }
}
