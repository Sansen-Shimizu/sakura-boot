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

import java.io.Serial;
import java.util.Objects;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Represents a super exception class. Superclass for custom exceptions,
 * providing common error status handling.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a custom exception that inherits from {@link SuperException},
 * follow these steps:
 * </p>
 * <p>
 * Create a new exception class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourException extends SuperException {
 *
 *     public YourException(String message) {
 *
 *         super(message, HttpStatus.YOUR_STATUS);
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@Getter
public class SuperException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5808008902099217204L;

    /**
     * Message for null status.
     */
    private static final String STATUS_REQUIRED = "status is required.";

    /**
     * The HTTP status code of the exception.
     */
    private final HttpStatusCode status;

    /**
     * Constructs a new SuperException with the given message and HTTP status.
     *
     * @param message The error message.
     * @param status  The HTTP status code.
     */
    public SuperException(final String message, final HttpStatusCode status) {

        super(message);
        this.status = Objects.requireNonNull(status, STATUS_REQUIRED);
    }

    /**
     * Constructs a new SuperException with the given message and HTTP status.
     *
     * @param message The error message.
     * @param status  The HTTP status code.
     * @param cause   The cause of the exception as a Throwable.
     */
    public SuperException(
        final String message, final HttpStatusCode status,
        final Throwable cause) {

        super(message, cause);
        this.status = Objects.requireNonNull(status, STATUS_REQUIRED);
    }
}
