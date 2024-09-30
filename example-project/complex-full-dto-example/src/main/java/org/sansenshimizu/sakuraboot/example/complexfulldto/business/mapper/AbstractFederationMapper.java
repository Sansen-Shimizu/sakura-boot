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

package org.sansenshimizu.sakuraboot.example.complexfulldto.business.mapper;

import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.HobbyDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Hobby;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.HobbyRepository;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.one.AbstractBasicMapper1RelationshipAnyToMany;

@Getter
@Mapper(config = BasicMapper.class)
public abstract class AbstractFederationMapper
    extends AbstractBasicMapper1RelationshipAnyToMany<Federation, FederationDto,
        Hobby, HobbyDto, Long> {

    @Nullable
    private HobbyRepository repository;

    @Nullable
    private AbstractHobbyMapper mapper;

    @Override
    public boolean useRelationObjectToMapToDto() {

        return true;
    }

    public Class<Long> getRelationalIdType() {

        return Long.class;
    }

    @Autowired
    public void setRepository(final HobbyRepository repository) {

        this.repository = repository;
    }

    @Autowired
    public void setMapper(@Lazy final AbstractHobbyMapper mapper) {

        this.mapper = mapper;
    }

    @Override
    @Nullable
    @Mapping(
        target = "hobbies",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    public abstract Federation toEntity(@Nullable FederationDto dto);

    @Override
    @Nullable
    @Mapping(target = "hobbies", source = "entity", ignore = true)
    @Mapping(
        target = "relationshipsId",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithId.class)
    public abstract FederationDto toDto(@Nullable Federation entity);
}
