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

import java.util.UUID;

import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.DepartmentDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.ManagerDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Department;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.DepartmentRepository;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Manager;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.one.AbstractBasicMapper1RelationshipAnyToOne;

@Getter
@Mapper(config = BasicMapper.class)
public abstract class AbstractManagerMapper
    extends AbstractBasicMapper1RelationshipAnyToOne<Manager, ManagerDto,
        Department, DepartmentDto, UUID> {

    @Nullable
    private DepartmentRepository repository;

    @Nullable
    private AbstractDepartmentMapper mapper;

    public Class<UUID> getRelationalIdType() {

        return UUID.class;
    }

    @Autowired
    public void setRepository(final DepartmentRepository repository) {

        this.repository = repository;
    }

    @Autowired
    public void setMapper(@Lazy final AbstractDepartmentMapper mapper) {

        this.mapper = mapper;
    }

    @Override
    @Nullable
    @Mapping(
        target = "department",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    public abstract Manager toEntity(@Nullable ManagerDto dto);

    @Override
    @Nullable
    @Mapping(
        target = "department",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "relationshipId",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithId.class)
    public abstract ManagerDto toDto(@Nullable Manager entity);
}
