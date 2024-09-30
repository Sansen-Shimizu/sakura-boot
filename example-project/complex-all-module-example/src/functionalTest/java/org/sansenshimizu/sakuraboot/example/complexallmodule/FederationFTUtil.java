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

package org.sansenshimizu.sakuraboot.example.complexallmodule;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.FederationDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.mapper.AbstractFederationMapper;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Federation;

@Getter
@Component
public class FederationFTUtil
    extends AbstractCommonUtil<Federation, FederationDto> {

    private final CacheManager cacheManager;

    private final AbstractFederationMapper mapper;

    @Autowired
    FederationFTUtil(
        final CacheManager cacheManager,
        final AbstractFederationMapper mapper) {

        this.cacheManager = cacheManager;
        this.mapper = mapper;
    }

    @Override
    public String getPath() {

        return "api/federations";
    }

    @Override
    public String[] getCacheNames() {

        return new String[] {
            "Federation"
        };
    }

    @Override
    public String entityCollectionName() {

        return "federations";
    }

    @Override
    public String relationshipName() {

        return "api/hobbies";
    }
}
