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

package org.sansenshimizu.sakuraboot.example.basic.presentation;

import lombok.Getter;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.example.basic.business.CompanyService;
import org.sansenshimizu.sakuraboot.example.basic.persistence.Company;
import org.sansenshimizu.sakuraboot.example.basic.util.CompanyTestUtil;
import org.sansenshimizu.sakuraboot.test.basic.api.presentation.BasicControllerTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class CompanyControllerTest
    implements BasicControllerTest<Company, Long, Company> {

    private final CompanyTestUtil util = new CompanyTestUtil();

    @InjectMocks
    private CompanyController controller;

    @Mock
    private CompanyService service;
}
