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

package org.sansenshimizu.sakuraboot.test.aop;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;

import org.sansenshimizu.sakuraboot.aop.AspectUtil;
import org.sansenshimizu.sakuraboot.log.api.Loggable;
import org.sansenshimizu.sakuraboot.log.api.annotations.LoggingAspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

/**
 * The base test interface for all aspect classes. This interface provides
 * common tests for testing {@link AspectUtil}.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a concrete test class that inherits from {@link AspectUtilTest},
 * follow these steps:
 * </p>
 * <p>
 * Implements the {@link AspectUtilTest} class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * public class YourAspectTest implements AspectUtilTest {
 *
 *     &#064;Mock
 *     private ProceedingJoinPoint joinPoint;
 *
 *     private YourAspect yourAspect = new YourAspect();
 *
 *     &#064;Override
 *     public ProceedingJoinPoint getJoinPoint() {
 *
 *         return joinPoint;
 *     }
 *
 *     &#064;Override
 *     public YourAspect getAspect() {
 *
 *         return yourAspect;
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @see    AspectUtil
 * @since  0.1.0
 */
@ExtendWith(MockitoExtension.class)
public interface AspectUtilTest {

    /**
     * The String use to test the method call log.
     */
    String METHOD_CALL_LOG = "Method call : {}, with : {} annotation. {}";

    /**
     * The expected value use in test.
     */
    int EXPECTED_VALUE = 10;

    /**
     * The SPeL expression use in test.
     */
    String SPEL_EXPRESSION = "#param1";

    /**
     * The pattern use to replace the parameter style in a log message.
     */
    Pattern LOG_PARAMETER_PATTERN = Pattern.compile("\\{}");

    /**
     * The expected error message for bad annotation on the log.
     */
    String BAD_ANNOTATION_ERROR_MESSAGE_STRING
        = "The logging annotation must have the attributes: "
            + "activateLogging and message.";

    /**
     * The expected error exception message for bad annotation on the log.
     */
    String BAD_ANNOTATION_ERROR_EXCEPTION_STRING
        = "With exception : java.lang.IllegalArgumentException";

    /**
     * The logback logger use to test the log methods.
     *
     * @return The logger.
     */
    default Logger getLogger() {

        return (Logger) LoggerFactory.getLogger(AspectUtilTest.class);
    }

    /**
     * The arguments pass to function to test.
     *
     * @return An array of Object.
     */
    default Object[] getArgs() {

        return new Object[] {
            EXPECTED_VALUE, "test value"
        };
    }

    /**
     * The arguments names pass to function to test.
     *
     * @return An array of String.
     */
    default String[] getParameterName() {

        return new String[] {
            "param1", "param2"
        };
    }

    /**
     * Get the aspect to test, that implements {@link AspectUtil}. Don't need to
     * be {@link InjectMocks}.
     *
     * @return An {@link AspectUtil}.
     */
    AspectUtil getAspect();

    /**
     * Get the {@link ProceedingJoinPoint} for test. Need to be {@link Mock}.
     *
     * @return A {@link ProceedingJoinPoint}.
     */
    ProceedingJoinPoint getJoinPoint();

    /**
     * The mock behavior for the join point.
     *
     * @param returnValue The returnValue parameter is the value that will be
     *                    returned when the join point proceeds.
     */
    default void mockJoinPoint(final Object returnValue) throws Throwable {

        given(getJoinPoint().proceed()).willReturn(returnValue);
    }

    /**
     * The mock behavior for the join point with arguments.
     *
     * @param returnValue The returnValue parameter is the value that will be
     *                    returned when the join point proceeds.
     */
    default void mockJoinPointWithArgs(final Object returnValue)
        throws Throwable {

        given(getJoinPoint().proceed(any())).willReturn(returnValue);
    }

    @Test
    @DisplayName("GIVEN a SPeL expression and arguments with "
        + "a specified return type,"
        + " WHEN parsing the expression,"
        + " THEN the result should be the expected result")
    default void testParseSpelExpressionWithType() {

        // GIVEN
        final Class<Integer> returnType = Integer.class;

        // WHEN
        final Integer result = getAspect().parseSpelExpression(
            getParameterName(), getArgs(), SPEL_EXPRESSION, returnType);

        // THEN
        assertEquals(EXPECTED_VALUE, result);
    }

