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
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.two.AbstractBasicDto2RelationshipAnyToMany;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class HobbyDto
    extends AbstractBasicDto2RelationshipAnyToMany<UUID, EmployeeDto, UUID,
        FederationDto, UUID> {

    @Serial
    private static final long serialVersionUID = -7056599975876114239L;

    @Nullable
    private final UUID id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<EmployeeDto> employees;

    @JsonProperty("employeesId")
    @Nullable
    private final Set<UUID> relationshipsId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<FederationDto> federations;

    @JsonProperty("federationsId")
    @Nullable
    private final Set<UUID> secondRelationshipsId;

    @Nullable
    private final String name;

    @Nullable
    private final Float participationRate;

    @Nullable
    private final Instant lastUpdated;

    @Nullable
    public Set<EmployeeDto> getEmployees() {

        if (employees == null) {

            return null;
        }
        return Collections.unmodifiableSet(employees);
    }

    @Override
    @JsonIgnore
    @Nullable
    public Set<EmployeeDto> getRelationships() {

        return getEmployees();
    }

    @Override
    @Nullable
    public Set<UUID> getRelationshipsId() {

        if (relationshipsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(relationshipsId);
    }

    @Nullable
    public Set<FederationDto> getFederations() {

        if (federations == null) {

            return null;
        }
        return Collections.unmodifiableSet(federations);
    }

    @Override
    @JsonIgnore
    @Nullable
    public Set<FederationDto> getSecondRelationships() {

        return getFederations();
    }

    @Override
    @Nullable
    public Set<UUID> getSecondRelationshipsId() {

        if (secondRelationshipsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(secondRelationshipsId);
    }
}
