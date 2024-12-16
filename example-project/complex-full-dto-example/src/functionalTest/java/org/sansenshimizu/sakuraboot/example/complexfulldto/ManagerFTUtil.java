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

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.ManagerDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.business.mapper.AbstractManagerMapper;
import org.sansenshimizu.sakuraboot.example.complexfulldto.persistence.Manager;

@Getter
@Component
public class ManagerFTUtil extends AbstractCommonUtil<Manager, ManagerDto> {

    private static final String TEST = "test";

    private static final String TEST2 = "test2";

    private final CacheManager cacheManager;

    private final AbstractManagerMapper mapper;

    private final GlobalSpecification globalSpecification;

    @Autowired
    ManagerFTUtil(
        final CacheManager cacheManager, final AbstractManagerMapper mapper,
        final GlobalSpecification globalSpecification) {

        this.cacheManager = cacheManager;
        this.mapper = mapper;
        this.globalSpecification = globalSpecification;
    }

    @Override
    public Manager getEntityWithoutId() {

        return Manager.builder().name(TEST).build();
    }

    @Override
    public ManagerDto getDataWithoutId() {

        return ManagerDto.builder().name(TEST).build();
    }

    @Override
    public ManagerDto getDifferentData() {

        return ManagerDto.builder().id(getInvalidId()).name(TEST2).build();
    }
}
