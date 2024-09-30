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

package org.sansenshimizu.sakuraboot.example.complexfulldto.persistence;

import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.basic.persistence.relationship.two.AbstractBasicEntity2RelationshipAnyToMany;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Hobby
    extends
    AbstractBasicEntity2RelationshipAnyToMany<Long, Employee, Federation> {

    @Serial
    private static final long serialVersionUID = -7056599975876114239L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Nullable
    private Long id;

    @ManyToMany(mappedBy = "hobbies", cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Nullable
    private Set<Employee> employees;

    @ManyToMany(cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinTable(
        name = "hobby_federation_join_table",
        joinColumns = @JoinColumn(name = "hobby_id"),
        inverseJoinColumns = @JoinColumn(name = "federation_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Nullable
    private Set<Federation> federations;

    @Nullable
    private String name;

    @Nullable
    public Set<Employee> getEmployees() {

        if (employees == null) {

            return null;
        }
        return Collections.unmodifiableSet(employees);
    }

    @Override
    @Nullable
    public Set<Employee> getRelationships() {

        return getEmployees();
    }

    @Nullable
    public Set<Federation> getFederations() {

        if (federations == null) {

            return null;
        }
        return Collections.unmodifiableSet(federations);
    }

    @Override
    @Nullable
    public Set<Federation> getSecondRelationships() {

        return getFederations();
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("federations", getFederations()));
        list.add(Pair.of("name", getName()));
        return list;
    }
}
