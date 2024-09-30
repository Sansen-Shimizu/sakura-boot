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

import java.io.Serial;

import org.springframework.hateoas.server.core.Relation;

import org.sansenshimizu.sakuraboot.example.complexfulldto.business.dto.EmployeeDto;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModel;

@Relation(collectionRelation = "employees")
public class EmployeeModel extends AbstractBasicModel<EmployeeDto> {

    @Serial
    private static final long serialVersionUID = -2636662559034440172L;

    public EmployeeModel(final EmployeeDto data) {

        super(data);
    }
}
