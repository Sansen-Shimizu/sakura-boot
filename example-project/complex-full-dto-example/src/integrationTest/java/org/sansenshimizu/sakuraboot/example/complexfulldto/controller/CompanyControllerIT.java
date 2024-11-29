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

package org.sansenshimizu.sakuraboot.example.complexfulldto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.MockMvc;

import org.sansenshimizu.sakuraboot.configuration.GlobalConfiguration;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.CompanyService;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.CompanyDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Company;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.CompanyController;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.CompanyFilter;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.model.CompanyModelAssembler;
import org.sansenshimizu.sakuraboot.example.complexfulldto.util.CompanyITUtil;
import org.sansenshimizu.sakuraboot.hypermedia.aop.HypermediaAspect;
import org.sansenshimizu.sakuraboot.test.integration.controller.hypermedia.HypermediaIT;
import org.sansenshimizu.sakuraboot.test.integration.controller.specification.CriteriaControllerIT;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
@WebMvcTest(CompanyController.class)
@Import({
    CompanyModelAssembler.class, AopAutoConfiguration.class,
    HypermediaAspect.class, GlobalConfiguration.class
})
public class CompanyControllerIT
    implements CriteriaControllerIT<Company, Long, CompanyDto, CompanyFilter>,
    HypermediaIT<Company, Long, CompanyDto> {

    private final CompanyITUtil util = new CompanyITUtil();

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @MockBean
    @Nullable
    private CompanyService service;

    @SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
    @Autowired
    CompanyControllerIT(
        final MockMvc mockMvc, final ObjectMapper objectMapper) {

        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getBasePath() {

        return "companies";
    }
}