    @Test
    @DisplayName("GIVEN a SPeL expression and arguments,"
        + " WHEN parsing the expression,"
        + " THEN the result should be the expected result")
    default void testParseSpelExpressionWithoutType() {

        // WHEN
        final Object result = getAspect().parseSpelExpression(
            getParameterName(), getArgs(), SPEL_EXPRESSION);

        // THEN
        assertEquals(EXPECTED_VALUE, result);
    }

    @Test
    @DisplayName("GIVEN a SPeL expression and bad arguments,"
        + " WHEN parsing the expression,"
        + " THEN the aspect should throw IllegalArgumentException")
    default void testParseSpelExpressionWithException() {

        // GIVEN
        final Object[] badArgs = {
            "bad test"
        };
        final AspectUtil aspect = getAspect();

        // WHEN
        assertThatThrownBy(() -> aspect.parseSpelExpression(getParameterName(),
            badArgs, SPEL_EXPRESSION))

            // THEN
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Method use in test if the tested method call the
     * {@link AspectUtil#log(String, org.slf4j.Logger, JoinPoint,
     * Object, Annotation, Object[])},
     * {@link AspectUtil#methodCallLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)} or
     * {@link AspectUtil#methodEndLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)}
     * for mocking this utility function. The test will be inside the runnable
     * argument.
     *
     * @param runnable The test to run after mocking the logging method.
     */
    default void mockForLog(final Runnable runnable) {

        mockForLog(runnable, false, true, "", true);
    }

    /**
     * Method use in test if the tested method call the
     * {@link AspectUtil#log(String, org.slf4j.Logger, JoinPoint,
     * Object, Annotation, Object[])},
     * {@link AspectUtil#methodCallLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)} or
     * {@link AspectUtil#methodEndLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)}
     * for mocking this utility function. The test will be inside the runnable
     * argument.
     *
     * @param runnable    The test to run after mocking the logging method.
     * @param asSignature If the test need to mock a signature : {@code false}
     *                    or if the test already mocks a signature :
     *                    {@code true}.
     */
    default
        void mockForLog(final Runnable runnable, final boolean asSignature) {

        mockForLog(runnable, asSignature, true, "", true);
    }

    /**
     * Method use in test if the tested method call the
     * {@link AspectUtil#log(String, org.slf4j.Logger, JoinPoint,
     * Object, Annotation, Object[])},
     * {@link AspectUtil#methodCallLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)} or
     * {@link AspectUtil#methodEndLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)}
     * for mocking this utility function. The test will be inside the runnable
     * argument.
     *
     * @param runnable The test to run after mocking the logging method.
     * @param message  The additional message for the log.
     */
    default void mockForLog(final Runnable runnable, final String message) {

        mockForLog(runnable, false, true, message, true);
    }

    /**
     * Method use in test if the tested method call the
     * {@link AspectUtil#log(String, org.slf4j.Logger, JoinPoint,
     * Object, Annotation, Object[])},
     * {@link AspectUtil#methodCallLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)} or
     * {@link AspectUtil#methodEndLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)}
     * for mocking this utility function. The test will be inside the runnable
     * argument.
     *
     * @param runnable        The test to run after mocking the logging method.
     * @param asSignature     If the test need to mock a signature :
     *                        {@code false} or if the test already mocks a
     *                        signature : {@code true}.
     * @param activateLogging The activateLogging attribute value.
     * @param message         The message attribute value.
     * @param isLoggable      If the target class is loggable. Implements
     *                        {@link Loggable}.
     */
    @SuppressWarnings("BooleanParameter")
    default void mockForLog(
        final Runnable runnable, final boolean asSignature,
        final boolean activateLogging, final String message,
        final boolean isLoggable) {

        try (MockedStatic<AnnotatedElementUtils> annotatedElementUtils
            = mockStatic(AnnotatedElementUtils.class)) {

            final AnnotationAttributes annotationAttributes
                = mock(AnnotationAttributes.class);

            if (!asSignature) {

                given(getJoinPoint().getSignature())
                    .willReturn(mock(MethodSignature.class));
            }

            annotatedElementUtils
                .when(() -> AnnotatedElementUtils
                    .findMergedAnnotationAttributes(any(),
                        ArgumentMatchers.<Class<? extends Annotation>>any(),
                        anyBoolean(), anyBoolean()))
                .thenReturn(annotationAttributes);
            given(annotationAttributes.getBoolean(any()))
                .willReturn(activateLogging);

            if (activateLogging || isLoggable) {

                given(annotationAttributes.getString(any()))
                    .willReturn(message);
            }
            runnable.run();
        } catch (final Throwable e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * Method use in test if the tested method call the
     * {@link AspectUtil#log(String, org.slf4j.Logger, JoinPoint,
     * Object, Annotation, Object[])},
     * {@link AspectUtil#methodCallLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)} or
     * {@link AspectUtil#methodEndLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)}
     * for mocking this utility function. The test will be inside the runnable
     * argument.
     * This function tests the specific case where an annotation
     * can't be retrieved by {@link AnnotatedElementUtils}.
     *
     * @param runnable The test to run after mocking the logging method.
     */
    private void mockForLogNoAnnotationAttributes(final Runnable runnable) {

        try (MockedStatic<AnnotatedElementUtils> annotatedElementUtils
            = mockStatic(AnnotatedElementUtils.class)) {

            final MethodSignature methodSignature = mock(MethodSignature.class);
            given(getJoinPoint().getSignature()).willReturn(methodSignature);

            annotatedElementUtils.when(
                () -> AnnotatedElementUtils.findMergedAnnotationAttributes(
                    any(), ArgumentMatchers.<Class<? extends Annotation>>any(),
                    anyBoolean(), anyBoolean()))
                .thenReturn(null);

            runnable.run();
        } catch (final Throwable e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * Method use in test if the tested method call the
     * {@link AspectUtil#log(String, org.slf4j.Logger, JoinPoint,
     * Object, Annotation, Object[])},
     * {@link AspectUtil#methodCallLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)} or
     * {@link AspectUtil#methodEndLog(org.slf4j.Logger, JoinPoint,
     * Object, Annotation)}
     * for mocking this utility function. The test will be inside the runnable
     * argument.
     * This function tests the specific case where an annotation has bad
     * argument and an error is thrown by {@link AnnotatedElementUtils}.
     *
     * @param runnable    The test to run after mocking the logging method.
     * @param asSignature If the test need to mock a signature : {@code false}
     *                    or if the test already mocks a signature :
     *                    {@code true}.
     */
    default void mockForLogBadAnnotationAttributes(
        final Runnable runnable, final boolean asSignature) {

        try (MockedStatic<AnnotatedElementUtils> annotatedElementUtils
            = mockStatic(AnnotatedElementUtils.class)) {

            final AnnotationAttributes annotationAttributes
                = mock(AnnotationAttributes.class);

            if (!asSignature) {

                final MethodSignature methodSignature
                    = mock(MethodSignature.class);
                given(getJoinPoint().getSignature())
                    .willReturn(methodSignature);
            }

            annotatedElementUtils
                .when(() -> AnnotatedElementUtils
                    .findMergedAnnotationAttributes(any(),
                        ArgumentMatchers.<Class<? extends Annotation>>any(),
                        anyBoolean(), anyBoolean()))
                .thenReturn(annotationAttributes);
            given(annotationAttributes.getBoolean(any()))
                .willThrow(IllegalArgumentException.class);

            runnable.run();
        } catch (final Throwable e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * The function adds a ListAppender to a Logger and returns the
     * ListAppender.
     *
     * @param  log The "log" parameter is a Logger object. It is the logger to
     *             which the ListAppender will be added.
     * @return     The method is returning a ListAppender{@code <ILoggingEvent>}
     *             object.
     */
    default ListAppender<ILoggingEvent> addTestAppender(final Logger log) {

        log.setLevel(Level.DEBUG);
        final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);
        return listAppender;
    }

    /**
     * This function is a parameterized test that verifies the behavior of a
     * logging aspect.
     * When the target implements {@link Loggable}.
     *
     * @param activateLogging A boolean value indicating whether logging should
     *                        be activated or not.
     */
    @ParameterizedTest
    @CsvSource({
        "true", "false"
    })
    @DisplayName("GIVEN a target of type Loggable,"
        + " WHEN logging a message,"
        + " THEN the message should be printed in the log")
    default void testLogOnLoggable(final boolean activateLogging) {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender
            = addTestAppender(getLogger());
        final TestAnnotation annotation = mock(TestAnnotation.class);
        final TestLoggableClass target = mock(TestLoggableClass.class);
        mockForLog(() -> getRunnableTestLogOnLoggable(target, annotation,
            listAppender), false, activateLogging, "", true);
    }

    private void getRunnableTestLogOnLoggable(
        final TestLoggableClass target, final TestAnnotation annotation,
        final ListAppender<ILoggingEvent> listAppender) {

        // WHEN
        getAspect().log(METHOD_CALL_LOG, getLogger(), getJoinPoint(), target,
            annotation, new Object[] {
                getJoinPoint().getSignature(), annotation, ""
            });

        // THEN
        final String expectedString = String.format(
            LOG_PARAMETER_PATTERN.matcher(METHOD_CALL_LOG).replaceAll("%s"),
            getJoinPoint().getSignature(), annotation, "");
        assertThat(listAppender.list.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage,
                ILoggingEvent::getLevel)
            .containsExactly(Tuple.tuple(expectedString, Level.DEBUG));
    }

    @Test
    @DisplayName("GIVEN a target of type Loggable and no annotation attribute,"
        + " WHEN logging a message,"
        + " THEN the message should be printed in the log")
    default void testLogOnLoggableNoAnnotationAttributes() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender
            = addTestAppender(getLogger());
        final TestAnnotation annotation = mock(TestAnnotation.class);
        final TestLoggableClass target = mock(TestLoggableClass.class);
        mockForLogNoAnnotationAttributes(() -> {

            // WHEN
            /*@formatter:off*/
            getAspect().log(METHOD_CALL_LOG, getLogger(), getJoinPoint(),
                target, annotation, new Object[] {
                    getJoinPoint().getSignature(), annotation, ""
                });
            /*@formatter:on*/

            // THEN
            final String expectedString = String.format(
                LOG_PARAMETER_PATTERN.matcher(METHOD_CALL_LOG).replaceAll("%s"),
                getJoinPoint().getSignature(), annotation, "");
            assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage,
                    ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple(expectedString, Level.DEBUG));
        });
    }

    @Test
    @DisplayName("GIVEN a target of type Loggable and bad annotation attribute,"
        + " WHEN logging a message,"
        + " THEN the message should be printed in the log")
    default void testLogOnLoggableBadAnnotationAttributes() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender
            = addTestAppender(getLogger());
        final TestAnnotation annotation = mock(TestAnnotation.class);
        final TestLoggableClass target = mock(TestLoggableClass.class);
        mockForLogBadAnnotationAttributes(
            () -> getRunnableTestLogOnLoggableBadAnnotationAttributes(target,
                annotation, listAppender),
            false);
    }

    private void getRunnableTestLogOnLoggableBadAnnotationAttributes(
        final TestLoggableClass target, final TestAnnotation annotation,
        final ListAppender<ILoggingEvent> listAppender) {

        // WHEN
        getAspect().log(METHOD_CALL_LOG, getLogger(), getJoinPoint(), target,
            annotation, new Object[] {
                getJoinPoint().getSignature(), annotation, ""
            });

        // THEN
        final String expectedString = String.format(
            LOG_PARAMETER_PATTERN.matcher(METHOD_CALL_LOG).replaceAll("%s"),
            getJoinPoint().getSignature(), annotation, "");
        assertThat(listAppender.list)
            .extracting(ILoggingEvent::getFormattedMessage,
                ILoggingEvent::getLevel)
            .containsExactly(Tuple.tuple(expectedString, Level.DEBUG),
                Tuple.tuple(BAD_ANNOTATION_ERROR_MESSAGE_STRING, Level.ERROR),
                Tuple.tuple(BAD_ANNOTATION_ERROR_EXCEPTION_STRING,
                    Level.ERROR));
    }

    /**
     * This function is a parameterized test that verifies the behavior of a
     * logging aspect.
     * When the target don't implement {@link Loggable}.
     *
     * @param activateLogging A boolean value indicating whether logging should
     *                        be activated or not.
     */
    @ParameterizedTest
    @CsvSource({
        "false", "true"
    })
    @DisplayName("GIVEN a target that doesn't implement Loggable,"
        + " WHEN logging a message,"
        + " THEN the message should be printed in the log")
    default void testLog(final boolean activateLogging) {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender
            = addTestAppender(getLogger());
        final TestAnnotation annotation = mock(TestAnnotation.class);
        final TestNotLoggableClass target = mock(TestNotLoggableClass.class);
        mockForLog(() -> getRunnableTestLog(activateLogging, target, annotation,
            listAppender), false, activateLogging, "", false);
    }

    private void getRunnableTestLog(
        final boolean activateLogging, final TestNotLoggableClass target,
        final TestAnnotation annotation,
        final ListAppender<ILoggingEvent> listAppender) {

        // WHEN
        getAspect().log(METHOD_CALL_LOG, getLogger(), getJoinPoint(), target,
            annotation, new Object[] {
                getJoinPoint().getSignature(), annotation, ""
            });

        // THEN
        if (activateLogging) {

            final String expectedString = String.format(
                LOG_PARAMETER_PATTERN.matcher(METHOD_CALL_LOG).replaceAll("%s"),
                getJoinPoint().getSignature(), annotation, "");
            assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage,
                    ILoggingEvent::getLevel)
                .containsExactly(Tuple.tuple(expectedString, Level.DEBUG));
        } else {

            assertThat(listAppender.list).isEmpty();
        }
    }

    @Test
    @DisplayName("GIVEN no annotation attribute,"
        + " WHEN logging a message,"
        + " THEN the message should be printed in the log")
    default void testLogNoAnnotationAttributes() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender
            = addTestAppender(getLogger());
        final TestAnnotation annotation = mock(TestAnnotation.class);
        final TestNotLoggableClass target = mock(TestNotLoggableClass.class);
        mockForLogNoAnnotationAttributes(() -> {

            // WHEN
            /*@formatter:off*/
            getAspect().log(METHOD_CALL_LOG, getLogger(), getJoinPoint(),
                target, annotation, new Object[] {
                    getJoinPoint().getSignature(), annotation, ""
                });
            /*@formatter:on*/

            // THEN
            assertThat(listAppender.list).isEmpty();
        });
    }

    @Test
    @DisplayName("GIVEN bad annotation attribute,"
        + " WHEN logging a message,"
        + " THEN the message should be printed in the log")
    default void testLogBadAnnotationAttributes() {

        // GIVEN
        final ListAppender<ILoggingEvent> listAppender
            = addTestAppender(getLogger());
        final TestAnnotation annotation = mock(TestAnnotation.class);
        final TestNotLoggableClass target = mock(TestNotLoggableClass.class);
        mockForLogBadAnnotationAttributes(() -> {

            // WHEN
            /*@formatter:off*/
            getAspect().log(METHOD_CALL_LOG, getLogger(), getJoinPoint(),
                target, annotation, new Object[] {
                    getJoinPoint().getSignature(), annotation, ""
                });
            /*@formatter:on*/

            // THEN
            assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage,
                    ILoggingEvent::getLevel)
                .containsExactly(
                    Tuple.tuple(BAD_ANNOTATION_ERROR_MESSAGE_STRING,
                        Level.ERROR),
                    Tuple.tuple(BAD_ANNOTATION_ERROR_EXCEPTION_STRING,
                        Level.ERROR));
        }, false);
    }

    /**
     * A functional interface for Runnable that can throws a Throwable.
     */
    @FunctionalInterface
    interface Runnable {

        /**
         * Perform an action.
         *
         * @throws Throwable If an error occurs while performing the action.
         */
        void run() throws Throwable;
    }

    /**
     * A class that implements {@link Loggable} for testing the log.
     */
    class TestLoggableClass implements Loggable {}

    /**
     * A class that don't implement {@link Loggable} for testing the log.
     */
    class TestNotLoggableClass {}

    /**
     * Annotation only used for testing aspect annotation logging.
     *
     * @author Malcolm Rozé
     * @since  0.1.0
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @LoggingAspect
    @interface TestAnnotation {

        /**
         * A default message to add to the log.
         *
         * @return A message to add to the log.
         */
        @AliasFor(annotation = LoggingAspect.class, attribute = "message")
        String message() default "";

        /**
         * A boolean to activate the logging if the target instance is not of
         * type {@link Loggable}.
         * {@code false} by default.
         *
         * @return A boolean to activate or not the logging.
         */
        @AliasFor(
            annotation = LoggingAspect.class,
            attribute = "activateLogging")
        boolean activateLogging() default false;
    }
}
