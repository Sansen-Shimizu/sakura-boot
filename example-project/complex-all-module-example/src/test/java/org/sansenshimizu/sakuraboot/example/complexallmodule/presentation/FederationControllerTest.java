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

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.FederationService;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.FederationFilter;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model.FederationModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexallmodule.util.FederationTestUtil;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.CriteriaBulkControllerTest;
import org.sansenshimizu.sakuraboot.test.hypermedia.api.HypermediaTest;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.CriteriaControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class FederationControllerTest
    implements
    CriteriaControllerTest<Federation, UUID, FederationDto, FederationFilter>,
    CriteriaBulkControllerTest<Federation, UUID, FederationDto,
        FederationFilter>,
    HypermediaTest<FederationDto, FederationModelAssembler> {

    private final FederationTestUtil util = new FederationTestUtil();

    @InjectMocks
    private FederationController controller;

    @Mock
    private FederationService service;

    @Mock
    private FederationModelAssembler modelAssembler;

    @Override
    public Class<FederationFilter> getExpectedFilterClass() {

        return FederationFilter.class;
    }

    @Override
    public Hypermedia<FederationDto, FederationModelAssembler> getHypermedia() {

        return controller;
    }

    @Override
    public Class<FederationDto> getExpectedDataClass() {

        return FederationDto.class;
    }
}
