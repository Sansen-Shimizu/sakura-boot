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

package org.sansenshimizu.sakuraboot.example.complexhypermediamodule.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.sansenshimizu.sakuraboot.basic.api.business.BasicService;
import org.sansenshimizu.sakuraboot.basic.api.relationship.annotations.Relationshipable;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.persistence.Manager;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.persistence.ManagerRepository;

@Service
@Relationshipable
@Getter
@RequiredArgsConstructor
public class ManagerService implements BasicService<Manager, Long> {

    private final ManagerRepository repository;

    private final ObjectMapper objectMapper;

    @Override
    public Class<Manager> getEntityClass() {

        return Manager.class;
    }
}
