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

package org.sansenshimizu.sakuraboot.example.complexfulldto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;

import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Department;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.DepartmentFilter;
import org.sansenshimizu.sakuraboot.test.functional.bulk.CriteriaBulkFT;
import org.sansenshimizu.sakuraboot.test.functional.specification.CriteriaFT;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class DepartmentFT
    implements CriteriaFT<Department, Long, DepartmentFilter>,
    CriteriaBulkFT<Department, Long> {

    private final DepartmentFTUtil util;

    private final ApplicationContext applicationContext;

    private final ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
    @Autowired
    DepartmentFT(
        final DepartmentFTUtil util,
        final ApplicationContext applicationContext,
        final ObjectMapper objectMapper) {

        this.util = util;
        this.applicationContext = applicationContext;
        this.objectMapper = objectMapper;
    }
}
