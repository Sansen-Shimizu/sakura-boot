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

package org.sansenshimizu.sakuraboot.example.complexallmodule.presentation.filter;

import java.io.Serial;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.UUIDFilter;
import org.sansenshimizu.sakuraboot.specification.presentation.AbstractBasicFilter;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.CommonFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.DateTimeFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.NumberFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.TextFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.UUIDFilterImpl;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class EmployeeFilter extends AbstractBasicFilter<UUIDFilter> {

    @Serial
    private static final long serialVersionUID = -2636662559034440172L;

    @EqualsAndHashCode.Exclude
    @Nullable
    private final Boolean distinct;

    @EqualsAndHashCode.Exclude
    @Nullable
    private final Boolean inclusive;

    @EqualsAndHashCode.Exclude
    @Nullable
    private final UUIDFilterImpl id;

    @Nullable
    private final DepartmentFilter department;

    @Nullable
    private final HobbyFilter hobbies;

    @Nullable
    private final TextFilterImpl name;

    @Nullable
    private final NumberFilterImpl<Short> age;

    @Nullable
    private final CommonFilterImpl<Character> gender;

    @Nullable
    private final DateTimeFilterImpl hiredDate;
}
