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

import lombok.Getter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Department;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.DepartmentRepository;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Employee;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Hobby;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.HobbyRepository;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.two.AbstractBasicMapper2RelationshipAnyToOneAndAnyToMany;

@Getter
@Mapper(config = BasicMapper.class)
public abstract class AbstractEmployeeMapper
    extends AbstractBasicMapper2RelationshipAnyToOneAndAnyToMany<Employee,
        EmployeeDto, Department, DepartmentDto, Long, Hobby, HobbyDto, Long> {

    @Nullable
    private DepartmentRepository repository;

    @Nullable
    private HobbyRepository secondRepository;

    @Nullable
    private AbstractDepartmentMapper mapper;

    @Nullable
    private HobbyMapper secondMapper;

    public Class<Long> getRelationalIdType() {

        return Long.class;
    }

    public Class<Long> getSecondRelationalIdType() {

        return Long.class;
    }

    @Autowired
    public void setRepository(final DepartmentRepository repository) {

        this.repository = repository;
    }

    @Autowired
    public void setSecondRepository(final HobbyRepository secondRepository) {

        this.secondRepository = secondRepository;
    }

    @Autowired
    public void setMapper(final AbstractDepartmentMapper mapper) {

        this.mapper = mapper;
    }

    @Autowired
    public void setSecondMapper(final HobbyMapper secondMapper) {

        this.secondMapper = secondMapper;
    }

    @Override
    @Nullable
    @Mapping(
        target = "department",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    @Mapping(
        target = "hobbies",
        source = "dto",
        qualifiedBy = SecondRelationshipFromDto.class)
    public abstract Employee toEntity(@Nullable EmployeeDto dto);

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
    @Mapping(
        target = "hobbies",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "relationshipsId",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithId.class)
    public abstract EmployeeDto toDto(@Nullable Employee entity);
}
