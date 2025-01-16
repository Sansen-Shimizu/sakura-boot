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
import org.springframework.test.util.ReflectionTestUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.PatchAllService;
import org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers.PatchAllController;
import org.sansenshimizu.sakuraboot.test.BasicDataTestUtil;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * The base test interface for all patchAll controllers. This interface
 * provides common tests for testing {@link PatchAllController}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete controller test class that inherits from
 * {@link PatchAllControllerTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link PatchAllControllerTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements PatchAllControllerTest&lt;YourEntity, YourIdType, //
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
 * @see        PatchAllController
 * @see        SuperControllerTest
 * @since      0.1.2
 */
public interface PatchAllControllerTest<E extends DataPresentation<I>,
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
     * Get the {@link PatchAllController} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link PatchAllController}.
     */
    @Override
    PatchAllController<E, I, D> getController();

    /**
     * Get the {@link PatchAllService} for test. Need to be {@link Mock}.
     *
     * @return A {@link PatchAllService}.
     */
    @Override
    PatchAllService<E, I> getService();

    @Test
    @DisplayName("GIVEN valid partial entities,"
        + " WHEN patching all,"
        + " THEN the controller should patch all and return a valid response "
        + "with the patched objects")
    default void testPatchingAll() {

        // GIVEN
        final List<DataPresentation<I>> datasWithId
            = List.of(getUtil().getData(), getUtil().getDifferentData());
        final D partialData = getUtil().getPartialData();
        final D secondPartialData = getUtil().getPartialData();
        ReflectionTestUtils.setField(secondPartialData, "id", getInvalidId());
        final List<D> partialDatas = List.of(partialData, secondPartialData);
        given(getService().patchAll(any())).willReturn(datasWithId);

        // WHEN
        final ResponseEntity<List<?>> response
            = getController().patchAll(partialDatas);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .asInstanceOf(
                InstanceOfAssertFactories.iterable(DataPresentation.class))
            .containsAll(datasWithId);
    }
}
