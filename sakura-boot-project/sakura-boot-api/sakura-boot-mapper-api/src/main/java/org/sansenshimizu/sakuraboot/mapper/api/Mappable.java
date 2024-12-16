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

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * Interface for all class that add mapping support needs to implement.
 * <p>
 * To create a class that implements {@link Mappable}, follow these steps:
 * </p>
 * <p>
 * Create a new class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourClass implements Mappable&lt;YourEntity, YourDto&gt; {
 *
 *     private final YourMapper yourMapper;
 *
 *     public YourClass(final YourMapper yourMapper) {
 *
 *         this.yourMapper = yourMapper;
 *     }
 *
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
 * @param  <D> The DTO type extending {@link DataPresentation}.
 * @author     Malcolm Rozé
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface Mappable<E extends DataPresentation<?>,
    D extends DataPresentation<?>> {

    /**
     * The {@link BasicMapper} use to perform the mapping.
     *
     * @return A mapper.
     */
    BasicMapper<E, D> getMapper();

    /**
     * Get the class of the entity used by the {@link BasicMapper}.
     *
     * @return The class of the entity.
     */
    default Class<E> getEntityClassToMap() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            Mappable.class.getTypeName());
    }

    /**
     * Get the class of the DTO used by the {@link BasicMapper}.
     *
     * @return The class of the DTO.
     */
    default Class<D> getDtoClass() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            Mappable.class.getTypeName(), 1);
    }
}
