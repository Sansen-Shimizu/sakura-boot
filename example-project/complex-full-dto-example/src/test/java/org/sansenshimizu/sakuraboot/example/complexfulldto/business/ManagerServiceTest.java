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

import org.sansenshimizu.sakuraboot.cache.api.Cacheable;
import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.ManagerDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.mapper.AbstractManagerMapper;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Manager;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.ManagerRepository;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter.ManagerFilter;
import org.sansenshimizu.sakuraboot.example.complexfulldto.util.ManagerTestUtil;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;
import org.sansenshimizu.sakuraboot.test.cache.api.CacheableTest;
import org.sansenshimizu.sakuraboot.test.mapper.api.MappableTest;
import org.sansenshimizu.sakuraboot.test.specification.api.business.CriteriaServiceTest;

@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
@Getter
public class ManagerServiceTest
    implements CriteriaServiceTest<Manager, Long, ManagerFilter>, CacheableTest,
    MappableTest<Manager, ManagerDto> {

    private final ManagerTestUtil util = new ManagerTestUtil();

    @InjectMocks
    private ManagerService service;

    @Mock
    private ManagerRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SpecificationBuilder<Manager> specificationBuilder;

    @Mock
    private CachingUtil cachingUtil;

    @Mock
    private AbstractManagerMapper mapper;

    @Override
    public Class<ManagerFilter> getExpectedFilterClass() {

        return ManagerFilter.class;
    }

    @Override
    public Cacheable getCacheable() {

        return service;
    }

    @Override
    public String[] getExpectedCacheNames() {

        return new String[] {
            "Manager"
        };
    }

    @Override
    public Mappable<Manager, ManagerDto> getMappable() {

        return service;
    }

    @Override
    public Class<Manager> getExpectedEntityClass() {

        return Manager.class;
    }

    @Override
    public Class<ManagerDto> getExpectedDtoClass() {

        return ManagerDto.class;
    }
}
