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

package org.sansenshimizu.sakuraboot.example.complexallmodule.presentation;

import lombok.Getter;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.EmployeeService;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.EmployeeDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Employee;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.EmployeeFilter;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model.EmployeeModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexallmodule.util.EmployeeTestUtil;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.test.hypermedia.api.HypermediaTest;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.CriteriaControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class EmployeeControllerTest
    implements
    CriteriaControllerTest<Employee, Long, EmployeeDto, EmployeeFilter>,
    HypermediaTest<EmployeeDto, EmployeeModelAssembler> {

    private final EmployeeTestUtil util = new EmployeeTestUtil();

    @InjectMocks
    private EmployeeController controller;

    @Mock
    private EmployeeService service;

    @Mock
    private EmployeeModelAssembler modelAssembler;

    @Override
    public Class<EmployeeFilter> getExpectedFilterClass() {

        return EmployeeFilter.class;
    }

    @Override
    public Hypermedia<EmployeeDto, EmployeeModelAssembler> getHypermedia() {

        return controller;
    }

    @Override
    public Class<EmployeeDto> getExpectedDataClass() {

        return EmployeeDto.class;
    }
}
