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

package org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto;

import java.io.Serial;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.AbstractBasicDto;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class HobbyDto extends AbstractBasicDto<Long> {

    @Serial
    private static final long serialVersionUID = -7056599975876114239L;

    @Nullable
    private final Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<EmployeeDto> employees;

    @Nullable
    private final Set<Long> employeesId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<FederationDto> federations;

    @Nullable
    private final Set<Long> federationsId;

    @Nullable
    private final String name;

    @Nullable
    public Set<EmployeeDto> getEmployees() {

        if (employees == null) {

            return null;
        }
        return Collections.unmodifiableSet(employees);
    }

    @Nullable
    public Set<Long> getEmployeesId() {

        if (employeesId == null) {

            return null;
        }
        return Collections.unmodifiableSet(employeesId);
    }

    @Nullable
    public Set<FederationDto> getFederations() {

        if (federations == null) {

            return null;
        }
        return Collections.unmodifiableSet(federations);
    }

    @Nullable
    public Set<Long> getFederationsId() {

        if (federationsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(federationsId);
    }
}
