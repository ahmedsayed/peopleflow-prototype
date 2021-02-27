package com.pplflw.prototype.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class RestApiException {

   private HttpStatus status;
   
   @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S]")
   private LocalDateTime timestamp;
   
   private String message;
   private String debugMessage;
   private List<BusinessException> businessExceptions;

   private RestApiException() {
       this.timestamp = LocalDateTime.now();
   }

   public RestApiException(HttpStatus status) {
       this();
       this.status = status;
   }

   public RestApiException(HttpStatus status, Throwable ex) {
       this();
       this.status = status;
       this.message = "Unexpected error";
       this.debugMessage = ex.getLocalizedMessage();
   }

   public RestApiException(HttpStatus status, String message, Throwable ex) {
       this();
       this.status = status;
       this.message = message;
       this.debugMessage = ex.getLocalizedMessage();
   }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public List<BusinessException> getBusinessExceptions() {
        return businessExceptions;
    }

    public void setBusinessExceptions(List<BusinessException> businessExceptions) {
        this.businessExceptions = businessExceptions;
    }
}
