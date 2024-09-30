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

package org.sansenshimizu.sakuraboot;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * The base repository interface.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a repository for your {@link DataPresentation} that inherits from
 * {@link SuperRepository}, follow these steps:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public interface YourEntityRepository
 *     extends SuperRepository&lt;YourEntity, YourIdType&gt; {}
 * </pre>
 *
 * </blockquote>
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        JpaRepository
 * @since      0.1.0
 */
@NoRepositoryBean
public interface SuperRepository<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends JpaRepository<E, I> {}
