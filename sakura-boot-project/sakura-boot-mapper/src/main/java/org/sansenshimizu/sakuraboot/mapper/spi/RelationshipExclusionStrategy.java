/*
 * Copyright (C) 2023-2025 Malcolm Rozé.
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

package org.sansenshimizu.sakuraboot.mapper.spi;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy;
import org.mapstruct.ap.spi.util.IntrospectorUtils;

/**
 * The RelationshipExclusionStrategy class extends
 * {@link DefaultAccessorNamingStrategy} to exclude the relationships in the
 * mapping from entity to DTO.
 *
 * @author Malcolm Rozé
 * @see    DefaultAccessorNamingStrategy
 * @since  0.1.2
 */
public class RelationshipExclusionStrategy
    extends DefaultAccessorNamingStrategy {

    /**
     * The index of the "get" in the name of the method.
     */
    private static final int GET_END_INDEX = 3;

    @Override
    public boolean isGetterMethod(final ExecutableElement method) {

        if (super.isGetterMethod(method)) {

            for (final Element element: method.getEnclosingElement()
                .getEnclosedElements()) {

                if (element.getSimpleName()
                    .contentEquals(
                        IntrospectorUtils.decapitalize(method.getSimpleName()
                            .toString()
                            .substring(GET_END_INDEX)))) {

                    return element.getAnnotation(OneToOne.class) == null
                        && element.getAnnotation(OneToMany.class) == null
                        && element.getAnnotation(ManyToOne.class) == null
                        && element.getAnnotation(ManyToMany.class) == null;
                }
            }
        }
        return super.isGetterMethod(method);
    }
}
