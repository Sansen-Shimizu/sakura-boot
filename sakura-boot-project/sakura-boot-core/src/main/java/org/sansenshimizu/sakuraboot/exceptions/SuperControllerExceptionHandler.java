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

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import org.sansenshimizu.sakuraboot.exceptions.configuration.ExceptionConfiguration;

/**
 * Global exception handler for the REST controller. Extends
 * {@link ResponseEntityExceptionHandler} to provide custom handling of various
 * exceptions and return appropriate error responses.
 * <p>
 * <b>NOTE:</b> This class already has all the needed methods to handle various
 * exceptions. If you need to add other methods or modify existing one you can
 * create a class that extends this one. All the methods are protected for this
 * purpose.
 * </p>
 *
 * @author Malcolm Rozé
 * @since  0.1.0
 */
@EnableConfigurationProperties(ExceptionConfiguration.class)
@RestControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SuperControllerExceptionHandler
    extends ResponseEntityExceptionHandler {

    /**
     * The message for unexpected server error.
     */
    private static final String UNEXPECTED_SERVER_ERROR_STRING
        = "unexpected server exception";

    /**
     * The configuration for exception.
     */
    private final ExceptionConfiguration exceptionConfiguration;

    /**
     * Handles generic exceptions with the provided status code and message.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Nullable
    protected ResponseEntity<Object> handleException(
        final Exception ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        String message = ex.getMessage();

        if (message == null) {

            message = "No message for: " + ex.getClass().getSimpleName();
        }
        return handleException(ex, headers, status, request, message);
    }

    /**
     * Handles generic exceptions with the provided status code and custom
     * message.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @param  message The custom message.
     * @return         ResponseEntity containing the error response.
     */
    @Nullable
    protected ResponseEntity<Object> handleException(
        final Exception ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request,
        final String message) {

        String description = request.getDescription(false);

        if (!request.getParameterMap().isEmpty()) {

            description = request.getParameterMap()
                .entrySet()
                .stream()
                .map(p -> p.getKey() + "=" + String.join(",", p.getValue()))
                .collect(Collectors.joining("&",
                    request.getDescription(false) + "?", ""));
        }

        final String stackTrace;

        if (exceptionConfiguration.showStackTrace()) {

            stackTrace = ExceptionUtils.getStackTrace(ex);
        } else {

            stackTrace = null;
        }

        final ErrorResponse errorResponse = ErrorResponse.builder()
            .status(status.value())
            .timestamp(Instant.now())
            .message(message)
            .description(description)
            .stackTrace(stackTrace)
            .build();

        logger.error(message, ex);

        return handleExceptionInternal(ex, errorResponse, headers, status,
            request);
    }

    /**
     * Handles HttpRequestMethodNotSupportedException and returns an appropriate
     * error response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        final HttpRequestMethodNotSupportedException ex,
        final HttpHeaders headers, final HttpStatusCode status,
        final WebRequest request) {

        String message
            = ex.getMethod() + " method is not supported for this request.";
        final Set<HttpMethod> methods = ex.getSupportedHttpMethods();

        if (methods != null) {

            message = methods.stream()
                .map(HttpMethod::toString)
                .collect(Collectors.joining(", ",
                    ex.getMethod()
                        + " method is not supported for this request. "
                        + "Supported methods are ",
                    ""));
        }

        return handleException(ex, headers, status, request, message);
    }

    /**
     * Handles NoHandlerFoundException and returns an appropriate error
     * response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
        final NoHandlerFoundException ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        pageNotFoundLogger.warn(ex.getMessage());
        return handleException(ex, headers, status, request,
            "No handler found for "
                + ex.getHttpMethod()
                + " "
                + ex.getRequestURL());
    }

    /**
     * Handles MethodArgumentNotValidException and returns an appropriate error
     * response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        final MethodArgumentNotValidException ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        final String prefix
            = "Validation error (" + ex.getErrorCount() + ") : ";
        final String message
            = ex.getAllErrors().stream().map((final ObjectError error) -> {

                String errorMessage
                    = "On '" + error.getObjectName() + "' Object: ";

                if (error instanceof final FieldError fieldError) {

                    errorMessage += "field '"
                        + fieldError.getField()
                        + "', rejected value '"
                        + fieldError.getRejectedValue()
                        + "'. ";
                }
                return errorMessage
                    + "With code: "
                    + error.getCode()
                    + " "
                    + error.getDefaultMessage();
            }).collect(Collectors.joining(", ", prefix, ""));

        return handleException(ex, headers, status, request, message);
    }

    /**
     * Handles ConstraintViolationException, DataIntegrityViolationException and
     * returns an appropriate error response.
     *
     * @param  ex      The exception to handle.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Nullable
    @ExceptionHandler({
        ConstraintViolationException.class,
        DataIntegrityViolationException.class
    })
    protected ResponseEntity<Object> handleConstraintViolation(
        final RuntimeException ex, final WebRequest request) {

        return handleException(ex, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST,
            request, "Entity validation error: " + ex.getMessage());
    }

    /**
     * Handles HttpMediaTypeNotSupportedException and returns an appropriate
     * error response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        final String message = ex.getSupportedMediaTypes()
            .stream()
            .map(MediaType::toString)
            .collect(Collectors.joining(", ",
                ex.getContentType()
                    + " media type is not supported. "
                    + "Supported media types are ",
                ""));

        return handleException(ex, headers, status, request, message);
    }

    /**
     * Handles TypeMismatchException and returns an appropriate error response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        final TypeMismatchException ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        String message = "The property: "
            + ex.getPropertyName()
            + " is not applicable for the value: "
            + ex.getValue()
            + ".";
        final Class<?> type = ex.getRequiredType();

        if (type != null) {

            message += " The supported type is: " + type.getSimpleName();
        }

        return handleException(ex, headers, status, request, message);
    }

    /**
     * Handles MissingPathVariableException and returns an appropriate error
     * response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
        final MissingPathVariableException ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        return handleException(ex, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Handles HttpMessageNotReadableException and returns an appropriate error
     * response.
     *
     * @param  ex      The exception to handle.
     * @param  headers The HTTP headers.
     * @param  status  The HTTP status code.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        final HttpMessageNotReadableException ex, final HttpHeaders headers,
        final HttpStatusCode status, final WebRequest request) {

        return handleException(ex, headers, status, request);
    }

    /**
     * Handles SuperException and returns an appropriate error response.
     *
     * @param  ex      The exception to handle.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Nullable
    @ExceptionHandler(SuperException.class)
    protected ResponseEntity<Object> handleSuperException(
        final SuperException ex, final WebRequest request) {

        if (HttpStatus.INTERNAL_SERVER_ERROR == ex.getStatus()) {

            return handleException(ex, new HttpHeaders(), ex.getStatus(),
                request, UNEXPECTED_SERVER_ERROR_STRING);
        }
        return handleException(ex, new HttpHeaders(), ex.getStatus(), request);
    }

    /**
     * Handles generic Exception and returns an appropriate error response.
     *
     * @param  ex      The exception to handle.
     * @param  request The web request.
     * @return         ResponseEntity containing the error response.
     */
    @Nullable
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleOtherException(
        final Exception ex, final WebRequest request) {

        return handleException(ex, new HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR, request,
            UNEXPECTED_SERVER_ERROR_STRING);
    }
}
