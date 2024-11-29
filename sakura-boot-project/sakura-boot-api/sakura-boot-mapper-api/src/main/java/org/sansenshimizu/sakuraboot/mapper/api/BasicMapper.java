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

package org.sansenshimizu.sakuraboot.mapper.api;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.DataPresentation;

/**
 * Parent interface for defining a mapper.
 * <p>
 * To create a mapper that implements {@link BasicMapper}, follow these steps:
 * </p>
 * <p>
 * Create a new class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourMapper implements BasicMapper&lt;YourEntity, YourDto&gt; {
 *
 *     &#064;Override
 *     public YourEntity toEntity(final YourDto dto) {
 *
 *         // Use your prefer mapper to map or manually map using the entity
 *         // constructor.
 *         return mapper.toEntity(dto);
 *     }
 *
 *     &#064;Override
 *     public YourDto toDto(final YourEntity entity) {
 *
 *         // Use your prefer mapper to map or manually map using the dto
 *         // constructor.
 *         return mapper.toDto(entity);
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 * <p>
 * Or with MapStruct:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Mapper(config = BasicMapper.class)
 * public interface YourMapper
 *     implements BasicMapper&lt;YourEntity, YourDto&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <D> The DTO type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BasicMapper<E extends DataPresentation<?>,
    D extends DataPresentation<?>> {

    /**
     * Converts a DTO object to it's corresponding entity object.
     *
     * @param  dto The {@link DataPresentation} object to convert.
     * @return     The {@link DataPresentation} object representing the DTO.
     */
    @SuppressWarnings({
        "EmptyMethod", "unused"
    })
    @Nullable
    E toEntity(@Nullable D dto);

    /**
     * Converts an entity object to it's corresponding DTO object.
     *
     * @param  entity The {@link DataPresentation} object to convert.
     * @return        The {@link DataPresentation} object representing the
     *                entity.
     */
    @SuppressWarnings({
        "EmptyMethod", "unused"
    })
    @Nullable
    D toDto(@Nullable E entity);

    /**
     * This method is used to change the mapping relationship from an entity to
     * a DTO.
     * If the DTO must have the relationship represent by the ID this method
     * must return {@code false}.
     * If the Dto must have the relationship represent by the actual entity this
     * method must return {@code true}.
     * Default {@code false}.
     * It is recommended to keep the default {@code false} when using the
     * {@code cache} module because hibernate proxy can't be put in the
     * cache and will be initialized, making more SQL request and slowing down
     * the application.
     *
     * @return {@code false} if the ID represents the relationship,
     *         {@code true} otherwise.
     */
    default boolean useRelationObjectToMapToDto() {

        return false;
    }
}
