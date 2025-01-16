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

package org.sansenshimizu.sakuraboot.test.specification.api.business;

import java.io.Serializable;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.specification.api.business.SuperCriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.persistence.CriteriaRepository;
import org.sansenshimizu.sakuraboot.specification.api.presentation.FilterPresentation;
import org.sansenshimizu.sakuraboot.test.SuperServiceTest;
import org.sansenshimizu.sakuraboot.util.ReflectionUtils;

/**
 * The base test interface for all criteria services. This interface provides
 * common tests for testing {@link SuperCriteriaService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link SuperCriteriaServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperCriteriaServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements SuperCriteriaServiceTest&lt;YourEntity, YourIdType&gt; {
 *
 *     private YourUtil util = new YourUtil();
 *
 *     &#064;InjectMocks
 *     private YourService service;
 *
 *     &#064;Mock
 *     private YourRepository repository;
 *
 *     &#064;Override
 *     public YourUtil getUtil() {
 *
 *         return util;
 *     }
 *
 *     &#064;Override
 *     public YourService getService() {
 *
 *         return service;
 *     }
 *
 *     &#064;Override
 *     public YourRepository getRepository() {
 *
 *         return repository;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <F> The {@link FilterPresentation} type.
 * @author     Malcolm Rozé
 * @see        SuperCriteriaService
 * @see        SuperServiceTest
 * @since      0.1.2
 */
@ExtendWith(MockitoExtension.class)
public interface SuperCriteriaServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    F extends FilterPresentation<?>> extends SuperServiceTest<E, I> {

    /**
     * Get the {@link SuperCriteriaService} to test. Need to be
     * {@link InjectMocks}.
     *
     * @return A {@link SuperCriteriaService}.
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    SuperCriteriaService<E, I, F> getService();

    /**
     * Get the {@link CriteriaRepository} for test. Need to be {@link Mock}.
     *
     * @return A {@link CriteriaRepository}.
     */
    @Override
    CriteriaRepository<E, I> getRepository();

    /**
     * Get the {@link SpecificationBuilder} for test. Need to be {@link Mock}.
     *
     * @return A {@link SpecificationBuilder}.
     */
    SpecificationBuilder<E> getSpecificationBuilder();

    /**
     * Get the expected class of the filter.
     *
     * @return The expected class of the filter.
     */
    default Class<F> getExpectedFilterClass() {

        return ReflectionUtils.findGenericTypeFromInterface(getClass(),
            SuperCriteriaServiceTest.class.getTypeName(), 2);
    }
}
