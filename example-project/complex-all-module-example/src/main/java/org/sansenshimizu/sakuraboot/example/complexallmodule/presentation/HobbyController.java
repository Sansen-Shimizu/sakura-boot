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

package org.sansenshimizu.sakuraboot.example.complexallmodule.presentation;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.HobbyService;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.HobbyDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.persistence.Hobby;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter.HobbyFilter;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model.HobbyModelAssembler;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.specification.api.presentation.CriteriaController;

@RestController
@RequestMapping("/hobbies")
@Getter
@RequiredArgsConstructor
public class HobbyController
    implements CriteriaController<Hobby, UUID, HobbyDto, HobbyFilter>,
    Hypermedia<HobbyDto, HobbyModelAssembler>, Loggable {

    private final HobbyService service;

    private final HobbyModelAssembler modelAssembler;
}
