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

package org.sansenshimizu.sakuraboot.test.basic.api.presentation.controllers;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.BasicService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.SaveService;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.SaveController;
import org.sansenshimizu.sakuraboot.test.BasicDataTestUtil;
import org.sansenshimizu.sakuraboot.test.BeanCreatorHelper;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all save controllers. This interface provides
 * common tests for testing {@link SaveController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link SaveControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link SaveControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements
 *     SaveControllerTest&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
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
 * @param  <D> The {@link DataPresentation} type use to map the given JSON to an
 *             entity or DTO.
 * @author     Malcolm Rozé
 * @see        SaveController
 * @see        SuperControllerTest
 * @since      0.1.0
 */
public interface SaveControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends SuperControllerTest<E, I> {

    /**
     * Return a util class of type {@link BasicDataTestUtil}.
     *
     * @return A util class for testing.
     */
    @Override
    BasicDataTestUtil<E, I, D> getUtil();

    /**
     * Get the {@link SaveController} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link SaveController}.
     */
    @Override
    SaveController<E, I, D> getController();

    /**
     * Get the {@link BasicService} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicService}.
     */
    @Override
    SaveService<E, I> getService();

    /**
     * Get the entity name used to test the header location after saving.
     *
     * @return The entity name.
     */
    default String getEntityName() {

        final Class<D> dataClass = BeanCreatorHelper.findBeanClassFromInterface(
            getClass(), SaveControllerTest.class.getTypeName(), 0);
        return English
            .plural(dataClass.getSimpleName().toLowerCase(Locale.ENGLISH));
    }

    @Test
    @DisplayName("GIVEN a valid entity,"
        + " WHEN saving,"
        + " THEN the controller should save and return a valid response with "
        + "the saved object")
    default void testSave() {

        // GIVEN
        final D dataWithoutId = getUtil().getDataWithoutId();
        final D entityWithId = getUtil().getData();
        given(getService().save(any())).willReturn(entityWithId);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder
            .setRequestAttributes(new ServletRequestAttributes(request));

        // WHEN
        final ResponseEntity<DataPresentation<I>> response
            = getController().save(dataWithoutId);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().toSingleValueMap())
            .isEqualTo(Map.of("Location",
                request.getRequestURL()
                    + "/"
                    + getEntityName()
                    + "/"
                    + entityWithId.getId()));
        assertThat(response.getBody()).isEqualTo(entityWithId);
    }
}
