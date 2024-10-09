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

package org.sansenshimizu.sakuraboot.example.complexallmodule.util;

import java.util.UUID;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.test.BasicDataTestUtil;

public abstract class AbstractCommonUtil<E extends DataPresentation<UUID>,
    D extends DataPresentation<UUID>> implements BasicDataTestUtil<E, UUID, D> {

    @Override
    public UUID getValidId() {

        return UUID.fromString("47b54ad6-42db-4617-b54a-d642db16173d");
    }

    @Override
    public UUID getBiggerValidId() {

        return UUID.fromString("6a923367-19d7-444d-9233-6719d7c44d09");
    }

    @Override
    public UUID getInvalidId() {

        return UUID.fromString("bb08256d-26b1-4314-8825-6d26b1e31422");
    }
}
