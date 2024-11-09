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
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.basic.persistence.relationship.one.AbstractBasicEntity1RelationshipAnyToMany;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Federation
    extends AbstractBasicEntity1RelationshipAnyToMany<UUID, Hobby> {

    @Serial
    private static final long serialVersionUID = -1856840435186840741L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @Nullable
    private UUID id;

    @ManyToMany(mappedBy = "federations")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Nullable
    private Set<Hobby> hobbies;

    @Embedded
    @Nullable
    private Address address;

    @Nullable
    private String name;

    @Nullable
    private Integer capacity;

    @Nullable
    private OffsetDateTime createdDate;

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
