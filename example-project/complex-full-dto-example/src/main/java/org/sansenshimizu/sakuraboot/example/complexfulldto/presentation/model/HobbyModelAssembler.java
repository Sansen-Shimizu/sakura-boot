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

package org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.model;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.HobbyDto;
import org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.HobbyController;
import org.sansenshimizu.sakuraboot.hypermedia.relationship.two.AbstractBasicModelAssembler2Relationship;

@Component
public class HobbyModelAssembler
    extends AbstractBasicModelAssembler2Relationship<HobbyDto, HobbyModel> {

    protected HobbyModelAssembler() {

        super(HobbyController.class, HobbyModel.class, "hobbies", "employees",
            "federations");
    }

    @Override
    protected Function<HobbyDto, HobbyModel> instantiateModel() {

        return HobbyModel::new;
    }
}
