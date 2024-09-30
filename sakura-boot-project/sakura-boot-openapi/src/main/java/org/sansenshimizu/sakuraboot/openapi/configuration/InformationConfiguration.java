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

package org.sansenshimizu.sakuraboot.openapi.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * Java class for openapi configuration.
 *
 * @param  name        The name of the application.
 * @param  version     The version of the application.
 * @param  description The description of the application.
 * @param  license     The license used by the application.
 * @param  contact     The contact information for the application.
 * @author             Malcolm Rozé
 * @since              0.1.0
 */
@ConfigurationProperties(prefix = "application.info")
public record InformationConfiguration(
    @Nullable String name, @Nullable String version,
    @Nullable String description, @Nullable License license,
    @Nullable Contact contact) {

    /**
     * Java class for license configuration.
     *
     * @param name The name of the license.
     * @param url  The URL of the license.
     */
    record License(@Nullable String name, @Nullable String url) {}

    /**
     * Java class for contact configuration.
     *
     * @param name  The name of the contact.
     * @param email The email of the contact.
     * @param url   The URL of the contact.
     */
    record Contact(
        @Nullable String name, @Nullable String email, @Nullable String url) {}
}
