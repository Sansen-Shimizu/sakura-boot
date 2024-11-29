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
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.mapper.dto.AbstractBasicDto;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class EmployeeDto extends AbstractBasicDto<UUID> {

    @Serial
    private static final long serialVersionUID = -2636662559034440172L;

    @Nullable
    private final UUID id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final DepartmentDto department;

    @Nullable
    private final UUID departmentId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<HobbyDto> hobbies;

    @Nullable
    private final Set<UUID> hobbiesId;

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

    @Nullable
    public Set<HobbyDto> getHobbies() {

        if (hobbies == null) {

            return null;
        }
        return Collections.unmodifiableSet(hobbies);
    }

    @Nullable
    public Set<UUID> getHobbiesId() {

        if (hobbiesId == null) {

            return null;
        }
        return Collections.unmodifiableSet(hobbiesId);
    }
}
