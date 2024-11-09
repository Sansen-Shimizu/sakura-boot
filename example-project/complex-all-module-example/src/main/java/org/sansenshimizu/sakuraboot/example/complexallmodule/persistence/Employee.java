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

package org.sansenshimizu.sakuraboot.example.complexallmodule.persistence;

import java.io.Serial;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.basic.persistence.relationship.two.AbstractBasicEntity2RelationshipAnyToOneAndAnyToMany;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee
    extends AbstractBasicEntity2RelationshipAnyToOneAndAnyToMany<UUID,
        Department, Hobby> {

    @Serial
    private static final long serialVersionUID = -2636662559034440172L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @Nullable
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    })
    @Nullable
    private Department department;

    @ManyToMany(cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    })
    @JoinTable(
        name = "employee_hobby_join_table",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "hobby_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Nullable
    private Set<Hobby> hobbies;

    @Nullable
    private String name;

    @Nullable
    private Short age;

    @Nullable
    private Character gender;

    @JdbcTypeCode(Types.BINARY)
    @Nullable
    private byte[] image;

    @Nullable
    private LocalDateTime hiredDate;

    @Override
    @Nullable
    public Department getRelationship() {

        return getDepartment();
    }

    @Nullable
    public Set<Hobby> getHobbies() {

        if (hobbies == null) {

            return null;
        }
        return Collections.unmodifiableSet(hobbies);
    }

    @Override
    @Nullable
    public Set<Hobby> getRelationships() {

        return getHobbies();
    }
}
