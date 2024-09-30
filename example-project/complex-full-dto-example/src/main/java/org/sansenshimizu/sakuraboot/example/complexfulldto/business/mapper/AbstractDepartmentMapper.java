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
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.CompanyDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.DepartmentDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.ManagerDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Company;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.CompanyRepository;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Department;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Manager;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.ManagerRepository;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.one.annotations.RelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromDto;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithEntity;
import org.sansenshimizu.sakuraboot.mapper.api.relationship.two.annotations.SecondRelationshipFromEntityWithId;
import org.sansenshimizu.sakuraboot.mapper.relationship.two.AbstractBasicMapper2RelationshipAnyToOne;

@Getter
@Mapper(config = BasicMapper.class)
public abstract class AbstractDepartmentMapper
    extends AbstractBasicMapper2RelationshipAnyToOne<Department, DepartmentDto,
        Company, CompanyDto, Long, Manager, ManagerDto, Long> {

    @Nullable
    private CompanyRepository repository;

    @Nullable
    private ManagerRepository secondRepository;

    @Nullable
    private CompanyMapper mapper;

    @Nullable
    private AbstractManagerMapper secondMapper;

    @Override
    public boolean useRelationObjectToMapToDto() {

        return true;
    }

    public Class<Long> getRelationalIdType() {

        return Long.class;
    }

    public Class<Long> getSecondRelationalIdType() {

        return Long.class;
    }

    @Autowired
    public void setRepository(final CompanyRepository repository) {

        this.repository = repository;
    }

    @Autowired
    public void setSecondRepository(final ManagerRepository secondRepository) {

        this.secondRepository = secondRepository;
    }

    @Autowired
    public void setMapper(final CompanyMapper mapper) {

        this.mapper = mapper;
    }

    @Autowired
    public void setSecondMapper(final AbstractManagerMapper secondMapper) {

        this.secondMapper = secondMapper;
    }

    @Override
    @Nullable
    @Mapping(
        target = "company",
        source = "dto",
        qualifiedBy = RelationshipFromDto.class)
    @Mapping(
        target = "manager",
        source = "dto",
        qualifiedBy = SecondRelationshipFromDto.class)
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
    @Mapping(
        target = "manager",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithEntity.class)
    @Mapping(
        target = "secondRelationshipId",
        source = "entity",
        qualifiedBy = SecondRelationshipFromEntityWithId.class)
    public abstract DepartmentDto toDto(@Nullable Department entity);
}
