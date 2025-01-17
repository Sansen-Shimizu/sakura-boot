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

package org.sansenshimizu.sakuraboot.example.basic.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.sansenshimizu.sakuraboot.basic.api.business.BasicService;
import org.sansenshimizu.sakuraboot.example.basic.persistence.Company;
import org.sansenshimizu.sakuraboot.example.basic.persistence.CompanyRepository;

@Service
@Getter
@RequiredArgsConstructor
public class CompanyService implements BasicService<Company, Long> {

    private final CompanyRepository repository;

    private final ObjectMapper objectMapper;
}
