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

package org.sansenshimizu.sakuraboot.test;

import java.io.Serializable;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperRepository;
import org.sansenshimizu.sakuraboot.SuperService;

/**
 * The base test interface for all services. This interface provides common
 * tests for testing {@link SuperService}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete service test class that inherits from
 * {@link SuperServiceTest}, follow these steps:
 * </p>
 * <p>
 * Implements the {@link SuperServiceTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourServiceTest
 *     implements BasicServiceTest&lt;YourEntity, YourIdType&gt; {
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
 * @author     Malcolm Rozé
 * @see        SuperService
 * @see        BasicTest
 * @since      0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface SuperServiceTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicTest<E, I> {

    /**
     * Get the {@link SuperService} to test. Need to be {@link InjectMocks}.
     *
     * @return A {@link SuperService}.
     */
    @SuppressWarnings("EmptyMethod")
    SuperService<E, I> getService();

    /**
     * Get the {@link SuperRepository} for test. Need to be {@link Mock}.
     *
     * @return A {@link SuperRepository}.
     */
    @SuppressWarnings("EmptyMethod")
    SuperRepository<E, I> getRepository();
}
