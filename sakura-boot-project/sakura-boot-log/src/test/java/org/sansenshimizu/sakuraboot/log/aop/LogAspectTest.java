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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;

import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.log.api.annotations.AfterLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.BeforeLogging;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.test.aop.AspectUtilTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * The aspect test class for logging method inside Loggable class.
 *
 * @author Malcolm Rozé
 * @see    AspectUtilTest
 * @see    LogAspect
 * @since  0.1.0
 */
class LogAspectTest implements AspectUtilTest {

    /**
     * The logback logger use to test the log methods.
     */
    private static final Logger LOG
        = (Logger) LoggerFactory.getLogger(LogAspect.class);

    /**
     * The log for method call.
     */
    private static final String METHOD_CALL_LOG
        = "Method call: %s, with argument: %s. %s";

    /**
     * The log for the method end.
     */
    private static final String METHOD_END_LOG
        = "Method finish: %s, with return: %s. %s";

    /**
     * The log for method ends with exception.
     */
    private static final String METHOD_END_EXCEPTION_LOG
        = "Method finish: %s, with exception: %s. %s";

    /**
     * The additional message pass to test the log.
     */
    private static final String TEST_MESSAGE_VALUE = "message";

    /**
     * The arguments value pass to test the log.
     */
    private static final String TEST_ARGS_VALUE = "10, test value";

    /**
     * The mock {@link ProceedingJoinPoint}.
     */
    @Mock
    @Getter
    private ProceedingJoinPoint joinPoint;

    /**
     * The mock {@link MethodSignature}.
     */
    @Mock
    private MethodSignature signature;

    /**
     * The mock {@link Loggable} target.
     */
    @Mock
    private Loggable target;

    /**
     * The mock {@link BeforeLogging} annotation.
     */
    @Mock
    private BeforeLogging beforeLoggingAnnotation;

    /**
     * The mock {@link AfterLogging} annotation.
     */
    @Mock
    private AfterLogging afterLoggingAnnotation;

    /**
     * The mock {@link Logging} annotation.
     */
    @Mock
    private Logging loggingAnnotation;

    /**
     * The {@link LogAspect} to test.
     */
    @Getter
    private final LogAspect aspect = new LogAspect();

    @Test
    @DisplayName("GIVEN the logging aspect method call,"
        + " WHEN logging before a call,"
        + " THEN the result should be the expected result")
    final void testBeforeLogging() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender = addTestAppender(LOG);
        given(joinPoint.getSignature()).willReturn(signature);
        given(joinPoint.getArgs()).willReturn(getArgs());
        given(beforeLoggingAnnotation.message()).willReturn(TEST_MESSAGE_VALUE);

        // WHEN
        LogAspect.beforeLogging(joinPoint, beforeLoggingAnnotation);

        // THEN
        final String expectedString = String.format(METHOD_CALL_LOG,
            getJoinPoint().getSignature(), TEST_ARGS_VALUE, TEST_MESSAGE_VALUE);
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage,
                ILoggingEvent::getLevel)
            .containsExactly(Tuple.tuple(expectedString, Level.DEBUG));
    }

    @Test
    @DisplayName("GIVEN the logging aspect method call,"
        + " WHEN logging before a call with no argument,"
        + " THEN the result should be the expected result")
    final void testBeforeLoggingNoArgument() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender = addTestAppender(LOG);
        given(joinPoint.getSignature()).willReturn(signature);
        given(joinPoint.getArgs()).willReturn(new Object[] {});
        given(beforeLoggingAnnotation.message()).willReturn(TEST_MESSAGE_VALUE);

        // WHEN
        LogAspect.beforeLogging(joinPoint, beforeLoggingAnnotation);

        // THEN
        final String expectedString = String.format(METHOD_CALL_LOG,
            getJoinPoint().getSignature(), "", TEST_MESSAGE_VALUE);
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage,
                ILoggingEvent::getLevel)
            .containsExactly(Tuple.tuple(expectedString, Level.DEBUG));
    }

    @Test
    @DisplayName("GIVEN the logging aspect method call,"
        + " WHEN logging after a call,"
        + " THEN the result should be the expected result")
    final void testAfterLogging() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender = addTestAppender(LOG);
        given(joinPoint.getSignature()).willReturn(signature);
        given(afterLoggingAnnotation.message()).willReturn(TEST_MESSAGE_VALUE);

        // WHEN
        LogAspect.afterLogging(joinPoint, afterLoggingAnnotation,
            EXPECTED_VALUE);

        // THEN
        final String expectedString = String.format(METHOD_END_LOG,
            getJoinPoint().getSignature(), EXPECTED_VALUE, TEST_MESSAGE_VALUE);
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage,
                ILoggingEvent::getLevel)
            .containsExactly(Tuple.tuple(expectedString, Level.DEBUG));
    }

    @Test
    @DisplayName("GIVEN the logging aspect method call,"
        + " WHEN logging after a call with exception,"
        + " THEN the result should be the expected result")
    final void testAfterExceptionLogging() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender = addTestAppender(LOG);
        given(joinPoint.getSignature()).willReturn(signature);
        given(afterLoggingAnnotation.message()).willReturn(TEST_MESSAGE_VALUE);
        final Throwable throwable = new Throwable(TEST_MESSAGE_VALUE);

        // WHEN
        LogAspect.afterLogging(joinPoint, target, afterLoggingAnnotation,
            throwable);

        // THEN
        final String expectedString = String.format(METHOD_END_EXCEPTION_LOG,
            getJoinPoint().getSignature(), TEST_MESSAGE_VALUE,
            TEST_MESSAGE_VALUE
                + ". "
                + ExceptionUtils.getStackTrace(throwable));
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage,
                ILoggingEvent::getLevel)
            .containsExactly(Tuple.tuple(expectedString, Level.ERROR));
    }

    @Test
    @DisplayName("GIVEN the logging aspect method call,"
        + " WHEN logging before a call,"
        + " THEN the result should be the expected result")
    final void testLogging() throws Throwable {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender = addTestAppender(LOG);
        given(joinPoint.getArgs()).willReturn(getArgs());
        mockJoinPoint(EXPECTED_VALUE);
        mockForLog(() -> {

            // WHEN
            final Object result
                = getAspect().logging(joinPoint, target, loggingAnnotation);

            // THEN
            assertThat(result).isEqualTo(EXPECTED_VALUE);
            final String expectedStringCall
                = String.format(METHOD_CALL_LOG, getJoinPoint().getSignature(),
                    TEST_ARGS_VALUE, TEST_MESSAGE_VALUE);
            final String expectedStringEnd
                = String.format(METHOD_END_LOG, getJoinPoint().getSignature(),
                    EXPECTED_VALUE, TEST_MESSAGE_VALUE);
            assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage,
                    ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple(expectedStringCall, Level.DEBUG),
                    Tuple.tuple(expectedStringEnd, Level.DEBUG));
        }, TEST_MESSAGE_VALUE);
    }
}
