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

import org.sansenshimizu.sakuraboot.bulk.api.presentation.CriteriaBulkController;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.CompanyService;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.CompanyDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Company;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.CompanyFilter;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.model.CompanyModelAssembler;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.specification.api.presentation.CriteriaController;

@RestController
@RequestMapping("/companies")
@Getter
@RequiredArgsConstructor
public class CompanyController
    implements CriteriaController<Company, Long, CompanyDto, CompanyFilter>,
    CriteriaBulkController<Company, Long, CompanyDto, CompanyFilter>,
    Hypermedia<CompanyDto, CompanyModelAssembler>, Loggable {

    private final CompanyService service;

    private final CompanyModelAssembler modelAssembler;
}
