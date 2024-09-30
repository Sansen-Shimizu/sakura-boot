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

import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Company;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.CompanyRepository;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Department;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.one.AbstractBasicMapper1RelationshipAnyToOne;

@Getter
@Mapper(config = BasicMapper.class)
public abstract class AbstractDepartmentMapper
    extends AbstractBasicMapper1RelationshipAnyToOne<Department, DepartmentDto,
        Company, CompanyDto, Long> {

    @Nullable
    private CompanyRepository repository;

    @Nullable
    private CompanyMapper mapper;

    public Class<Long> getRelationalIdType() {

        return Long.class;
    }

    @Autowired
    public void setRepository(final CompanyRepository repository) {

        this.repository = repository;
    }

    @Autowired
    public void setMapper(final CompanyMapper mapper) {

        this.mapper = mapper;
    }

    @Override
    @Nullable
    @Mapping(
        target = "company",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    public abstract Department toEntity(@Nullable DepartmentDto dto);

    @Override
    @Nullable
    @Mapping(
        target = "company",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "relationshipId",
        source = "entity",
        qualifiedBy = RelationshipFromEntityWithId.class)
    public abstract DepartmentDto toDto(@Nullable Department entity);
}
