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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Address;
import org.sansenshimizu.sakuraboot.mapper.dto.AbstractBasicDto;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class CompanyDto extends AbstractBasicDto<UUID> {

    @Serial
    private static final long serialVersionUID = 8075672169790602082L;

    @Nullable
    private final UUID id;

    @Nullable
    private final Address address;

    @Nullable
    private final String name;

    @Nullable
    private final LocalDate createdDate;

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("address", address));
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("createdDate", getCreatedDate()));
        return list;
    }
}
