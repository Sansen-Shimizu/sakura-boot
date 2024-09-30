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

import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.EmployeeDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.HobbyDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Employee;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.EmployeeRepository;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.FederationRepository;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Hobby;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.two.AbstractBasicMapper2RelationshipAnyToMany;

@Getter
@Mapper(config = BasicMapper.class)
public abstract class AbstractHobbyMapper
    extends AbstractBasicMapper2RelationshipAnyToMany<Hobby, HobbyDto, Employee,
        EmployeeDto, Long, Federation, FederationDto, Long> {

    @Nullable
    private EmployeeRepository repository;

    @Nullable
    private FederationRepository secondRepository;

    @Nullable
    private AbstractEmployeeMapper mapper;

    @Nullable
    private AbstractFederationMapper secondMapper;

    public Class<Long> getRelationalIdType() {

        return Long.class;
    }

    public Class<Long> getSecondRelationalIdType() {

        return Long.class;
    }

    @Autowired
    public void setRepository(final EmployeeRepository repository) {

        this.repository = repository;
    }

    @Autowired
    public
        void setSecondRepository(final FederationRepository secondRepository) {

        this.secondRepository = secondRepository;
    }

    @Autowired
    public void setMapper(@Lazy final AbstractEmployeeMapper mapper) {

        this.mapper = mapper;
    }

    @Autowired
    public void setSecondMapper(final AbstractFederationMapper secondMapper) {

        this.secondMapper = secondMapper;
    }

    @Override
    @Nullable
    @Mapping(
        target = "employees",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    @Mapping(
        target = "federations",
        source = "dto",
        qualifiedBy = SecondRelationshipFromDto.class)
    public abstract Hobby toEntity(@Nullable HobbyDto dto);

    @Override
    @Nullable
    @Mapping(
        target = "employees",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "relationshipsId",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithId.class)
    @Mapping(
        target = "federations",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "secondRelationshipsId",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithId.class)
    public abstract HobbyDto toDto(@Nullable Hobby entity);
}
