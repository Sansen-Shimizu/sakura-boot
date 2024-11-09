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

package org.sansenshimizu.sakuraboot.example.allmodule.business;

import java.io.Serial;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.two.AbstractBasicDto2RelationshipAnyToOneAndAnyToMany;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class EmployeeDto
    extends AbstractBasicDto2RelationshipAnyToOneAndAnyToMany<Long,
        DepartmentDto, Long, HobbyDto, Long> {

    @Serial
    private static final long serialVersionUID = -2636662559034440172L;

    @Nullable
    private final Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final DepartmentDto department;

    @JsonProperty("departmentId")
    @Nullable
    private final Long relationshipId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<HobbyDto> hobbies;

    @JsonProperty("hobbiesId")
    @Nullable
    private final Set<Long> relationshipsId;

    @Nullable
    private final String name;

    @Override
    @JsonIgnore
    @Nullable
    public DepartmentDto getRelationship() {

        return getDepartment();
    }

    @Nullable
    public Set<HobbyDto> getHobbies() {

        if (hobbies == null) {

            return null;
        }
        return Collections.unmodifiableSet(hobbies);
    }

    @Override
    @Nullable
    @JsonIgnore
    public Set<HobbyDto> getRelationships() {

        return getHobbies();
    }

    @Override
    @Nullable
    public Set<Long> getRelationshipsId() {

        if (relationshipsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(relationshipsId);
    }
}
