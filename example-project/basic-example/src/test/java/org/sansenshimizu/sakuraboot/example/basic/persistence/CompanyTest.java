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

package org.sansenshimizu.sakuraboot.example.basic.persistence;

import lombok.Getter;

import org.sansenshimizu.sakuraboot.example.basic.util.CompanyTestUtil;
import org.sansenshimizu.sakuraboot.test.basic.persistence.AbstractBasicEntityTest;

@SuppressWarnings("java:S2187")
@Getter
public class CompanyTest extends AbstractBasicEntityTest<Company, Long> {

    private final CompanyTestUtil util = new CompanyTestUtil();
}
