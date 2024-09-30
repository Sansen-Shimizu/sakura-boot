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

package org.sansenshimizu.sakuraboot.test.hypermedia;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModel;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;
import org.sansenshimizu.sakuraboot.test.BeanTest;

/**
 * The base test abstract class for all Model. This abstract class provides
 * common tests for testing {@link AbstractBasicModel}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete model test class that inherits from
 * {@link AbstractBasicModelTest}, follow these steps:
 * </p>
 * <p>
 * Extends the {@link AbstractBasicModelTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourModelTest //
 *     implements AbstractBasicModelTest&lt;YourModel&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <M> The {@link AbstractBasicModel} type.
 * @author     Malcolm Rozé
 * @see        AbstractBasicModel
 * @see        BeanTest
 * @since      0.1.0
 */
public abstract class AbstractBasicModelTest<M extends AbstractBasicModel<?>>
    implements BeanTest<M> {

    /**
     * The data field name.
     */
    private static final String DATA_FIELD_NAME = "data";

    @SuppressWarnings("java:S2325")
    @BeforeEach
    final void setUp() {

        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder
            .setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Override
    public List<String> includeFieldsGetter() {

        return List.of(DATA_FIELD_NAME);
    }

    @Override
    public List<String> includeFieldsEqAndHash() {

        return List.of(DATA_FIELD_NAME);
    }

    @Override
    public List<String> includeFieldsToString() {

        return List.of(DATA_FIELD_NAME);
    }

    /**
     * Get the entity class.
     *
     * @return The entity class.
     */
    protected Class<M> getModelClass() {

        @SuppressWarnings("unchecked")
        final Class<M> modelClass
            = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        return modelClass;
    }

    @Override
    public M getBean() {

        return BeanCreatorHelper.getBean(getModelClass());
    }

    @Override
    public M getSecondBean() {

        return BeanCreatorHelper.getSecondBean(getModelClass());
    }
}
