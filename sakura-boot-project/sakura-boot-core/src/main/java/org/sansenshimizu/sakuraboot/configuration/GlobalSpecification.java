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

package org.sansenshimizu.sakuraboot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Java class for global configuration.
 *
 * @param  entityPackage     The package to find entity class.
 *                           ("persistence" by default)
 * @param  servicePackage    The package to find service class. ("business" by
 *                           default)
 * @param  dtoPackage        The package to find DTO class. ("business" by
 *                           default)
 * @param  mapperPackage     The package to find mapper class. ("business" by
 *                           default)
 * @param  controllerPackage The package to find controller class.
 *                           ("presentation" by default)
 * @author                   Malcolm Rozé
 * @since                    0.1.1
 */
@ConfigurationProperties("sakuraboot")
public record GlobalSpecification(
    @DefaultValue("persistence") String entityPackage,
    @DefaultValue("business") String servicePackage,
    @DefaultValue("business") String dtoPackage,
    @DefaultValue("business") String mapperPackage,
    @DefaultValue("presentation") String controllerPackage) {}
