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

package org.sansenshimizu.sakuraboot.exceptions.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The test class for testing the ExceptionConfiguration class.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
class ExceptionConfigurationTest {

    @Test
    @DisplayName("GIVEN an ExceptionConfiguration class,"
        + " WHEN creating an instance,"
        + " THEN the getter should work")
    final void testExceptionConfigurationClass() {

        // WHEN
        final ExceptionConfiguration exceptionConfiguration
            = new ExceptionConfiguration(true);

        // THEN
        assertThat(exceptionConfiguration.showStackTrace()).isTrue();
    }
}
