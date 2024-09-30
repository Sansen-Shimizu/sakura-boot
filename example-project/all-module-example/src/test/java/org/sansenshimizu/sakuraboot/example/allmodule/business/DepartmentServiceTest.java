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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.cache.api.Cacheable;
import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Department;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.DepartmentRepository;
import org.sansenshimizu.sakuraboot.example.allmodule.presentation.DepartmentFilter;
import org.sansenshimizu.sakuraboot.example.allmodule.util.DepartmentTestUtil;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.test.cache.api.CacheableTest;
import org.sansenshimizu.sakuraboot.test.mapper.api.MappableTest;
import org.sansenshimizu.sakuraboot.test.specification.api.business.CriteriaServiceTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class DepartmentServiceTest
    implements CriteriaServiceTest<Department, Long, DepartmentFilter>,
    CacheableTest, MappableTest<Department, DepartmentDto> {

    private final DepartmentTestUtil util = new DepartmentTestUtil();

    @InjectMocks
    private DepartmentService service;

    @Mock
    private DepartmentRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SpecificationBuilder<Department> specificationBuilder;

    @Mock
    private CachingUtil cachingUtil;

    @Mock
    private AbstractDepartmentMapper mapper;

    @Override
    public Class<DepartmentFilter> getExpectedFilterClass() {

        return DepartmentFilter.class;
    }

    @Override
    public Cacheable getCacheable() {

        return service;
    }

    @Override
    public String[] getExpectedCacheNames() {

        return new String[] {
            "Department"
        };
    }

    @Override
    public Mappable<Department, DepartmentDto> getMappable() {

        return service;
    }

    @Override
    public Class<Department> getExpectedEntityClass() {

        return Department.class;
    }

    @Override
    public Class<DepartmentDto> getExpectedDtoClass() {

        return DepartmentDto.class;
    }
}
