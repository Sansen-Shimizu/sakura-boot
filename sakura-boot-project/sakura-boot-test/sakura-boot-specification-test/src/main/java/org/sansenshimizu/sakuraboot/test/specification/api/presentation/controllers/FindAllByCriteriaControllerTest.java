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

package org.sansenshimizu.sakuraboot.test.specification.api.presentation.controllers;

import java.io.Serializable;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.specification.api.business.CriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.business.services.FindAllByCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.specification.api.presentation.controllers.FindAllByCriteriaController;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * The base test interface for all find all controllers. This interface provides
 * common tests for testing {@link FindAllByCriteriaController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link FindAllByCriteriaControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link FindAllByCriteriaControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements FindAllByCriteriaControllerTest&lt;YourEntity, //
 *         YourIdType, YourFilter&gt; {
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
 *
 *     &#064;Override
 *     public Class&lt;YourFilter&gt; getExpectedFilterClass() {
 *
 *         return YourFilter.class;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type use in the service layer.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        FindAllByCriteriaController
 * @see        SuperControllerTest
 * @since      0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface FindAllByCriteriaControllerTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperControllerTest<E, I> {

    /**
     * Get the {@link FindAllByCriteriaController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link FindAllByCriteriaController}.
     */
    @Override
    FindAllByCriteriaController<E, I, F> getController();

    /**
     * Get the {@link CriteriaService} to test. Need to be {@link Mock}.
     *
     * @return A {@link CriteriaService}.
     */
    @Override
    FindAllByCriteriaService<E, I, F> getService();

    /**
     * Get the expected class of the filter.
     *
     * @return The expected class of the filter.
     */
    Class<F> getExpectedFilterClass();

    @Test
    @DisplayName("GIVEN a pageable and a filter,"
        + " WHEN finding all by criteria,"
        + " THEN the controller should return a valid response with a page "
        + "filtered")
    default void testFindAllWithFilter() {

        // GIVEN
        final F filter = mock(getExpectedFilterClass());
        final E entityWithId = getUtil().getEntity();
        given(getService().findAllByCriteria(any(), any(Pageable.class)))
            .willReturn(new PageImpl<>(List.of(entityWithId)));

        // WHEN
        final ResponseEntity<Page<DataPresentation<I>>> response
            = getController().findAllByCriteria(filter, Pageable.unpaged());

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
            .containsExactly(entityWithId);
    }

    @Test
    @DisplayName("GIVEN a pageable and no filter,"
        + " WHEN finding all by criteria,"
        + " THEN the controller should return a valid response with a page")
    default void testFindAllWithFilterWithNoFilter() {

        // GIVEN
        final E entityWithId = getUtil().getEntity();
        final E otherEntityWithId = getUtil().getDifferentEntity();
        given(getService().findAllByCriteria(any(), any(Pageable.class)))
            .willReturn(
                new PageImpl<>(List.of(entityWithId, otherEntityWithId)));

        // WHEN
        final ResponseEntity<Page<DataPresentation<I>>> response
            = getController().findAllByCriteria(null, Pageable.unpaged());

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull()
            .containsExactly(entityWithId, otherEntityWithId);
    }
}
