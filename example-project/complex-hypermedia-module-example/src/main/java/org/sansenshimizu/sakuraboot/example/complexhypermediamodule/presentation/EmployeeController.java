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

package org.sansenshimizu.sakuraboot.example.complexhypermediamodule.presentation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.sansenshimizu.sakuraboot.basic.api.presentation.BasicController;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.business.EmployeeService;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.persistence.Employee;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.presentation.model.EmployeeModelAssembler;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;

@RestController
@RequestMapping("/employees")
@Getter
@RequiredArgsConstructor
public class EmployeeController
    implements BasicController<Employee, Long, Employee>,
    Hypermedia<Employee, EmployeeModelAssembler> {

    private final EmployeeService service;

    private final EmployeeModelAssembler modelAssembler;
}
