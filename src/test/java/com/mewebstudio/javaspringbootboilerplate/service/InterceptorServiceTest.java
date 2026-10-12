package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.security.Authorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Enumeration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterceptorServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private InterceptorService interceptorService;

    private HandlerMethod handlerMethod;
    private Method testMethod;

    @BeforeEach
    void setUp() throws Exception {
        // Create a dummy controller class with various methods for testing
        handlerMethod = mock(HandlerMethod.class);
        
        // Create a simple method to mock
        testMethod = TestController.class.getMethod("simpleMethod");
        when(handlerMethod.getMethod()).thenReturn(testMethod);
    }

    @Test
    @DisplayName("Given non-HandlerMethod handler when preHandle then return true")
    void givenNonHandlerMethod_whenPreHandle_thenReturnTrue() {
        // Arrange
        Object nonHandlerMethod = new Object();

        // Act
        boolean result = interceptorService.preHandle(request, response, nonHandlerMethod);

        // Assert
        assertTrue(result);
        verifyNoInteractions(authenticationService);
    }

    @Test
    @DisplayName("Given valid request without Authorize annotation when preHandle then return true")
    void givenValidRequestWithoutAuthorizeAnnotation_whenPreHandle_thenReturnTrue() throws Exception {
        // Arrange
        Method methodWithoutAnnotation = TestController.class.getMethod("methodWithoutAnnotation");
        when(handlerMethod.getMethod()).thenReturn(methodWithoutAnnotation);
        when(handlerMethod.getMethodParameters()).thenReturn(new MethodParameter[0]);
        when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());

        // Act
        boolean result = interceptorService.preHandle(request, response, handlerMethod);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Given request with unexpected query params when preHandle then throw InvalidParameterException")
    void givenRequestWithUnexpectedQueryParams_whenPreHandle_thenThrowInvalidParameterException() throws Exception {
        // Arrange
        Method methodWithRequestParam = TestController.class.getMethod("methodWithRequestParam", String.class);
        when(handlerMethod.getMethod()).thenReturn(methodWithRequestParam);
        
        MethodParameter[] methodParameters = new MethodParameter[1];
        methodParameters[0] = mock(MethodParameter.class);
        when(methodParameters[0].getParameterAnnotation(RequestParam.class)).thenReturn(null);
        when(handlerMethod.getMethodParameters()).thenReturn(methodParameters);
        
        // Simulate request with unexpected parameter
        when(request.getParameterNames()).thenReturn(
            Collections.enumeration(java.util.List.of("unexpectedParam"))
        );

        // Act & Assert
        assertThrows(InvalidParameterException.class, () -> 
            interceptorService.preHandle(request, response, handlerMethod)
        );
    }

    @Test
    @DisplayName("Given request with expected query params when preHandle then return true")
    void givenRequestWithExpectedQueryParams_whenPreHandle_thenReturnTrue() throws Exception {
        // Arrange
        Method methodWithRequestParam = TestController.class.getMethod("methodWithRequestParam", String.class);
        when(handlerMethod.getMethod()).thenReturn(methodWithRequestParam);
        
        MethodParameter[] methodParameters = new MethodParameter[1];
        methodParameters[0] = mock(MethodParameter.class);
        RequestParam requestParam = mock(RequestParam.class);
        when(requestParam.name()).thenReturn("expectedParam");
        when(methodParameters[0].getParameterAnnotation(RequestParam.class)).thenReturn(requestParam);
        when(handlerMethod.getMethodParameters