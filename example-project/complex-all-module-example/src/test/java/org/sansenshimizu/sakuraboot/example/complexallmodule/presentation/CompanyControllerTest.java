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

import java.util.UUID;

import lombok.Getter;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.CompanyService;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.CompanyDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Company;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.CompanyFilter;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model.CompanyModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexallmodule.util.CompanyTestUtil;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.CriteriaBulkControllerTest;
import org.sansenshimizu.sakuraboot.test.file.api.presentation.FileControllerTest;
import org.sansenshimizu.sakuraboot.test.hypermedia.api.HypermediaTest;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.CriteriaControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class CompanyControllerTest
    implements CriteriaControllerTest<Company, UUID, CompanyDto, CompanyFilter>,
    CriteriaBulkControllerTest<Company, UUID, CompanyDto, CompanyFilter>,
    HypermediaTest<CompanyDto, CompanyModelAssembler>,
    FileControllerTest<Company, UUID> {

    private final CompanyTestUtil util = new CompanyTestUtil();

    @InjectMocks
    private CompanyController controller;

    @Mock
    private CompanyService service;

    @Mock
    private CompanyModelAssembler modelAssembler;

    @Override
    public Class<CompanyFilter> getExpectedFilterClass() {

        return CompanyFilter.class;
    }

    @Override
    public Hypermedia<CompanyDto, CompanyModelAssembler> getHypermedia() {

        return controller;
    }

    @Override
    public Class<CompanyDto> getExpectedDataClass() {

        return CompanyDto.class;
    }
}
