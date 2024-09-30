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

package org.sansenshimizu.sakuraboot.specification.api.relationship.impl;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.basic.api.relationship.impl.FetchRelationshipRepositoryImpl;
import org.sansenshimizu.sakuraboot.specification.api.relationship.FetchRelationshipSpecificationRepository;

/**
 * The implementation of {@link FetchRelationshipSpecificationRepository}.
 *
 * @param  <E> The entity type extending {@link DataPresentation}.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        FetchRelationshipSpecificationRepository
 * @since      0.1.0
 */
public class FetchRelationshipSpecificationRepositoryImpl<
    E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable>
    extends FetchRelationshipRepositoryImpl<E, I>
    implements FetchRelationshipSpecificationRepository<E, I> {

    @Override
    public Page<I> findAllIds(
        final Pageable pageable, final Specification<E> specification,
        final Class<E> entityType) {

        final CriteriaBuilder criteriaBuilder
            = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> criteriaQuery
            = criteriaBuilder.createTupleQuery();
        final Root<E> root = criteriaQuery.from(entityType);

        final Predicate predicate
            = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        if (predicate != null) {

            criteriaQuery.where(predicate);
        }

        criteriaQuery.orderBy(
            QueryUtils.toOrders(pageable.getSort(), root, criteriaBuilder));
        final TypedQuery<Tuple> typedQuery = entityManager.createQuery(
            criteriaQuery.select(criteriaBuilder.tuple(root.get("id"))));
        typedQuery.setFirstResult(Math.toIntExact(pageable.getOffset()));
        typedQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        final List<I> resultList = typedQuery.getResultList()
            .stream()
            .map(tuple -> (I) tuple.get(0))
            .toList();
        return new PageImpl<>(resultList, pageable,
            getTotalCount(criteriaBuilder, specification, entityType));
    }

    private Long getTotalCount(
        final CriteriaBuilder criteriaBuilder,
        final Specification<E> specification, final Class<E> entityType) {

        final CriteriaQuery<Long> criteriaQuery
            = criteriaBuilder.createQuery(Long.class);
        final Root<E> root = criteriaQuery.from(entityType);
        criteriaQuery.select(criteriaBuilder.count(root));
        final Predicate predicate
            = specification.toPredicate(root, criteriaQuery, criteriaBuilder);

        if (predicate != null) {

            criteriaQuery.where(predicate);
        }
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}
