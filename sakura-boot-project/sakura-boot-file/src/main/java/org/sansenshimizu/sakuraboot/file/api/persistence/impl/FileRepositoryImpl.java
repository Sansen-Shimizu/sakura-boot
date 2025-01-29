/*
 * Copyright (C) 2023-2025 Malcolm Rozé.
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

package org.sansenshimizu.sakuraboot.file.api.persistence.impl;

import java.io.Serializable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;

import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.file.api.persistence.FileRepository;

/**
 * The implementation of {@link FileRepository}.
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        FileRepository
 * @since      0.1.2
 */
@Transactional(readOnly = true)
public class FileRepositoryImpl<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    implements FileRepository<E, I> {

    /**
     * The entity manager.
     */
    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public File findFileById(
        final I id, final String fileFieldName, final Class<E> entityType) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<File> query = cb.createQuery(File.class);
        final Root<E> root = query.from(entityType);

        query.select(root.get(fileFieldName))
            .where(cb.equal(root.get("id"), id));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Override
    @Transactional
    public void updateFileById(
        final I id, final String fileFieldName, final Class<E> entityType,
        @Nullable final File file) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaUpdate<E> query = cb.createCriteriaUpdate(entityType);
        final Root<E> root = query.from(entityType);

        query.set(root.get(fileFieldName), file)
            .where(cb.equal(root.get("id"), id));

        entityManager.createQuery(query).executeUpdate();
    }
}
