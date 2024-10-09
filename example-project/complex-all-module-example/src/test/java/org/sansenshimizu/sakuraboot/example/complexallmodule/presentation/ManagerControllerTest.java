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

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.ManagerService;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.ManagerDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Manager;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.ManagerFilter;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model.ManagerModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexallmodule.util.ManagerTestUtil;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.test.hypermedia.api.HypermediaTest;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.CriteriaControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class ManagerControllerTest
    implements CriteriaControllerTest<Manager, UUID, ManagerDto, ManagerFilter>,
    HypermediaTest<ManagerDto, ManagerModelAssembler> {

    private final ManagerTestUtil util = new ManagerTestUtil();

    @InjectMocks
    private ManagerController controller;

    @Mock
    private ManagerService service;

    @Mock
    private ManagerModelAssembler modelAssembler;

    @Override
    public Class<ManagerFilter> getExpectedFilterClass() {

        return ManagerFilter.class;
    }

    @Override
    public Hypermedia<ManagerDto, ManagerModelAssembler> getHypermedia() {

        return controller;
    }

    @Override
    public Class<ManagerDto> getExpectedDataClass() {

        return ManagerDto.class;
    }
}
