package com.pplflw.prototype.web;

import com.pplflw.prototype.exceptions.BusinessException;
import com.pplflw.prototype.exceptions.ResourceNotFoundException;
import com.pplflw.prototype.exceptions.RestApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    private final Environment environment;

    @Autowired
    public ControllerExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String error = "Malformed JSON request";
        return buildResponseEntity(new RestApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(RestApiError restApiError) {

        return new ResponseEntity<>(restApiError, restApiError.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(ResourceNotFoundException ex) {

        RestApiError restApiError = new RestApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), ex);
        return buildResponseEntity(restApiError);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<Object> handleBusinessException(BusinessException ex) {

        RestApiError restApiError = new RestApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), ex);
        
        // We can choose to hide the more technical details in productions env.
        if(!Arrays.asList(this.environment.getActiveProfiles()).contains("prod")) {
            StringWriter stackTrace = new StringWriter();
            ex.printStackTrace(new PrintWriter(stackTrace));
            
            restApiError.setDebugMessage(stackTrace.toString());
            restApiError.setBusinessException(ex);
        }
        
        return buildResponseEntity(restApiError);
    }
}
