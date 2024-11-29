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

import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.example.complexallmodule.business.dto.CompanyDto;
import org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.CompanyController;
import org.sansenshimizu.sakuraboot.hypermedia.AbstractBasicModelAssembler;

@Component
public class CompanyModelAssembler
    extends AbstractBasicModelAssembler<CompanyDto, CompanyModel> {

    protected CompanyModelAssembler(
        final GlobalSpecification globalSpecification) {

        super(CompanyController.class, CompanyModel.class, globalSpecification);
    }

    @Override
    protected Function<CompanyDto, CompanyModel> instantiateModel() {

        return CompanyModel::new;
    }
}
