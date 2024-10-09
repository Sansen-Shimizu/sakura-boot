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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.basic.persistence.relationship.two.AbstractBasicEntity2RelationshipAnyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Department
    extends AbstractBasicEntity2RelationshipAnyToOne<UUID, Company, Manager> {

    @Serial
    private static final long serialVersionUID = 3483687044259717900L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @Nullable
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    })
    @Nullable
    private Company company;

    @OneToOne(fetch = FetchType.LAZY, cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    })
    @MapsId
    @Nullable
    private Manager manager;

    @Nullable
    private String name;

    @Nullable
    private Boolean bankrupt;

    @Nullable
    private Double income;

    @ElementCollection
    @Nullable
    private Map<String, String> properties;

    @Nullable
    private ZonedDateTime createdDate;

    @Override
    @Nullable
    public Company getRelationship() {

        return getCompany();
    }

    @Override
    @Nullable
    public Manager getSecondRelationship() {

        return getManager();
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("company", getCompany()));
        list.add(Pair.of("manager", getManager()));
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("bankrupt", getBankrupt()));
        list.add(Pair.of("income", getIncome()));
        list.add(Pair.of("properties", getProperties()));
        list.add(Pair.of("createdDate", getCreatedDate()));
        return list;
    }
}
