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

package org.sansenshimizu.sakuraboot.test.hypermedia.api;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PagedResourcesAssembler;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModel;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModelAssembler;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.test.SuperControllerTest;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The base test interface for all Hypermedia classes. This interface provides
 * common tests for testing {@link Hypermedia}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link HypermediaTest},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link HypermediaTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourHypermediaClassTest //
 *     implements HypermediaTest&lt;YourModel&gt; {
 *
 *     &#064;InjectMocks
 *     private YourHypermediaClass yourHypermediaClass;
 *
 *     &#064;Mock
 *     private YourModelAssembler modelAssembler;
 *
 *     &#064;Override
 *     public YourHypermediaClass getHypermedia() {
 *
 *         return yourHypermediaClass;
 *     }
 *
 *     &#064;Override
 *     public YourModelAssembler getModelAssembler() {
 *
 *         return modelAssembler;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <D> The {@link DataPresentation} type.
 * @param  <M> The model type extending {@link AbstractBasicModel}.
 * @author     Malcolm Rozé
 * @see        Hypermedia
 * @since      0.1.0
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
@ExtendWith(MockitoExtension.class)
public interface HypermediaTest<D extends DataPresentation<?>,
    M extends AbstractBasicModelAssembler<D, ?>> {

    /**
     * Get the class to test, that implements {@link Hypermedia}.
     *
     * @return A {@link Hypermedia}.
     */
    default Hypermedia<D, M> getHypermedia() {

        if (SuperControllerTest.class.isAssignableFrom(getClass())) {

            // noinspection RedundantClassCall
            @SuppressWarnings("unchecked")
            final Hypermedia<D, M> controller = (Hypermedia<D,
                M>) SuperControllerTest.class.cast(this).getController();
            return controller;
        }
        throw new IllegalStateException(
            "HypermediaTest controller must also implement SuperControllerTest "
                + "or need to override this method.");
    }

    /**
     * Get the expected data class uses in test.
     * Need to be the same data class
     * from the class that implements {@link Hypermedia}.
     *
     * @return A DataPresentation class.
     */
    default Class<D> getExpectedDataClass() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            HypermediaTest.class.getTypeName());
    }

    /**
     * Get the {@link AbstractBasicModelAssembler} for test. Need to be
     * {@link Mock}.
     *
     * @return A {@link AbstractBasicModelAssembler}.
     */
    M getModelAssembler();

    @Test
    @DisplayName("GIVEN a Hypermedia,"
        + " WHEN getting the data class,"
        + " THEN the correct data class should be returned")
    default void testGetDataClass() {

        // GIVEN
        final Class<D> expectedDataClass = getExpectedDataClass();

        // WHEN
        final Class<D> dataClass = getHypermedia().getDataClass();

        // THEN
        assertThat(dataClass).isEqualTo(expectedDataClass);
    }

    @Test
    @DisplayName("GIVEN a Hypermedia,"
        + " WHEN getting the model assembler,"
        + " THEN the model assembler shouldn't be null")
    default void testGetModelAssembler() {

        // GIVEN
        final M expectedModelAssembler = getModelAssembler();

        // WHEN
        final M modelAssembler = getHypermedia().getModelAssembler();

        // THEN
        assertThat(modelAssembler).isEqualTo(expectedModelAssembler);
    }

    @Test
    @DisplayName("GIVEN a Hypermedia,"
        + " WHEN getting the paged resources' assembler,"
        + " THEN the paged resources assembler shouldn't be null")
    default void testGetPagedResourcesAssembler() {

        // WHEN
        final Map<String, PagedResourcesAssembler<D>> pagedResourcesAssembler
            = getHypermedia().getPagedResourcesAssembler();

        // THEN
        assertThat(pagedResourcesAssembler).isNotNull()
            .isNotEmpty()
            .allSatisfy((
                final String name,
                final PagedResourcesAssembler<D> assembler) -> {

                assertThat(name).isNotNull();
                assertThat(assembler).isNotNull();
            });
    }
}
