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
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.one.AbstractBasicDto1RelationshipAnyToOne;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class ManagerDto
    extends AbstractBasicDto1RelationshipAnyToOne<Long, DepartmentDto, Long> {

    @Serial
    private static final long serialVersionUID = -4869852242794893140L;

    @Nullable
    private final Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final DepartmentDto department;

    @JsonProperty("departmentId")
    @Nullable
    private final Long relationshipId;

    @Nullable
    private final String name;

    @Nullable
    private final LocalTime lastLoginTime;

    @Override
    @JsonIgnore
    public DepartmentDto getRelationship() {

        return getDepartment();
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("lastLoginTime", getLastLoginTime()));
        return list;
    }
}
