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

import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolationException;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import org.sansenshimizu.sakuraboot.exceptions.configuration.ExceptionConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * The base test class for all exceptions. This class provides common tests for
 * testing {@link SuperException}, {@link ErrorResponse} and
 * {@link SuperControllerExceptionHandler}.
 * It is possible to extend this class to test custom exceptions.
 *
 * @author Malcolm Rozé
 * @see    SuperException
 * @see    ErrorResponse
 * @see    SuperControllerExceptionHandler
 * @since  0.1.0
 */
@ExtendWith(MockitoExtension.class)
class ExceptionTest {

    /**
     * A simple error message use in test.
     */
    private static final String ERROR_MESSAGE = "Error exception";

    /**
     * The expected message for an internal error message.
     */
    private static final String INTERNAL_ERROR_MESSAGE
        = "unexpected server exception";

    /**
     * The {@link SuperControllerExceptionHandler} to test.
     */
    @InjectMocks
    private SuperControllerExceptionHandler controllerExceptionHandler;

    /**
     * The {@link ExceptionConfiguration} used in test.
     */
    @Mock
    private ExceptionConfiguration exceptionConfiguration;

    /**
     * The {@link HttpHeaders} use in the test.
     */
    private HttpHeaders headers;

    /**
     * The {@link WebRequest} use in the test.
     */
    private WebRequest request;

    /**
     * The setUp function call before every test.
     */
    @BeforeEach
    protected void setUp() {

        headers = new HttpHeaders();
        request = new ServletWebRequest(
            new MockHttpServletRequest(new MockServletContext()));
    }

    @Test
    @DisplayName("GIVEN a HttpRequestMethodNotSupportedException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testHttpRequestMethodNotSupportedException() {

        // GIVEN
        final HttpRequestMethodNotSupportedException exception
            = mock(HttpRequestMethodNotSupportedException.class);
        final HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        given(exception.getMethod()).willReturn(HttpMethod.GET.toString());
        given(exception.getSupportedHttpMethods())
            .willReturn(Set.of(HttpMethod.POST));

        // WHEN
        final ResponseEntity<Object> response
            = controllerExceptionHandler.handleHttpRequestMethodNotSupported(
                exception, headers, status, request);

        // THEN
        assertResponse(response, status,
            HttpMethod.GET
                + " method is not supported for this request. "
                + "Supported methods are POST");
    }

