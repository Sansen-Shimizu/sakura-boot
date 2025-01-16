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
import java.util.Locale;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.SuperService;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * The super test interface for all controllers.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link SuperControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements SuperControllerTest&lt;YourEntity, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourController controller;
 *
 *     &#064;Mock
 *     private YourService service;
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourController getController() {
 *
 *         return controller;
 *     }
 *
 *     &#064;Override
 *     public YourService getService() {
 *
 *         return service;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type use in the service layer.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        SuperController
 * @see        BasicTest
 * @since      0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface SuperControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicTest<E, I> {

    /**
     * Get the {@link SuperController} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link SuperController}.
     */
    @SuppressWarnings("EmptyMethod")
    SuperController<E, I> getController();

    /**
     * Get the {@link SuperService} for test. Need to be {@link Mock}.
     *
     * @return A {@link SuperService}.
     */
    @SuppressWarnings("EmptyMethod")
    SuperService<E, I> getService();

    /**
     * Get the entity name used to test the header location after saving.
     *
     * @return The entity name.
     */
    default String getEntityName() {

        final Class<E> dataClass = ReflectionUtils.findGenericTypeFromInterface(
            getClass(), SuperControllerTest.class.getTypeName(), 0);
        return English
            .plural(dataClass.getSimpleName().toLowerCase(Locale.ENGLISH));
    }
}
