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
import java.util.List;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.UUIDFilter;
import org.sansenshimizu.sakuraboot.specification.presentation.AbstractBasicFilter;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.BooleanFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.DateFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.NumberFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.TextFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.UUIDFilterImpl;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class DepartmentFilter extends AbstractBasicFilter<UUIDFilter> {

    @Serial
    private static final long serialVersionUID = 3483687044259717900L;

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
    private final CompanyFilter company;

    @Nullable
    private final ManagerFilter manager;

    @Nullable
    private final TextFilterImpl name;

    @Nullable
    private final BooleanFilterImpl bankrupt;

    @Nullable
    private final NumberFilterImpl<Double> income;

    @Nullable
    private final DateFilterImpl createdDate;

    @Override
    protected List<Pair<String, Object>> listFieldsForToString(
        final List<Pair<String, Object>> list) {

        super.listFieldsForToString(list);
        list.add(Pair.of("company", getCompany()));
        list.add(Pair.of("manager", getManager()));
        list.add(Pair.of("name", getName()));
        list.add(Pair.of("bankrupt", getBankrupt()));
        list.add(Pair.of("income", getIncome()));
        list.add(Pair.of("createdDate", getCreatedDate()));
        return list;
    }
}
