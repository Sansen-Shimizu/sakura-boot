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

package org.sansenshimizu.sakuraboot.example.complexfulldto.presentation.filter;

import java.io.Serial;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.NumberFilter;
import org.sansenshimizu.sakuraboot.specification.presentation.AbstractBasicFilter;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.NumberFilterImpl;
import org.sansenshimizu.sakuraboot.specification.presentation.filters.TextFilterImpl;

@Builder(toBuilder = true)
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
public class HobbyFilter extends AbstractBasicFilter<NumberFilter<Long>> {

    @Serial
    private static final long serialVersionUID = -7056599975876114239L;

    @EqualsAndHashCode.Exclude
    @Nullable
    private final Boolean distinct;

    @EqualsAndHashCode.Exclude
    @Nullable
    private final Boolean inclusive;

    @EqualsAndHashCode.Exclude
    @Nullable
    private final NumberFilterImpl<Long> id;

    @Nullable
    private final FederationFilter federations;

    @Nullable
    private final TextFilterImpl name;
}
