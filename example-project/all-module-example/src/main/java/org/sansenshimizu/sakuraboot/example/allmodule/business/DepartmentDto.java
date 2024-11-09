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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.relationship.one.AbstractBasicDto1RelationshipAnyToOne;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class DepartmentDto
    extends AbstractBasicDto1RelationshipAnyToOne<Long, CompanyDto, Long> {

    @Serial
    private static final long serialVersionUID = 3483687044259717900L;

    @Nullable
    private final Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final CompanyDto company;

    @JsonProperty("companyId")
    @Nullable
    private final Long relationshipId;

    @Nullable
    private final String name;

    @Override
    @JsonIgnore
    public CompanyDto getRelationship() {

        return getCompany();
    }
}
