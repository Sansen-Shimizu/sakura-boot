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

package org.sansenshimizu.sakuraboot.log.aop;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.log.api.annotations.AfterLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.BeforeLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * The aspect class for logging method inside {@link Loggable} class.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@Aspect
@Component
@Slf4j
public final class LogAspect implements AspectUtil {

    /**
     * The log for the method call.
     */
    private static final String METHOD_CALL_LOG
        = "Method call: {}, with argument: {}. {}";

    /**
     * The log for the method end.
     */
    private static final String METHOD_END_LOG
        = "Method finish: {}, with return: {}. {}";

    private static String logArguments(final Object[] arguments) {

        if (arguments.length == 0) {

            return "";
        }
        return Arrays.stream(arguments)
            .map(ToStringUtils::objectToString)
            .collect(Collectors.joining(", "));
    }

    /**
     * Aspect method that logs the call of another method. This aspect method is
     * call for method annotated with {@link BeforeLogging}.
     *
     * @param joinPoint  The method that will be logged.
     * @param annotation The annotation of type {@link BeforeLogging}.
     */
    @Before(ALL_EXECUTION_POINTCUT + ANNOTATION_POINTCUT)
    public static void beforeLogging(
        final JoinPoint joinPoint, final BeforeLogging annotation) {

        log.atDebug()
            .log(METHOD_CALL_LOG, joinPoint.getSignature(),
                logArguments(joinPoint.getArgs()), annotation.message());
    }

    /**
     * Aspect method that logs the end of another method. This aspect method is
     * call for method annotated with {@link AfterLogging}.
     *
     * @param joinPoint  The method that will be logged.
     * @param annotation The annotation of type {@link AfterLogging}.
     * @param result     The result of the method.
     */
    @AfterReturning(
        value = ALL_EXECUTION_POINTCUT + ANNOTATION_POINTCUT,
        returning = "result")
    public static void afterLogging(
        final JoinPoint joinPoint, final AfterLogging annotation,
        final Object result) {

        log.atDebug()
            .log(METHOD_END_LOG, joinPoint.getSignature(),
                ToStringUtils.objectToString(result), annotation.message());
    }

    /**
     * Aspect method that logs the end of another method when there is an
     * exception.
     * This aspect method is call for method annotated with
     * {@link AfterLogging}.
     *
     * @param joinPoint  The method that will be logged.
     * @param target     The target of type {@link Loggable}.
     * @param annotation The annotation of type {@link AfterLogging}.
     * @param ex         The exception that occurred on the method.
     */
    @AfterThrowing(
        value = ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT,
        throwing = "ex")
    public static void afterLogging(
        final JoinPoint joinPoint, final Loggable target,
        final AfterLogging annotation, final Throwable ex) {

        log.error("Method finish: {}, with exception: {}. {}",
            joinPoint.getSignature(), ex.getMessage(),
            annotation.message() + ". " + ExceptionUtils.getStackTrace(ex));
    }

    /**
     * Aspect method that logs the call and the end of another method. This
     * aspect method is call for method annotated with {@link Logging}.
     *
     * @param  joinPoint  The method that will be logged.
     * @param  target     The target of type {@link Loggable}.
     * @param  annotation The annotation of type {@link Logging}.
     * @return            The result of the join point.
     * @throws Throwable  The exception that occurred on the method.
     */
    @Around(ALL_EXECUTION_POINTCUT + TARGET_POINTCUT + ANNOTATION_POINTCUT)
    public Object logging(
        final ProceedingJoinPoint joinPoint, final Loggable target,
        final Logging annotation)
        throws Throwable {

        log(METHOD_CALL_LOG, log, joinPoint, target, annotation, new Object[] {
            joinPoint.getSignature(), logArguments(joinPoint.getArgs()), ""
        });
        final Object result = joinPoint.proceed();
        log(METHOD_END_LOG, log, joinPoint, target, annotation, new Object[] {
            joinPoint.getSignature(), ToStringUtils.objectToString(result), ""
        });
        return result;
    }
}