    @Test
    @DisplayName("GIVEN a HttpRequestMethodNotSupportedException and "
        + "no supported method,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final
        void testHttpRequestMethodNotSupportedExceptionAndNoSupportedMethod() {

        // GIVEN
        final HttpRequestMethodNotSupportedException exception
            = mock(HttpRequestMethodNotSupportedException.class);
        final HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        given(exception.getMethod()).willReturn(HttpMethod.GET.toString());
        given(exception.getSupportedHttpMethods()).willReturn(null);

        // WHEN
        final ResponseEntity<Object> response
            = controllerExceptionHandler.handleHttpRequestMethodNotSupported(
                exception, headers, status, request);

        // THEN
        assertResponse(response, status,
            HttpMethod.GET + " method is not supported for this request.");
    }

    @Test
    @DisplayName("GIVEN a NoHandlerFoundException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testNoHandlerFoundException() {

        // GIVEN
        final NoHandlerFoundException exception
            = mock(NoHandlerFoundException.class);
        final HttpStatus status = HttpStatus.NOT_FOUND;
        final String url = "http://url/test";
        given(exception.getHttpMethod()).willReturn(HttpMethod.GET.toString());
        given(exception.getRequestURL()).willReturn(url);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleNoHandlerFoundException(exception, headers, status, request);

        // THEN
        assertResponse(response, status,
            "No handler found for " + HttpMethod.GET + " " + url);
    }

    @Test
    @DisplayName("GIVEN a MethodArgumentNotValidException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testMethodArgumentNotValidException() {

        // GIVEN
        final MethodArgumentNotValidException exception
            = mock(MethodArgumentNotValidException.class);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final FieldError fieldError = mock(FieldError.class);
        final ObjectError objectError = mock(ObjectError.class);
        given(exception.getErrorCount()).willReturn(2);
        given(exception.getAllErrors())
            .willReturn(List.of(fieldError, objectError));
        final String objectName = "objectName";
        final String fieldName = "fieldName";
        final String codeName = "codeName";
        final String defaultMessage = "defaultMessage";
        given(fieldError.getObjectName()).willReturn(objectName);
        given(fieldError.getField()).willReturn(fieldName);
        given(fieldError.getRejectedValue()).willReturn(null);
        given(fieldError.getCode()).willReturn(codeName);
        given(fieldError.getDefaultMessage()).willReturn(defaultMessage);
        given(objectError.getObjectName()).willReturn(objectName);
        given(objectError.getCode()).willReturn(codeName);
        given(objectError.getDefaultMessage()).willReturn(defaultMessage);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleMethodArgumentNotValid(exception, headers, status, request);

        // THEN
        assertResponse(response, status,
            "Validation error (2) : On '"
                + objectName
                + "' Object: field '"
                + fieldName
                + "', rejected value 'null'. With code: "
                + codeName
                + " "
                + defaultMessage
                + ", On '"
                + objectName
                + "' Object: With code: "
                + codeName
                + " "
                + defaultMessage);
    }

    @Test
    @DisplayName("GIVEN a ConstraintViolationException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testConstraintViolationException() {

        // GIVEN
        final ConstraintViolationException exception
            = mock(ConstraintViolationException.class);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        given(exception.getMessage()).willReturn(ERROR_MESSAGE);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleConstraintViolation(exception, request);

        // THEN
        assertResponse(response, status,
            "Entity validation error: " + ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN a HttpMediaTypeNotSupportedException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testHttpMediaTypeNotSupportedException() {

        // GIVEN
        final HttpMediaTypeNotSupportedException exception
            = mock(HttpMediaTypeNotSupportedException.class);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        given(exception.getContentType())
            .willReturn(MediaType.APPLICATION_JSON);
        given(exception.getSupportedMediaTypes())
            .willReturn(List.of(MediaType.TEXT_XML));

        // WHEN
        final ResponseEntity<Object> response
            = controllerExceptionHandler.handleHttpMediaTypeNotSupported(
                exception, headers, status, request);

        // THEN
        assertResponse(response, status,
            MediaType.APPLICATION_JSON
                + " media type is not supported. "
                + "Supported media types are "
                + MediaType.TEXT_XML);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(classes = String.class)
    @DisplayName("GIVEN a TypeMismatchException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final
        void testTypeMismatchException(@Nullable final Class<?> requiredType) {

        // GIVEN
        final TypeMismatchException exception
            = mock(TypeMismatchException.class);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final String propertyName = "TEST";
        final Long propertyValue = 1L;
        given(exception.getPropertyName()).willReturn(propertyName);
        given(exception.getValue()).willReturn(propertyValue);
        final BDDMockito.BDDMyOngoingStubbing<Class<?>> stubbing
            = given(exception.getRequiredType());
        stubbing.willReturn(requiredType);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleTypeMismatch(exception, headers, status, request);

        // THEN
        String errorMessage = "The property: "
            + propertyName
            + " is not applicable for the value: "
            + propertyValue
            + ".";

        if (requiredType != null) {

            errorMessage
                += " The supported type is: " + requiredType.getSimpleName();
        }
        assertResponse(response, status, errorMessage);
    }

    @Test
    @DisplayName("GIVEN a MissingPathVariableException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testMissingPathVariableException() {

        // GIVEN
        final MissingPathVariableException exception
            = mock(MissingPathVariableException.class);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final String errorMessage = "Error missing path variable";
        given(exception.getMessage()).willReturn(errorMessage);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleMissingPathVariable(exception, headers, status, request);

        // THEN
        assertResponse(response, status, errorMessage);
    }

    @Test
    @DisplayName("GIVEN a HttpMessageNotReadableException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testHttpMessageNotReadableException() {

        // GIVEN
        final HttpMessageNotReadableException exception
            = mock(HttpMessageNotReadableException.class);
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final String errorMessage = "Error message not readable";
        given(exception.getMessage()).willReturn(errorMessage);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleHttpMessageNotReadable(exception, headers, status, request);

        // THEN
        assertResponse(response, status, errorMessage);
    }

    @Test
    @DisplayName("GIVEN a SuperException,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testSuperException() {

        // GIVEN
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        final SuperException exception = new SuperException(ERROR_MESSAGE,
            status, new Throwable(ERROR_MESSAGE));

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleSuperException(exception, request);

        // THEN
        assertResponse(response, status, ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN a SuperException with internal server error status,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testSuperExceptionInternalServerError() {

        // GIVEN
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final SuperException exception = new SuperException(ERROR_MESSAGE,
            status, new Throwable(ERROR_MESSAGE));

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleSuperException(exception, request);

        // THEN
        assertResponse(response, status, INTERNAL_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN an Exception,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testException() {

        // GIVEN
        final Exception exception = new Exception(ERROR_MESSAGE);
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleOtherException(exception, request);

        // THEN
        assertResponse(response, status, INTERNAL_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("GIVEN an Exception with no message,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testExceptionWithNoMessage() {

        // GIVEN
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        @SuppressWarnings("DataFlowIssue")
        final SuperException exception
            = new SuperException(null, status, new Throwable(ERROR_MESSAGE));

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleSuperException(exception, request);

        // THEN
        assertResponse(response, status, "No message for: SuperException");
    }

    @Test
    @DisplayName("GIVEN an Exception and need to show stack trace,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error with "
        + "the stack trace")
    final void testExceptionWithShowStackTrace() {

        // GIVEN
        final Exception exception
            = new Exception(ERROR_MESSAGE, new Throwable(ERROR_MESSAGE));
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        given(exceptionConfiguration.showStackTrace()).willReturn(true);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleOtherException(exception, request);

        // THEN
        assertResponse(response, status, INTERNAL_ERROR_MESSAGE);
        assert response != null;
        assertThat(response.getBody())
            .asInstanceOf(InstanceOfAssertFactories.type(ErrorResponse.class))
            .extracting(ErrorResponse::stackTrace)
            .asString()
            .isNotEmpty();
    }

    @Test
    @DisplayName("GIVEN an Exception and a request with parameters,"
        + " WHEN handle the exception,"
        + " THEN should return an appropriate response error")
    final void testExceptionAndRequestWithParameters() {

        // GIVEN
        final Exception exception = new Exception(ERROR_MESSAGE);
        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final MockHttpServletRequest mockRequest
            = new MockHttpServletRequest(new MockServletContext());
        mockRequest.addParameter("testName", "testValue");
        request = new ServletWebRequest(mockRequest);

        // WHEN
        final ResponseEntity<Object> response = controllerExceptionHandler
            .handleOtherException(exception, request);

        // THEN
        assertResponse(response, status, INTERNAL_ERROR_MESSAGE);
        assert response != null;
        assertThat(response.getBody())
            .asInstanceOf(InstanceOfAssertFactories.type(ErrorResponse.class))
            .extracting(ErrorResponse::description)
            .isEqualTo(request.getDescription(false) + "?testName=testValue");
    }

    /**
     * Assert for testing the created response by the
     * {@link SuperControllerExceptionHandler}.
     *
     * @param response The response created in the test.
     * @param status   The expected status of the response.
     * @param message  The expected error message of the response.
     */
    private static void assertResponse(
        @Nullable final ResponseEntity<Object> response,
        final HttpStatus status, final String message) {

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody())
            .asInstanceOf(InstanceOfAssertFactories.type(ErrorResponse.class))
            .extracting(ErrorResponse::message)
            .isEqualTo(message);
    }
}
