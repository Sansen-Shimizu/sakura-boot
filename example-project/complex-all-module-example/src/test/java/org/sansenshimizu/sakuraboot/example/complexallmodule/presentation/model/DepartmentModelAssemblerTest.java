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

import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.DepartmentDto;
import org.sansenshimizu.sakuraboot.test.hypermedia.AbstractBasicModelAssemblerTest;

@SuppressWarnings("java:S2187")
public class DepartmentModelAssemblerTest
    extends
    AbstractBasicModelAssemblerTest<DepartmentModelAssembler, DepartmentDto> {

    @Override
    protected String getPath() {

        return "departments";
    }

    @Override
    protected String getRelationshipName() {

        return "companies";
    }

    @Override
    protected String getSecondRelationshipName() {

        return "managers";
    }
}
