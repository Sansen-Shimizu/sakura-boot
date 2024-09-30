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
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.business.BasicService;
import org.sansenshimizu.sakuraboot.basic.api.business.services.FindAllService;
import org.sansenshimizu.sakuraboot.basic.api.presentation.controllers.FindAllController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all find all controllers. This interface provides
 * common tests for testing {@link FindAllController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link FindAllControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements FindAllControllerTest&lt;YourEntity, YourIdType&gt; {
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
 * @see        FindAllController
 * @see        SuperControllerTest
 * @since      0.1.0
 */
public interface FindAllControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends SuperControllerTest<E, I> {

    /**
     * Get the {@link FindAllController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link FindAllController}.
     */
    @Override
    FindAllController<E, I> getController();

    /**
     * Get the {@link BasicService} for test. Need to be {@link Mock}.
     *
     * @return A {@link BasicService}.
     */
    @Override
    FindAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN a pageable request,"
        + " WHEN finding all,"
        + " THEN the controller should return a valid response with the page")
    default void testFindAllWithPageable() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final Pageable pageable = Pageable.ofSize(1);
        given(getService().findAll(any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(entityWithId)));

        // WHEN
        final ResponseEntity<Page<DataPresentation<I>>> response
            = getController().findAll(pageable);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
            .containsExactly(entityWithId);
    }

    @Test
    @DisplayName("GIVEN no pageable request,"
        + " WHEN finding all,"
        + " THEN the controller should return a valid response with the page")
    default void testFindAll() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final E otherEntityWithId = getUtil().getDifferentEntity();
        given(getService().findAll(any(Pageable.class))).willReturn(
            new PageImpl<>(List.of(entityWithId, otherEntityWithId)));

        // WHEN
        final ResponseEntity<Page<DataPresentation<I>>> response
            = getController().findAll(Pageable.unpaged());

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
            .containsExactly(entityWithId, otherEntityWithId);
    }
}
