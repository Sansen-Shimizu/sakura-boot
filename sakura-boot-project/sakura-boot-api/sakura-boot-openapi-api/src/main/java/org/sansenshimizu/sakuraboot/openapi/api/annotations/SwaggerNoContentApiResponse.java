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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

/**
 * Meta-annotation representing the no content API response with HTTP status
 * code 204 (No Content).
 * The method annotated with this meta-annotation will give the Swagger
 * documentation for the API response for HTTP status code 204 (No Content) when
 * the request is successful, but there is no data to return in the response
 * body.
 * <p>
 * <b>Example:</b>
 * </p>
 * <blockquote>
 *
 * <pre>
 *
 * &#064;SwaggerNoContentApiResponse
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
@Operation(
    responses = @ApiResponse(
        responseCode = "204",
        description = "No Content",
        content = @Content))
public @interface SwaggerNoContentApiResponse {

    /**
     * Description of the API response with HTTP status code 204 (No Content).
     *
     * @return The description of the API response.
     */
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";

    /**
     * Description of the API response with HTTP status code 204 (No Content).
     *
     * @return The description of the API response.
     */
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String value() default "";
}
