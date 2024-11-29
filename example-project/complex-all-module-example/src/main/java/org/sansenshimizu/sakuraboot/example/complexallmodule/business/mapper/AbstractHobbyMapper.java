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

package org.sansenshimizu.sakuraboot.example.complexallmodule.business.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.HobbyDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Hobby;
import org.sansenshimizu.sakuraboot.mapper.api.AbstractBasicMapperForRelationship;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;

@Mapper(config = BasicMapper.class)
public abstract class AbstractHobbyMapper
    extends AbstractBasicMapperForRelationship<Hobby, HobbyDto> {

    @Override
    @Nullable
    @Mapping(target = "employees", ignore = true)
    public abstract Hobby toEntity(@Nullable HobbyDto dto);

    @Mapping(target = "hobbies", ignore = true)
    public abstract
        Federation federationDtoToFederation(FederationDto federationDto);

    @Override
    @Nullable
    @Mapping(target = "employees", ignore = true)
    public abstract HobbyDto toDto(@Nullable Hobby entity);

    @Mapping(target = "hobbies", ignore = true)
    public abstract
        FederationDto federationToFederationDto(Federation federation);
}
