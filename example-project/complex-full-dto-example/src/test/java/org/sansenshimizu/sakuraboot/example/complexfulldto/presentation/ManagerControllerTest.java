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
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.ManagerService;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.ManagerDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Manager;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.ManagerFilter;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.model.ManagerModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexfulldto.util.ManagerTestUtil;
import org.sansenshimizu.sakuraboot.test.bulk.api.presentation.CriteriaBulkControllerTest;
import org.sansenshimizu.sakuraboot.test.hypermedia.api.HypermediaTest;
import org.sansenshimizu.sakuraboot.test.specification.api.presentation.CriteriaControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class ManagerControllerTest
    implements CriteriaControllerTest<Manager, Long, ManagerDto, ManagerFilter>,
    CriteriaBulkControllerTest<Manager, Long, ManagerDto, ManagerFilter>,
    HypermediaTest<ManagerDto, ManagerModelAssembler> {

    private final ManagerTestUtil util = new ManagerTestUtil();

    @InjectMocks
    private ManagerController controller;

    @Mock
    private ManagerService service;

    @Mock
    private ManagerModelAssembler modelAssembler;
}
