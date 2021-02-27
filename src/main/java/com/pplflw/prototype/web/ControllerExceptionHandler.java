package com.pplflw.prototype.web;

import com.pplflw.prototype.exceptions.ResourceNotFoundException;
import com.pplflw.prototype.exceptions.RestApiException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String error = "Malformed JSON request";
        return buildResponseEntity(new RestApiException(HttpStatus.BAD_REQUEST, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(RestApiException restApiException) {
        
        return new ResponseEntity<>(restApiException, restApiException.getStatus());
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(ResourceNotFoundException ex) {
        
        RestApiException restApiError = new RestApiException(HttpStatus.NOT_FOUND);
        restApiError.setMessage(ex.getMessage());
        return buildResponseEntity(restApiError);
    }
}
