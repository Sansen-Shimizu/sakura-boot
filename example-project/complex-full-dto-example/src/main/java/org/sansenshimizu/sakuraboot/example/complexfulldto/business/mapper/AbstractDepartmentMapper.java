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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.DepartmentDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Department;
import org.sansenshimizu.sakuraboot.mapper.api.AbstractBasicMapperForRelationship;
import org.sansenshimizu.sakuraboot.mapper.api.BasicMapper;

@Mapper(config = BasicMapper.class)
public abstract class AbstractDepartmentMapper
    extends AbstractBasicMapperForRelationship<Department, DepartmentDto> {

    @Override
    @Nullable
    @Mapping(target = "manager.department", ignore = true)
    public abstract Department toEntity(@Nullable DepartmentDto dto);

    @Override
    @Nullable
    @Mapping(target = "manager.department", ignore = true)
    public abstract DepartmentDto toDto(@Nullable Department entity);

    @Override
    public boolean useRelationObjectToMapToDto() {

        return true;
    }
}
