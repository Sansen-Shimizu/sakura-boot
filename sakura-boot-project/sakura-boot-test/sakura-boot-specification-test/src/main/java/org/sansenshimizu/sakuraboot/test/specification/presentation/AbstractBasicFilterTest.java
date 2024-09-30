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

package org.sansenshimizu.sakuraboot.test.specification.presentation;

import java.lang.reflect.ParameterizedType;

import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.presentation.AbstractBasicFilter;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.FilterPresentationTest;

/**
 * The base test interface for all filterPresentation. This interface provides
 * common tests for testing {@link FilterPresentation}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete filterPresentation test class that inherits from
 * {@link FilterPresentationTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FilterPresentationTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourFilterTest //
 *     implements FilterPresentationTest&lt;YourFilter&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <F> The {@link AbstractBasicFilter} type.
 * @author     Malcolm Rozé
 * @see        AbstractBasicFilter
 * @see        FilterPresentationTest
 * @since      0.1.0
 */
public abstract class AbstractBasicFilterTest<F extends AbstractBasicFilter<?>>
    implements FilterPresentationTest<F> {

    @Override
    public boolean toStringIncludeNullValue() {

        return false;
    }

    /**
     * Get the filter class.
     *
     * @return The filter class.
     */
    protected Class<F> getFilterClass() {

        @SuppressWarnings("unchecked")
        final Class<F> castClass
            = (Class<F>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        return castClass;
    }

    @Override
    public F getBean() {

        return BeanCreatorHelper.getBean(getFilterClass());
    }

    @Override
    public F getSecondBean() {

        return BeanCreatorHelper.getSecondBean(getFilterClass());
    }
}
