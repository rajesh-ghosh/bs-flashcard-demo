package org.bigspring.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler  {

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {

        StringBuilder respBody = new StringBuilder(100);
        HttpStatus stat = HttpStatus.INTERNAL_SERVER_ERROR;
        String accepts = request.getHeader("Accept");

        if (IllegalArgumentException.class.isAssignableFrom(ex.getClass())) {
            respBody.append("{ error_type : \"INVALID_PARAMETER_OR_PROGRAM_ARGUMENT\", error_message : \"" + ex.getMessage() + "\" }");
            stat = HttpStatus.BAD_REQUEST;
        } else if (IllegalStateException.class.isAssignableFrom(ex.getClass())) {
            respBody.append("{ error_type : \"INVALID_APPLICATION_STATE\", error_message : \"" + ex.getMessage() + "\" }");
            stat = HttpStatus.CONFLICT;
        } else {
            respBody.append("{ error_type : \"MISCELLANEOUS\", error_message : \"" + ex.getMessage() + "\" }");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return handleExceptionInternal(ex, respBody.toString(), headers, stat, request);

    }
}
