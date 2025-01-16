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

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.HobbyService;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.HobbyDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Hobby;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.HobbyFilter;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model.HobbyModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexallmodule.util.HobbyTestUtil;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.CriteriaBulkControllerTest;
import org.sansenshimizu.sakuraboot.test.hypermedia.api.HypermediaTest;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.CriteriaControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class HobbyControllerTest
    implements CriteriaControllerTest<Hobby, UUID, HobbyDto, HobbyFilter>,
    CriteriaBulkControllerTest<Hobby, UUID, HobbyDto, HobbyFilter>,
    HypermediaTest<HobbyDto, HobbyModelAssembler> {

    private final HobbyTestUtil util = new HobbyTestUtil();

    @InjectMocks
    private HobbyController controller;

    @Mock
    private HobbyService service;

    @Mock
    private HobbyModelAssembler modelAssembler;

    @Override
    public Class<HobbyFilter> getExpectedFilterClass() {

        return HobbyFilter.class;
    }

    @Override
    public Hypermedia<HobbyDto, HobbyModelAssembler> getHypermedia() {

        return controller;
    }

    @Override
    public Class<HobbyDto> getExpectedDataClass() {

        return HobbyDto.class;
    }
}
