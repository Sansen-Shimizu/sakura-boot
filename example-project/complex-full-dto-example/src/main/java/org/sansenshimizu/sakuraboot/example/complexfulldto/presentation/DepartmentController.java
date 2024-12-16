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

package org.sansenshimizu.sakuraboot.example.complexfulldto.presentation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.DepartmentService;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.DepartmentDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Department;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.DepartmentFilter;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.model.DepartmentModelAssembler;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.specification.api.presentation.CriteriaController;

@RestController
@RequestMapping("/departments")
@Getter
@RequiredArgsConstructor
public class DepartmentController
    implements
    CriteriaController<Department, Long, DepartmentDto, DepartmentFilter>,
    Hypermedia<DepartmentDto, DepartmentModelAssembler>, Loggable {

    private final DepartmentService service;

    private final DepartmentModelAssembler modelAssembler;
}
