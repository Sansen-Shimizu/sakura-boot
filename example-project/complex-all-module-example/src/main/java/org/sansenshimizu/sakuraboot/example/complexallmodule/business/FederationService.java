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

package org.sansenshimizu.sakuraboot.example.complexallmodule.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.sansenshimizu.sakuraboot.cache.api.Cacheable;
import org.sansenshimizu.sakuraboot.cache.api.CachingUtil;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.mapper.AbstractFederationMapper;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.FederationRepository;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.FederationFilter;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.mapper.api.Mappable;
import org.sansenshimizu.sakuraboot.specification.api.business.CriteriaService;
import org.sansenshimizu.sakuraboot.specification.api.business.SpecificationBuilder;

@Service
@Getter
@RequiredArgsConstructor
public class FederationService
    implements CriteriaService<Federation, Long, FederationFilter>, Cacheable,
    Mappable<Federation, FederationDto>, Loggable {

    private final FederationRepository repository;

    private final ObjectMapper objectMapper;

    private final SpecificationBuilder<Federation> specificationBuilder;

    private final CachingUtil cachingUtil;

    private final AbstractFederationMapper mapper;

    @Override
    public Class<Federation> getEntityClass() {

        return Federation.class;
    }

    @Override
    public String[] getCacheNames() {

        return new String[] {
            "Federation"
        };
    }

    @Override
    public Class<FederationDto> getDtoClass() {

        return FederationDto.class;
    }
}
