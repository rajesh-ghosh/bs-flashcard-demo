package org.bigspring.service;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler  {

    Logger logger = Logger.getLogger(GlobalErrorHandler.class.getName());

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class, EntityNotFoundException.class, DataIntegrityViolationException.class,
            RuntimeException.class, Exception.class})
    protected ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {

        logger.log(Level.SEVERE, ex.getMessage(), ex);

        StringBuilder respBody = new StringBuilder(100);
        HttpStatus stat = HttpStatus.INTERNAL_SERVER_ERROR;
        String accepts = request.getHeader("Accept");

        StringBuilder builder = new StringBuilder("1: ").append(ex.getMessage().replaceAll("\"", "`"));

        Throwable cause2 = ex.getCause();
        if (cause2 != null) {
            builder.append(" 2: ").append(cause2.getMessage().replaceAll("\"", "`") );
            Throwable cause3 = cause2.getCause();
            if (cause3 != null)
                builder.append(" 3: ").append(cause3.getMessage().replaceAll("\"", "`"));
        }

        if (IllegalArgumentException.class.isAssignableFrom(ex.getClass())) {
            respBody.append("{ error_type : \"INVALID_PARAMETER_OR_PROGRAM_ARGUMENT\", error_message : \"" + builder.toString() + "\" }");
            stat = HttpStatus.BAD_REQUEST;
        } else if (IllegalStateException.class.isAssignableFrom(ex.getClass())) {
            respBody.append("{ error_type : \"INVALID_APPLICATION_STATE\", error_message : \"" + builder.toString() + "\" }");
            stat = HttpStatus.CONFLICT;
        } else if (DataIntegrityViolationException.class.isAssignableFrom(ex.getClass())) {
            respBody.append("{ error_type : \"DATA_INTEGRITY_ISSUE\", error_message : \"" + builder.toString() + "\" }");
            stat = HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (EntityNotFoundException.class.isAssignableFrom(ex.getClass())) {
            respBody.append("{ error_type : \"DATABASE_ENTITY_NOT_FOUND\", error_message : \"" + builder.toString() + "\" }");
            stat = HttpStatus.UNPROCESSABLE_ENTITY;
        } else {
            respBody.append("{ error_type : \"MISCELLANEOUS\", error_message : \"" + builder.toString() + "\" }");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return handleExceptionInternal(ex, respBody.toString(), headers, stat, request);

    }
}
