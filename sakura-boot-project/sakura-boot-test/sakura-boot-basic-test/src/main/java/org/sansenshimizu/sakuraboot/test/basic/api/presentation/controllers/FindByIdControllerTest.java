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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindByIdService;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.FindByIdController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all find by id controllers. This interface
 * provides common tests for testing {@link FindByIdController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link FindByIdControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindByIdControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements FindByIdControllerTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        FindByIdController
 * @see        SuperControllerTest
 * @since      0.1.0
 */
public interface FindByIdControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperControllerTest<E, I> {

    /**
     * Get the {@link FindByIdController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link FindByIdController}.
     */
    @Override
    FindByIdController<E, I> getController();

    /**
     * Get the {@link FindByIdService} for test. Need to be {@link Mock}.
     *
     * @return A {@link FindByIdService}.
     */
    @Override
    FindByIdService<E, I> getService();

    @Test
    @DisplayName("GIVEN a valid ID,"
        + " WHEN finding by ID,"
        + " THEN the controller should return a valid response with "
        + "the corresponding object")
    default void testFindById() {

        // GIVEN
        final I validId = getValidId();
        final E entityWithId = getUtil().getEntity();
        given(getService().findById(any())).willReturn(entityWithId);

        // WHEN
        final ResponseEntity<?> response = getController().findById(validId);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(entityWithId);
    }
}
