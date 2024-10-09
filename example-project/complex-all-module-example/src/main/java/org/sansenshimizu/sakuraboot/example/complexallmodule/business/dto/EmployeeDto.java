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
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.two.AbstractBasicDto2RelationshipAnyToOneAndAnyToMany;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class EmployeeDto
    extends AbstractBasicDto2RelationshipAnyToOneAndAnyToMany<UUID,
        DepartmentDto, UUID, HobbyDto, UUID> {

    @Serial
    private static final long serialVersionUID = -2636662559034440172L;

    @Nullable
    private final UUID id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final DepartmentDto department;

    @JsonProperty("departmentId")
    @Nullable
    private final UUID relationshipId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<HobbyDto> hobbies;

    @JsonProperty("hobbiesId")
    @Nullable
    private final Set<UUID> relationshipsId;

    @Nullable
    private final String name;

    @Nullable
    private final Short age;

    @Nullable
    private final Character gender;

    @JdbcTypeCode(Types.BINARY)
    @Nullable
    private final byte[] image;

    @Nullable
    private final LocalDateTime hiredDate;

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
    @JsonIgnore
    @Nullable
    public Set<HobbyDto> getRelationships() {

        return getHobbies();
    }

    @Override
    @Nullable
    public Set<UUID> getRelationshipsId() {

        if (relationshipsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(relationshipsId);
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("department", getDepartment()));
        list.add(Pair.of("hobbies", getHobbies()));
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("age", getAge()));
        list.add(Pair.of("gender", getGender()));
        list.add(Pair.of("image", getImage()));
        list.add(Pair.of("hiredDate", getHiredDate()));
        return list;
    }
}
