package com.wanted.global.exception;

import com.wanted.global.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appExceptionHandler(AppException e) {

        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(Response.error(e.getErrorCode().getMessage()));

    }

    @ExceptionHandler(BindingException.class)
    public ResponseEntity<?> bindingExceptionHandler(BindingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.error(e.getMessage()));
    }

}