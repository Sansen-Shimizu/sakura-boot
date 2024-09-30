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

package org.sansenshimizu.sakuraboot.test.functional.mapper;

import java.io.Serializable;
import java.util.Objects;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.test.SuperDataITUtil;
import org.sansenshimizu.sakuraboot.test.functional.BasicFTUtil;

/**
 * The interface for all the utility functional test function. This interface
 * provides common functions for testing mapper class.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete util class that inherits from {@link MapperFTUtil},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link MapperFTUtil} interface:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Component
 * public class YourITUtil
 *     implements MapperFTUtil&lt;YourEntity, YourIdType, YourDto&gt; {
 *
 *     private final YourMapper yourMapper;
 *
 *     &#064;Autowired
 *     YourITUtil(final YourMapper yourMapper) {
 *
 *         this.yourMapper = yourMapper;
 *     }
 *
 *     &#064;Override
 *     public Optional&lt;YourEntity&gt; createValidation
 *     ErrorEntity(YourIdType id) {
 *
 *         return YourEntity.builder().id(id).build();
 *         // If your class don't have a builder you can use the constructor
 *     }
 *
 *     &#064;Override
 *     public String getPath() {
 *
 *         return "api/pathName";
 *     }
 *
 *     &#064;Override
 *     public Optional&lt;YourDto&gt; createValidationErrorDto(YourIdType id) {
 *
 *         return YourDto.builder().id(id).build();
 *         // If your class don't have a builder you can use the constructor
 *     }
 *
 *     &#064;Override
 *     public YourMapper getMapper() {
 *
 *         return yourMapper;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <D> The DTO type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @see        BasicFTUtil
 * @since      0.1.0
 */
public interface MapperFTUtil<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>>
    extends BasicFTUtil<E, I>, SuperDataITUtil<E, I, D> {

    /**
     * Get the mapper.
     *
     * @return The mapper.
     */
    BasicMapper<E, D> getMapper();

    /**
     * Converts an entity object to it's corresponding DTO object.
     *
     * @param  entity The {@link DataPresentation} object to convert.
     * @return        The {@link DataPresentation} object representing the
     *                entity.
     */
    default DataPresentation<I> toDto(final DataPresentation<I> entity) {

        if (getEntityClass().isInstance(entity)) {

            @SuppressWarnings("unchecked")
            final E castEntity = (E) entity;
            return Objects.requireNonNull(getMapper().toDto(castEntity));
        }
        return entity;
    }
}
