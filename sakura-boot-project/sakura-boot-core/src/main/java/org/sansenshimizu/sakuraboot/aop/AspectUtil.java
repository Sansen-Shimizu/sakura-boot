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

package org.sansenshimizu.sakuraboot.aop;

import java.lang.annotation.Annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.log.api.Loggable;

/**
 * The interface that all aspect class can use.
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
public interface AspectUtil {

    /**
     * The value of this variable is defining a pointcut expression for all
     * method executions.
     * It matches any method with any return type and any number of parameters.
     * The `..` represents any number of parameters.
     */
    String ALL_EXECUTION_POINTCUT = "execution(* *(..))";

    /**
     * The value of this variable is defining a pointcut expression for the
     * target object in an aspect.
     */
    String TARGET_POINTCUT = " && target(target)";

    /**
     * The value of this variable is defining a pointcut expression for the
     * first argument in a method execution.
     * It matches any method with any return type and any number of parameters,
     * but it specifically captures the first argument.
     * The `args(arg, ..)` part of the expression specifies that the first
     * argument should be captured and can be referred to as `arg` in the aspect
     * code.
     */
    String FIRST_ARG_POINTCUT = " && args(arg, ..)";

    /**
     * The value of this variable is defining a pointcut expression for the
     * first and second arguments in a method execution.
     * It matches any method with any return type and any number of parameters,
     * but it specifically captures the first and second arguments.
     * The `args(firstArg, secondArg, ..)` part of the expression specifies that
     * the first and second arguments should
     * be captured and can be referred to as `firstArg` and `secondArg` in the
     * aspect code.
     */
    String FIRST_AND_SECOND_ARGS_POINTCUT = " && args(firstArg, secondArg, ..)";

    /**
     * The value of this variable is defining a pointcut expression for an
     * annotation.
     * It is used in aspect-oriented programming to specify that the aspect
     * should be applied to methods that are annotated with a specific
     * annotation.
     * In this case, the `@annotation(annotation)` expression matches any method
     * that has the specified annotation.
     */
    String ANNOTATION_POINTCUT = " && @annotation(annotation)";

    /**
     * The value of this variable is defining a pointcut expression for a type
     * annotation.
     * It is used in aspect-oriented programming to specify that the aspect
     * should be applied to methods that are inside classes that are annotated
     * with a specific annotation.
     * In this case, the `@within(typeAnnotation)` expression matches any method
     * in a class that has the specified annotation.
     */
    String TYPE_ANNOTATION_POINTCUT = " && @target(typeAnnotation)";

    /**
     * The value of this variable is defining a constant variable
     * `HYPERMEDIA_ORDER` with a value of 100.
     * This variable is used to specify the order in which the hypermedia aspect
     * should be executed in relation to other aspects.
     * The {@link Order} annotation can be used to specify the order of
     * execution for multiple aspects.
     * In this case, the hypermedia aspect should be executed before other
     * aspects, so it is assigned a lower order value (100) compared to other
     * aspects.
     */
    int HYPERMEDIA_ORDER = 100;

    /**
     * The value of this variable is used to specify the order in which the save
     * relationship aspect should be executed in relation to other aspects.
     * The {@link Order} annotation can be used to specify the order of
     * execution for multiple aspects.
     * In this case, the save relationship aspect should be executed before any
     * other aspects, so it is assigned a lower order value (50) compared to
     * other aspects.
     */
    int SAVE_RELATIONSHIP_ORDER = 50;

    /**
     * The value of this variable is used to specify the order in which the
     * cache aspect should be executed in relation to other aspects.
     * The {@link Order} annotation can be used to specify the order of
     * execution for multiple aspects.
     * In this case, the cache aspect should be executed before other aspects,
     * so it is assigned a lower order value (100) compared to other aspects.
     */
    int CACHE_ORDER = 100;

    /**
     * The value of this variable is used to specify the order in which the
     * mapping aspect should be executed in relation to other aspects.
     * The {@link Order} annotation can be used to specify the order of
     * execution for multiple aspects.
     * In this case, the mapping aspect should be executed before other aspects,
     * so it is assigned a lower order value (200) compared to other aspects.
     * This also ensures that the mapping aspect is executed after the caching
     * aspect, which has a lower order value.
     */
    int MAPPING_ORDER = 200;

    /**
     * The value of this variable is used to specify the order in which the
     * relationship aspect should be executed in relation to other aspects.
     * The {@link Order} annotation can be used to specify the order of
     * execution for multiple aspects.
     * In this case, the relationship aspect should be executed after other
     * aspects, so it is assigned a higher order value (300) compared to other
     * aspects.
     */
    int RELATIONSHIP_ORDER = 300;

    /**
     * Retrieves the value of the specified annotation attribute.
     *
     * @param  annotation the annotation object from which to retrieve the
     *                    attribute value
     * @param  name       the name of the attribute to retrieve
     * @return            the value of the specified attribute, or null if it is
     *                    not present
     */
    @Nullable
    static Object getAnnotationValue(
        final Annotation annotation, final String name) {

        final Object value = AnnotationUtils.getValue(annotation, name);

        if (value == null
            || value
                .equals(AnnotationUtils.getDefaultValue(annotation, name))) {

            return AnnotationUtils.getValue(annotation);
        }
        return value;
    }

    /**
     * The function parses a SpEL expression using the given parameter names,
     * arguments, and expression string, then returns the result as an Object.
     *
     * @param  parameterNames An array of parameter names for the method being
     *                        called.
     * @param  args           An array of objects representing the arguments
     *                        passed to the method.
     * @param  expression     The expression parameter is a String that
     *                        represents a SpEL (Spring Expression Language)
     *                        expression.
     * @return                The value of type Object parsed from the given
     *                        SpEL expression.
     */
    @Nullable
    default Object parseSpelExpression(
        final String[] parameterNames, final Object[] args,
        final String expression) {

        return parseSpelExpression(parameterNames, args, expression,
            Object.class);
    }

    /**
     * This function parses a SpEL expression using the given parameter names,
     * arguments, and expression string, then returns the result of the
     * expression as the specified return type.
     *
     * @param  <T>            The return type.
     * @param  parameterNames An array of parameter names for the method being
     *                        called.
     * @param  args           An array of objects representing the arguments
     *                        passed to the method.
     * @param  expression     The expression parameter is a String that
     *                        represents a SpEL (Spring Expression Language)
     *                        expression.
     * @param  returnType     The returnType parameter is the class type of the
     *                        expected return value from the SpEL
     *                        expression.
     * @return                The method is returning a value of type T parsed
     *                        from the given SpEL expression.
     */
    @Nullable
    default <T> T parseSpelExpression(
        final String[] parameterNames, final Object[] args,
        final String expression, final Class<T> returnType) {

        if (parameterNames.length != args.length) {

            throw new IllegalArgumentException(
                "Exception in aspect method when parsing SpEL expression:"
                    + " parameterNames and args aren't of the same length.");
        }

        final ExpressionParser parser = new SpelExpressionParser();
        final EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {

            context.setVariable(parameterNames[i], args[i]);
        }
        return parser.parseExpression(expression).getValue(context, returnType);
    }

    /**
     * The function parses a SpEL expression using the given parameter names,
     * arguments, and expression string, then returns the result at the
     * specified index as an Object.
     *
     * @param  parameterNames An array of parameter names for the method being
     *                        called.
     * @param  args           An array of objects representing the arguments
     *                        passed to the method.
     * @param  expression     The expression parameter is a String that
     *                        represents a SpEL (Spring Expression Language)
     *                        expression with the index need to be
     *                        represented by [i] (e.g., #list[i]).
     * @param  index          The index parameter is the index of the element
     *                        in the collection to be retrieved.
     * @return                The value of type Object parsed from the given
     *                        SpEL expression at the specified index.
     */
    @Nullable
    default Object parseSpelExpressionForCollection(
        final String[] parameterNames, final Object[] args,
        final String expression, final int index) {

        return parseSpelExpressionForCollection(parameterNames, args,
            expression, index, Object.class);
    }

    /**
     * This function parses a SpEL expression using the given parameter names,
     * arguments, and expression string, then returns the result at the
     * specified index as the specified return type.
     *
     * @param  <T>            The return type.
     * @param  parameterNames An array of parameter names for the method being
     *                        called.
     * @param  args           An array of objects representing the arguments
     *                        passed to the method.
     * @param  expression     The expression parameter is a String that
     *                        represents a SpEL (Spring Expression Language)
     *                        expression with the index need to be
     *                        represented by [i] (e.g., #list[i]).
     * @param  index          The index parameter is the index of the element
     *                        in the collection to be retrieved.
     * @param  returnType     The returnType parameter is the class type of the
     *                        expected return value from the SpEL
     *                        expression.
     * @return                The method is returning a value of type T parsed
     *                        from the given SpEL expression at the specified
     *                        index.
     */
    @Nullable
    default <T> T parseSpelExpressionForCollection(
        final String[] parameterNames, final Object[] args,
        final String expression, final int index, final Class<T> returnType) {

        if (parameterNames.length != args.length) {

            throw new IllegalArgumentException(
                "Exception in aspect method when parsing SpEL expression:"
                    + " parameterNames and args aren't of the same length.");
        }

        final ExpressionParser parser = new SpelExpressionParser();
        final EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < parameterNames.length; i++) {

            context.setVariable(parameterNames[i], args[i]);
        }
        return parser
            .parseExpression(expression.replace("[i]", "[" + index + "]"))
            .getValue(context, returnType);
    }

    /**
     * Helper function to log messages for an aspect method.
     *
     * @param message       The log message to be logged. Need to have a last
     *                      parameter for additional message logging.
     * @param log           The "log" parameter is an instance of the Logger
     *                      class, which is used for logging messages.
     * @param joinPoint     The joinPoint parameter represents the specific
     *                      point in the execution of a program where advice
     *                      (code that runs before, after, or around a
     *                      method) can be applied.
     *                      It contains information about the method being
     *                      executed, such as its signature, arguments, and
     *                      target object.
     * @param target        The "target" parameter refers to the object on which
     *                      the annotated method is being called.
     *                      It represents the instance of the class that
     *                      contains the annotated method.
     * @param annotation    The "annotation" parameter is an object of type
     *                      Annotation.
     *                      It represents the specific annotation that is being
     *                      processed in the log method.
     * @param logParameters An array of objects that represent the parameters
     *                      passed to the message to be logged.
     *                      Must have the exact same number of elements as the
     *                      number of parameters in the message argument.
     *                      The last parameter for the additional message can be
     *                      an empty string.
     * @see                 #methodCallLog(Logger, JoinPoint, Object,
     *                      Annotation)
     * @see                 #methodEndLog(Logger, JoinPoint, Object, Annotation)
     */
    default void log(
        final String message, final Logger log, final JoinPoint joinPoint,
        final Object target, final Annotation annotation,
        final Object[] logParameters) {

        final MethodSignature signature
            = (MethodSignature) joinPoint.getSignature();
        final AnnotationAttributes loggingAttribute = AnnotatedElementUtils
            .findMergedAnnotationAttributes(signature.getMethod(),
                annotation.annotationType(), false, false);

        final String activateLoggingAttribute = "activateLogging";
        final String messageAttribute = "message";

        if (loggingAttribute != null) {

            try {

                if (loggingAttribute.getBoolean(activateLoggingAttribute)
                    || target instanceof Loggable) {

                    logParameters[logParameters.length - 1]
                        = loggingAttribute.getString(messageAttribute);
                    log.atDebug().log(message, logParameters);
                }
            } catch (final IllegalArgumentException e) {

                if (target instanceof Loggable) {

                    log.atDebug().log(message, logParameters);
                }
                log.atError()
                    .log("The logging annotation must have the attributes: "
                        + activateLoggingAttribute
                        + " and "
                        + messageAttribute
                        + ".");
                log.atError().log("With exception : " + e);
            }
        } else {

            if (target instanceof Loggable) {

                log.atDebug().log(message, logParameters);
            }
        }
    }

    /**
     * Helper function to log messages at the beginning of an aspect method.
     *
     * @param log        The "log" parameter is an instance of the Logger class,
     *                   which is used for logging messages.
     * @param joinPoint  The joinPoint parameter represents the specific point
     *                   in the execution of a program where advice (code that
     *                   runs before, after, or around a method) can be applied.
     *                   It contains information about the method being
     *                   executed, such as its signature, arguments, and target
     *                   object.
     * @param target     The "target" parameter refers to the object on which
     *                   the annotated method is being called.
     *                   It represents the instance of the class that contains
     *                   the annotated method.
     * @param annotation The "annotation" parameter is an object of type
     *                   Annotation.
     *                   It represents the specific annotation that is being
     *                   processed in the log method.
     */
    default void methodCallLog(
        final Logger log, final JoinPoint joinPoint, final Object target,
        final Annotation annotation) {

        log("Method call : {}, with : {} annotation. {}", log, joinPoint,
            target, annotation, new Object[] {
                joinPoint.getSignature(), annotation, ""
            });
    }

    /**
     * Helper function to log messages at the end of an aspect method.
     *
     * @param log        The "log" parameter is an instance of the Logger class,
     *                   which is used for logging messages.
     * @param joinPoint  The joinPoint parameter represents the specific point
     *                   in the execution of a program where advice (code that
     *                   runs before, after, or around a method) can be applied.
     *                   It contains information about the method being
     *                   executed, such as its signature, arguments, and target
     *                   object.
     * @param target     The "target" parameter refers to the object on which
     *                   the annotated method is being called.
     *                   It represents the instance of the class that contains
     *                   the annotated method.
     * @param annotation The "annotation" parameter is an object of type
     *                   Annotation.
     *                   It represents the specific annotation that is being
     *                   processed in the log method.
     */
    default void methodEndLog(
        final Logger log, final JoinPoint joinPoint, final Object target,
        final Annotation annotation) {

        log("Method finish : {}, with : {} annotation. {}", log, joinPoint,
            target, annotation, new Object[] {
                joinPoint.getSignature(), annotation, ""
            });
    }
}
