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

package org.sansenshimizu.sakuraboot.bulk.api.persistence.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.bulk.api.persistence.BulkRepository;

/**
 * The implementation of {@link BulkRepository}.
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BulkRepository
 * @since      0.1.2
 */
@Transactional
public class BulkRepositoryImpl<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    implements BulkRepository<E, I> {

    /**
     * The entity manager.
     */
    @PersistenceContext
    protected EntityManager entityManager;

    /**
     * The bulk specification.
     */
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size: 50}")
    private String batchSize;

    @Override
    public List<E> bulkInsert(final Iterable<E> entities) {

        int count = 0;
        final List<E> result = new ArrayList<>();

        for (final E entity: entities) {

            entityManager.persist(entity);
            result.add(entity);
            count++;

            if (count % Integer.parseInt(batchSize) == 0) {

                entityManager.flush();

                if (clearContextAfterFlush()) {

                    entityManager.clear();
                }
            }
        }
        entityManager.flush();

        if (clearContextAfterFlush()) {

            entityManager.clear();
        }

        return result;
    }

    @Override
    public List<E> bulkUpdate(final Iterable<E> entities) {

        int count = 0;
        final List<E> result = new ArrayList<>();

        for (final E entity: entities) {

            result.add(entityManager.merge(entity));
            count++;

            if (count % Integer.parseInt(batchSize) == 0) {

                entityManager.flush();

                if (clearContextAfterFlush()) {

                    entityManager.clear();
                }
            }
        }
        entityManager.flush();

        if (clearContextAfterFlush()) {

            entityManager.clear();
        }

        return result;
    }
}
