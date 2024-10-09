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

package org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.two.AbstractBasicDto2RelationshipAnyToOne;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class DepartmentDto
    extends AbstractBasicDto2RelationshipAnyToOne<UUID, CompanyDto, UUID,
        ManagerDto, UUID> {

    @Serial
    private static final long serialVersionUID = 3483687044259717900L;

    @Nullable
    private final UUID id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final CompanyDto company;

    @JsonProperty("companyId")
    @Nullable
    private final UUID relationshipId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final ManagerDto manager;

    @JsonProperty("managerId")
    @Nullable
    private final UUID secondRelationshipId;

    @Nullable
    private final String name;

    @Nullable
    private final Boolean bankrupt;

    @Nullable
    private final Double income;

    @Nullable
    private final Map<String, String> properties;

    @Nullable
    private final ZonedDateTime createdDate;

    @Override
    @JsonIgnore
    @Nullable
    public CompanyDto getRelationship() {

        return getCompany();
    }

    @Override
    @JsonIgnore
    @Nullable
    public ManagerDto getSecondRelationship() {

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
