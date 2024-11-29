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
import org.sansenshimizu.sakuraboot.test.SuperDataITUtil;

public abstract class AbstractCommonUtil<E extends DataPresentation<UUID>,
    D extends DataPresentation<UUID>> implements SuperDataITUtil<E, UUID, D> {

    @Override
    public UUID getValidId() {

        return UUID.fromString("e07c1515-35d7-4c09-bc15-1535d73c0963");
    }

    @Override
    public UUID getBiggerValidId() {

        return UUID.fromString("4e134d06-eb42-4a5a-934d-06eb421a5aab");
    }

    @Override
    public UUID getInvalidId() {

        return UUID.fromString("777fe735-4d1d-4be1-bfe7-354d1d1be138");
    }

    @Override
    public String getDtoPackageName() {

        return "business.dto";
    }

    @Override
    public String getMapperPackageName() {

        return "business.mapper";
    }
}
