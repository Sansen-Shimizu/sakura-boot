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

package org.sansenshimizu.sakuraboot.specification.presentation.filters;

import java.io.Serial;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.UUIDFilter;

/**
 * A filter for UUID values.
 *
 * @author Malcolm Rozé
 * @see    CommonFilterImpl
 * @see    UUIDFilter
 * @since  0.1.1
 */
@SuperBuilder(toBuilder = true, builderMethodName = "UUIDFilterBuilder")
@Getter
@EqualsAndHashCode(callSuper = true)
public class UUIDFilterImpl extends CommonFilterImpl<UUID>
    implements UUIDFilter {

    @Serial
    private static final long serialVersionUID = 4979147384246089572L;

    /**
     * This is a constructor for the UUIDFilter class.
     *
     * @param equal    Filters the data to include only values that are equal to
     *                 this value.
     * @param in       Filters the data to include only values that are present
     *                 in this list.
     * @param notEqual Filters the data to exclude values that are equal to this
     *                 value.
     * @param notIn    Filters the data to exclude values that are present in
     *                 this list.
     * @param isNull   Filters the data to include only values that are null (or
     *                 not null if {@code false}).
     */
    public UUIDFilterImpl(
        @Nullable final String equal, @Nullable final List<String> in,
        @Nullable final String notEqual, @Nullable final List<String> notIn,
        @Nullable final Boolean isNull) {

        this(stringToUUID(equal), stringToUUID(in), stringToUUID(notEqual),
            stringToUUID(notIn), isNull);
    }

    private UUIDFilterImpl(
        @Nullable final UUID equal, @Nullable final List<UUID> in,
        @Nullable final UUID notEqual, @Nullable final List<UUID> notIn,
        @Nullable final Boolean isNull) {

        super(equal, in, notEqual, notIn, isNull);
    }

    @Nullable
    private static UUID stringToUUID(@Nullable final String source) {

        if (StringUtils.hasText(source)) {

            try {

                return UUID.fromString(source.trim());
            } catch (final IllegalArgumentException ignored) {

                return null;
            }
        }
        return null;
    }

    @Nullable
    private static
        List<UUID> stringToUUID(@Nullable final List<String> source) {

        if (source != null) {

            return source.stream()
                .map(UUIDFilterImpl::stringToUUID)
                .filter(Objects::nonNull)
                .toList();
        }
        return null;
    }
}
