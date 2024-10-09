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

import java.util.List;
import java.util.UUID;

import org.sansenshimizu.sakuraboot.specification.api.presentation.filters.UUIDFilter;

/**
 * The test class for the UUID filter that extends
 * {@link AbstractCommonFilterTest}.
 *
 * @author Malcolm Rozé
 * @see    UUIDFilterImpl
 * @see    AbstractCommonFilterTest
 * @since  0.1.1
 */
@SuppressWarnings({
    "java:S2187", "JUnitTestCaseWithNoTests"
})
class UUIDFilterTest extends AbstractCommonFilterTest<UUID> {

    /**
     * The UUID String value to test.
     */
    private static final String UUID_STRING_VALUE
        = "00000000-0000-0000-0000-000000000000";

    /**
     * The UUID value to test.
     */
    private static final UUID UUID_VALUE = UUID.fromString(UUID_STRING_VALUE);

    @SuppressWarnings("SuspiciousGetterSetter")
    @Override
    public UUID getEqualValue() {

        return UUID_VALUE;
    }

    @Override
    public List<UUID> getInValue() {

        return List.of(UUID_VALUE);
    }

    @SuppressWarnings({
        "SuspiciousGetterSetter", "java:S4144"
    })
    @Override
    public UUID getNotEqualValue() {

        return UUID_VALUE;
    }

    @Override
    public List<UUID> getNotInValue() {

        return List.of(UUID_VALUE);
    }

    @Override
    public boolean isNullValue() {

        return true;
    }

    @Override
    public UUIDFilter getBean() {

        return new UUIDFilterImpl(UUID_STRING_VALUE, List.of(UUID_STRING_VALUE),
            UUID_STRING_VALUE, List.of(UUID_STRING_VALUE), isNullValue());
    }

    @Override
    public UUIDFilter getSecondBean() {

        return getBean();
    }
}
