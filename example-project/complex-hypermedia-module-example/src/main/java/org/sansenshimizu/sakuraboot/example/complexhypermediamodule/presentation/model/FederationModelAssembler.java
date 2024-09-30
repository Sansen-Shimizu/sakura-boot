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

package org.sansenshimizu.sakuraboot.example.complexhypermediamodule.presentation.model;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.persistence.Federation;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.presentation.FederationController;
import org.sansenshimizu.sakuraboot.hypermedia.relationship.one.AbstractBasicModelAssembler1Relationship;

@Component
public class FederationModelAssembler
    extends
    AbstractBasicModelAssembler1Relationship<Federation, FederationModel> {

    protected FederationModelAssembler() {

        super(FederationController.class, FederationModel.class, "federations",
            "hobbies");
    }

    @Override
    protected Function<Federation, FederationModel> instantiateModel() {

        return FederationModel::new;
    }
}
