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

package org.sansenshimizu.sakuraboot.openapi.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.sansenshimizu.sakuraboot.exceptions.ErrorResponse;

/**
 * Meta-annotation representing the basic API response with HTTP status codes
 * 400 (Bad Request) and 500 (Internal Server Error).
 * The method annotated with this meta-annotation will give the Swagger
 * documentation for the API response for HTTP status code 400 (Bad Request)
 * when there is an issue with the request, and HTTP status code 500 (Internal
 * Server Error) when an unexpected error occurs during the processing of the
 * request.
 * <p>
 * <b>Example:</b>
 * </p>
 * <blockquote>
 *
 * <pre>
 *
 * &#064;SwaggerBasicApiResponse
 * public ResponseEntity&lt;?&gt; yourCustomMethod() {
 *
 *     // Your code.
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@ApiResponse(
    responseCode = "400",
    description = "Bad Request",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
    responseCode = "500",
    description = "Internal Server Error",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public @interface SwaggerBasicApiResponse {}
