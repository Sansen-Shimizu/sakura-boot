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

import org.springframework.http.HttpStatus;

/**
 * Represents an exception indicating a bad request from the client. Extends the
 * {@link SuperException} class and sets the HTTP status to 400 Bad Request.
 *
 * @author Malcolm Rozé
 * @see    SuperException
 * @since  0.1.0
 */
public class BadRequestException extends SuperException {

    @Serial
    private static final long serialVersionUID = -6096149982608034144L;

    /**
     * Constructs a new BadRequestException with the given message.
     *
     * @param message The error message.
     */
    public BadRequestException(final String message) {

        super(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Constructs a new BadRequestException with the given message.
     *
     * @param message The error message.
     * @param cause   The cause of the exception as a Throwable.
     */
    public BadRequestException(final String message, final Throwable cause) {

        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
