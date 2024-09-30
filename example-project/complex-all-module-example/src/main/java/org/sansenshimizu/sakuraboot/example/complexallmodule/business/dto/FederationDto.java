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
import java.time.OffsetDateTime;
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

import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Address;
import org.sansenshimizu.sakuraboot.mapper.dto.relationship.one.AbstractBasicDto1RelationshipAnyToMany;

@Builder(toBuilder = true)
@Jacksonized
@Getter
public class FederationDto
    extends AbstractBasicDto1RelationshipAnyToMany<Long, HobbyDto, Long> {

    @Serial
    private static final long serialVersionUID = -1856840435186840741L;

    @Nullable
    private final Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Nullable
    private final Set<HobbyDto> hobbies;

    @JsonProperty("hobbiesId")
    @Nullable
    private final Set<Long> relationshipsId;

    @Nullable
    private final Address address;

    @Nullable
    private final String name;

    @Nullable
    private final Integer capacity;

    @Nullable
    private final OffsetDateTime createdDate;

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
    public Set<Long> getRelationshipsId() {

        if (relationshipsId == null) {

            return null;
        }
        return Collections.unmodifiableSet(relationshipsId);
    }

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("address", getAddress()));
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("capacity", getCapacity()));
        list.add(Pair.of("createdDate", getCreatedDate()));
        return list;
    }
}
