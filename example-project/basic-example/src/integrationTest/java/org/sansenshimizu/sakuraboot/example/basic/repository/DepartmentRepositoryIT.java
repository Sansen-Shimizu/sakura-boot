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

package org.sansenshimizu.sakuraboot.example.basic.repository;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import org.sansenshimizu.sakuraboot.example.basic.ExampleConfig;
import org.sansenshimizu.sakuraboot.example.basic.persistence.Department;
import org.sansenshimizu.sakuraboot.example.basic.persistence.DepartmentRepository;
import org.sansenshimizu.sakuraboot.example.basic.util.DepartmentITUtil;
import org.sansenshimizu.sakuraboot.test.integration.repository.basic.BasicRepositoryIT;

@Import(ExampleConfig.class)
// Import is only necessary in this example.
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
@DataJpaTest
public class DepartmentRepositoryIT
    implements BasicRepositoryIT<Department, Long> {

    private final DepartmentITUtil util = new DepartmentITUtil();

    private final DepartmentRepository repository;

    @SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
    @Autowired
    public DepartmentRepositoryIT(final DepartmentRepository repository) {

        this.repository = repository;
    }
}
