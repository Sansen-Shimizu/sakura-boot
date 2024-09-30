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

package org.sansenshimizu.sakuraboot.specification.api.persistence;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.persistence.BasicRepository;

/**
 * The base interface for all Spring Data repositories. Adds
 * {@link JpaSpecificationExecutor} via the {@link JpaRepositoryImplementation}
 * implementation, allowing the execution of Specifications based on the JPA
 * criteria API.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a repository for your {@link DataPresentation} with filtering
 * support that inherits from {@link CriteriaRepository}, follow these steps:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourEntityRepository
 *     extends CriteriaRepository&lt;YourEntity, YourIdType&gt; {
 *     // Add custom query methods if needed
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicRepository
 * @see        JpaSpecificationExecutor
 * @since      0.1.0
 */
@NoRepositoryBean
public interface CriteriaRepository<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends BasicRepository<E, I>, JpaRepositoryImplementation<E, I> {}
