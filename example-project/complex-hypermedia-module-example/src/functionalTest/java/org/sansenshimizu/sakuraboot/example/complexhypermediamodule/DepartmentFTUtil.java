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

package org.sansenshimizu.sakuraboot.example.complexhypermediamodule;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.configuration.GlobalSpecification;
import org.sansenshimizu.sakuraboot.example.complexhypermediamodule.persistence.Department;
import org.sansenshimizu.sakuraboot.test.functional.hypermedia.HypermediaFTUtil;

@Getter
@Component
public class DepartmentFTUtil implements HypermediaFTUtil<Department, Long> {

    private final GlobalSpecification globalSpecification;

    @Autowired
    public DepartmentFTUtil(final GlobalSpecification globalSpecification) {

        this.globalSpecification = globalSpecification;
    }
}
