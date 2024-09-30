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
 * Represents an exception indicating that the requested object wasn't found.
 * Extends the {@link SuperException} class and sets the HTTP status to 404 Not
 * Found.
 *
 * @author Malcolm Rozé
 * @see    SuperException
 * @since  0.1.0
 */
public class NotFoundException extends SuperException {

    @Serial
    private static final long serialVersionUID = -3147994084442650954L;

    /**
     * Default message for not-found exception: "{} not found".
     */
    private static final String MESSAGE_NOT_FOUND = "{} not found";

    /**
     * Default message for not-found exception: "{} not found with ID :".
     */
    private static final String MESSAGE_NOT_FOUND_WITH_ID
        = MESSAGE_NOT_FOUND + " with ID : ";

    /**
     * Constructs a new NotFoundException with the given object name.
     *
     * @param objectName The name of the object that wasn't found.
     */
    public NotFoundException(final String objectName) {

        super(MESSAGE_NOT_FOUND.replace("{}", objectName),
            HttpStatus.NOT_FOUND);
    }

    /**
     * Constructs a new NotFoundException with the given object name and
     * expected ID.
     *
     * @param objectName The name of the object that wasn't found.
     * @param id         The id of the object that wasn't found.
     */
    public NotFoundException(final String objectName, final Object id) {

        super(MESSAGE_NOT_FOUND_WITH_ID.replace("{}", objectName) + id,
            HttpStatus.NOT_FOUND);
    }

    /**
     * Constructs a new NotFoundException with the given object class and
     * expected ID.
     *
     * @param objectClass The class of the object that wasn't found.
     * @param id          The id of the object that wasn't found.
     */
    public NotFoundException(final Class<?> objectClass, final Object id) {

        super(
            MESSAGE_NOT_FOUND_WITH_ID.replace("{}", objectClass.getSimpleName())
                + id,
            HttpStatus.NOT_FOUND);
    }
}
