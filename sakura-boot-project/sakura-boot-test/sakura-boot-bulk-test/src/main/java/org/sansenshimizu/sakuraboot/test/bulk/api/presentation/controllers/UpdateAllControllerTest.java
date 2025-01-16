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

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.UpdateAllService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.UpdateAllController;
import org.sansenshimizu.sakuraboot.test.BasicDataTestUtil;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all updateAll controllers. This interface
 * provides common tests for testing {@link UpdateAllController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link UpdateAllControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link UpdateAllControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements UpdateAllControllerTest&lt;YourEntity, YourIdType, //
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
 * @see        UpdateAllController
 * @see        SuperControllerTest
 * @since      0.1.2
 */
public interface UpdateAllControllerTest<E extends DataPresentation<I>,
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
     * Get the {@link UpdateAllController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link UpdateAllController}.
     */
    @Override
    UpdateAllController<E, I, D> getController();

    /**
     * Get the {@link UpdateAllService} for test. Need to be {@link Mock}.
     *
     * @return A {@link UpdateAllService}.
     */
    @Override
    UpdateAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN valid entities,"
        + " WHEN updating all,"
        + " THEN the controller should update all and return a valid response "
        + "with the updated objects")
    default void testUpdateAll() {

        // GIVEN
        final List<DataPresentation<I>> datasWithId
            = List.of(getUtil().getData(), getUtil().getDifferentData());
        @SuppressWarnings("unchecked")
        final List<D> castDatasWithId = (List<D>) datasWithId;
        given(getService().updateAll(any())).willReturn(datasWithId);

        // WHEN
        final ResponseEntity<List<?>> response
            = getController().updateAll(castDatasWithId);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .asInstanceOf(
                InstanceOfAssertFactories.iterable(DataPresentation.class))
            .containsAll(datasWithId);
    }
}
