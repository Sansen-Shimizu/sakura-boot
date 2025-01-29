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

package org.sansenshimizu.sakuraboot.exceptions;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.lang.Nullable;

/**
 * Represents an error response class.
 *
 * @param  status           The HTTP status code of the error response.
 * @param  timestamp        The timestamp when the error response was generated.
 * @param  message          The error message in the error response.
 * @param  description      A description or additional details about the error
 *                          in the error response.
 * @param  additionalValues Additional values to be included in the error
 *                          response.
 * @param  stackTrace       The stackTrace of the error.
 * @author                  Malcolm Rozé
 * @since                   0.1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder(toBuilder = true)
public record ErrorResponse(
    int status, @NonNull Instant timestamp, @NonNull String message,
    @NonNull String description,
    @Nullable @JsonAnyGetter Map<String, Object> additionalValues,
    @Nullable String stackTrace) {}
