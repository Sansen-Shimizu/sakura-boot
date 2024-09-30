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

package org.sansenshimizu.sakuraboot.example.complexallmodule;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.functional.cache.CachingFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.hypermedia.HypermediaFTUtil;
import org.sansenshimizu.sakuraboot.test.functional.mapper.MapperFTUtil;

public abstract class AbstractCommonUtil<E extends DataPresentation<Long>,
    D extends DataPresentation<Long>>
    implements CachingFTUtil<E, Long>, MapperFTUtil<E, Long, D>,
    HypermediaFTUtil<E, Long> {}
