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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.DeleteAllByCriteriaService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.DeleteAllByCriteriaController;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.SuperCriteriaControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The base test interface for all deleteAll by criteria controllers. This
 * interface provides common tests for testing
 * {@link DeleteAllByCriteriaController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link DeleteAllByCriteriaControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link DeleteAllByCriteriaControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements //
 *     DeleteAllByCriteriaControllerTest&lt;YourEntity, YourIdType, //
 *         YourFilter&gt; {
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
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        DeleteAllByCriteriaController
 * @see        SuperCriteriaControllerTest
 * @since      0.1.2
 */
public interface DeleteAllByCriteriaControllerTest<
    E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>>
    extends SuperCriteriaControllerTest<E, I, F> {

    /**
     * Get the {@link DeleteAllByCriteriaController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link DeleteAllByCriteriaController}.
     */
    @Override
    DeleteAllByCriteriaController<E, I, F> getController();

    /**
     * Get the {@link DeleteAllByCriteriaService} for test. Need to be
     * {@link Mock}.
     *
     * @return A {@link DeleteAllByCriteriaService}.
     */
    @Override
    DeleteAllByCriteriaService<E, I, F> getService();

    @Test
    @DisplayName("GIVEN nothing,"
        + " WHEN deleting all by criteria,"
        + " THEN the controller should return no content")
    default void testDeleteAllByCriteriaWithNoFilter() {

        // WHEN
        final ResponseEntity<Void> response
            = getController().deleteAllByCriteria(null);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(getService(), times(1)).deleteAllByCriteria(null);
    }

    @Test
    @DisplayName("GIVEN a filter,"
        + " WHEN deleting all by criteria,"
        + " THEN the controller should return no content")
    default void testDeleteAllByCriteriaWithFilter() {

        // GIVEN
        final F filter = mock(getExpectedFilterClass());

        // WHEN
        final ResponseEntity<Void> response
            = getController().deleteAllByCriteria(filter);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
        verify(getService(), times(1)).deleteAllByCriteria(filter);
    }
}
