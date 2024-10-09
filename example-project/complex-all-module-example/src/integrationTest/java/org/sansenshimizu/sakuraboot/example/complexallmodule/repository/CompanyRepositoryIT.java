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

package org.sansenshimizu.sakuraboot.example.complexallmodule.repository;

import java.util.UUID;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import org.sansenshimizu.sakuraboot.example.complexallmodule.ExampleConfig;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Company;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.CompanyRepository;
import org.sansenshimizu.sakuraboot.example.complexallmodule.util.CompanyITUtil;
import org.sansenshimizu.sakuraboot.test.integration.repository.basic.BasicRepositoryIT;

@Import(ExampleConfig.class)
// Import is only necessary in this example.
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
@DataJpaTest
public class CompanyRepositoryIT implements BasicRepositoryIT<Company, UUID> {

    private final CompanyITUtil util = new CompanyITUtil();

    private final CompanyRepository repository;

    @SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
    @Autowired
    public CompanyRepositoryIT(final CompanyRepository repository) {

        this.repository = repository;
    }
}
