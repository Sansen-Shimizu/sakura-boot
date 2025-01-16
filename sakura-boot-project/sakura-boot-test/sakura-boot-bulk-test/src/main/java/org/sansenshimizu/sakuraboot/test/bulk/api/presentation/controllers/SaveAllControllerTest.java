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

package org.sansenshimizu.sakuraboot.test.bulk.api.presentation.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.InstanceOfAssertFactories;
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
import org.sansenshimizu.sakuraboot.bulk.api.business.services.SaveAllService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.SaveAllController;
import org.sansenshimizu.sakuraboot.test.BasicDataTestUtil;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all saveAll controllers. This interface provides
 * common tests for testing {@link SaveAllController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link SaveAllControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link SaveAllControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements SaveAllControllerTest&lt;YourEntity, YourIdType, //
 *         YourDataPresentation&gt; {
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
 * @see        SaveAllController
 * @see        SuperControllerTest
 * @since      0.1.2
 */
public interface SaveAllControllerTest<E extends DataPresentation<I>,
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
     * Get the {@link SaveAllController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link SaveAllController}.
     */
    @Override
    SaveAllController<E, I, D> getController();

    /**
     * Get the {@link SaveAllService} for test. Need to be {@link Mock}.
     *
     * @return A {@link SaveAllService}.
     */
    @Override
    SaveAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN valid entities,"
        + " WHEN saving all,"
        + " THEN the controller should save all and return a valid response "
        + "with the saved objects")
    default void testSaveAll() {

        // GIVEN
        final List<D> datasWithoutId = List.of(getUtil().getDataWithoutId(),
            getUtil().getDataWithoutId());
        final List<DataPresentation<I>> entitiesWithId
            = List.of(getUtil().getData(), getUtil().getDifferentData());
        given(getService().saveAll(any())).willReturn(entitiesWithId);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder
            .setRequestAttributes(new ServletRequestAttributes(request));

        // WHEN
        final ResponseEntity<List<?>> response
            = getController().saveAll(datasWithoutId);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().toSingleValueMap()).isEqualTo(Map
            .of("Location", request.getRequestURL() + "/" + getEntityName()));
        assertThat(response.getBody())
            .asInstanceOf(
                InstanceOfAssertFactories.iterable(DataPresentation.class))
            .containsAll(entitiesWithId);
    }
}
