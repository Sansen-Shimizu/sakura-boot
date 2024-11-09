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

package org.sansenshimizu.sakuraboot.example.complexhypermediamodule.persistence;

import java.io.Serial;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.basic.persistence.relationship.one.AbstractBasicEntity1RelationshipAnyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Manager
    extends AbstractBasicEntity1RelationshipAnyToOne<Long, Department> {

    @Serial
    private static final long serialVersionUID = -4869852242794893140L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Nullable
    private Long id;

    @OneToOne(mappedBy = "manager")
    @JsonIgnore
    @Nullable
    private Department department;

    @Nullable
    private String name;

    @Override
    @Nullable
    @JsonIgnore
    public Department getRelationship() {

        return getDepartment();
    }
}
