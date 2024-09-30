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

package org.sansenshimizu.sakuraboot.log.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.sansenshimizu.sakuraboot.log.api.Loggable;

/**
 * Parent annotation used on meta-annotation to log the call and the end of
 * the aspect method.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoggingAspect {

    /**
     * A default message to add to the log.
     *
     * @return A message to add to the log.
     */
    String message() default "";

    /**
     * A boolean to activate the logging if the target instance is not of
     * type {@link Loggable}.
     * {@code false} by default.
     *
     * @return A boolean to activate or not the logging.
     */
    boolean activateLogging() default false;
}
