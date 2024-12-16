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

package org.sansenshimizu.sakuraboot.example.complexfulldto.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.mapper.AbstractFederationMapper;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.FederationRepository;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.FederationFilter;
import org.sansenshimizu.sakuraboot.example.complexfulldto.util.FederationTestUtil;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.test.cache.api.CacheableTest;
import org.sansenshimizu.sakuraboot.test.mapper.api.MappableTest;
import org.sansenshimizu.sakuraboot.test.specification.api.business.CriteriaServiceTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class FederationServiceTest
    implements CriteriaServiceTest<Federation, Long, FederationFilter>,
    CacheableTest, MappableTest<Federation, FederationDto> {

    private final FederationTestUtil util = new FederationTestUtil();

    @InjectMocks
    private FederationService service;

    @Mock
    private FederationRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SpecificationBuilder<Federation> specificationBuilder;

    @Mock
    private CachingUtil cachingUtil;

    @Mock
    private AbstractFederationMapper mapper;
}
