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
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.two.AbstractBasicDto2RelationshipAnyToMany;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class HobbyDto
    extends AbstractBasicDto2RelationshipAnyToMany<Long, EmployeeDto, Long,
        FederationDto, Long> {

    @Serial
    private static final long serialVersionUID = -7056599975876114239L;

    @Nullable
    private final Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<EmployeeDto> employees;

    @JsonProperty("employeesId")
    @Nullable
    private final Set<Long> relationshipsId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<FederationDto> federations;

    @JsonProperty("federationsId")
    @Nullable
    private final Set<Long> secondRelationshipsId;

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
    public Set<Long> getRelationshipsId() {

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
    public Set<Long> getSecondRelationshipsId() {

        if (secondRelationshipsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(secondRelationshipsId);
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("federations", getFederations()));
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("participationRate", getParticipationRate()));
        list.add(Pair.of("lastUpdated", getLastUpdated()));
        return list;
    }
}
