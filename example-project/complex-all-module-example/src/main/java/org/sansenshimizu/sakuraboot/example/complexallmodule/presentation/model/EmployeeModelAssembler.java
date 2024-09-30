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

package org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.model;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.EmployeeDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.EmployeeController;
import org.sansenshimizu.sakuraboot.hypermedia.relationship.two.AbstractBasicModelAssembler2Relationship;

@Component
public class EmployeeModelAssembler
    extends
    AbstractBasicModelAssembler2Relationship<EmployeeDto, EmployeeModel> {

    protected EmployeeModelAssembler() {

        super(EmployeeController.class, EmployeeModel.class, "employees",
            "departments", "hobbies");
    }

    @Override
    protected Function<EmployeeDto, EmployeeModel> instantiateModel() {

        return EmployeeModel::new;
    }
}
