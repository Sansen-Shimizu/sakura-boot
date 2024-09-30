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
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.Employee;
import org.sansenshimizu.sakuraboot.example.allmodule.persistence.EmployeeRepository;
import org.sansenshimizu.sakuraboot.example.allmodule.presentation.EmployeeFilter;
import org.sansenshimizu.sakuraboot.example.allmodule.util.EmployeeTestUtil;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.test.cache.api.CacheableTest;
import org.sansenshimizu.sakuraboot.test.mapper.api.MappableTest;
import org.sansenshimizu.sakuraboot.test.specification.api.business.CriteriaServiceTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class EmployeeServiceTest
    implements CriteriaServiceTest<Employee, Long, EmployeeFilter>,
    CacheableTest, MappableTest<Employee, EmployeeDto> {

    private final EmployeeTestUtil util = new EmployeeTestUtil();

    @InjectMocks
    private EmployeeService service;

    @Mock
    private EmployeeRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SpecificationBuilder<Employee> specificationBuilder;

    @Mock
    private CachingUtil cachingUtil;

    @Mock
    private AbstractEmployeeMapper mapper;

    @Override
    public Class<EmployeeFilter> getExpectedFilterClass() {

        return EmployeeFilter.class;
    }

    @Override
    public Cacheable getCacheable() {

        return service;
    }

    @Override
    public String[] getExpectedCacheNames() {

        return new String[] {
            "Employee"
        };
    }

    @Override
    public Mappable<Employee, EmployeeDto> getMappable() {

        return service;
    }

    @Override
    public Class<Employee> getExpectedEntityClass() {

        return Employee.class;
    }

    @Override
    public Class<EmployeeDto> getExpectedDtoClass() {

        return EmployeeDto.class;
    }
}
