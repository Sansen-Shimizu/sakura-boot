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
import org.sansenshimizu.sakuraboot.specification.presentation.filters.DateFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.TextFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.UUIDFilterImpl;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class CompanyFilter extends AbstractBasicFilter<UUIDFilter> {

    @Serial
    private static final long serialVersionUID = 8075672169790602082L;

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
    private final AddressFilter address;

    @Nullable
    private final TextFilterImpl name;

    @Nullable
    private final DateFilterImpl createdDate;
}
