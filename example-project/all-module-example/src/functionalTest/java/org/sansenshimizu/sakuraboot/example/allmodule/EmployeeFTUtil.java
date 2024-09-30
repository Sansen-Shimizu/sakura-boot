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

package org.sansenshimizu.sakuraboot.example.allmodule;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.example.allmodule.business.AbstractEmployeeMapper;
import org.sansenshimizu.sakuraboot.example.allmodule.business.EmployeeDto;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Employee;

@Getter
@Component
public class EmployeeFTUtil extends AbstractCommonUtil<Employee, EmployeeDto> {

    private final CacheManager cacheManager;

    private final AbstractEmployeeMapper mapper;

    @Autowired
    EmployeeFTUtil(
        final CacheManager cacheManager, final AbstractEmployeeMapper mapper) {

        this.cacheManager = cacheManager;
        this.mapper = mapper;
    }

    @Override
    public String getPath() {

        return "api/employees";
    }

    @Override
    public String[] getCacheNames() {

        return new String[] {
            "Employee"
        };
    }

    @Override
    public String entityCollectionName() {

        return "employees";
    }

    @Override
    public String relationshipName() {

        return "api/departments";
    }

    @Override
    public String secondRelationshipName() {

        return "api/hobbies";
    }
}
