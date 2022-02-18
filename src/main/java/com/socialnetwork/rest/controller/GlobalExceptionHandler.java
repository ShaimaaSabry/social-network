package com.socialnetwork.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        List<FieldError> fieldErrorsList = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrorsList) {
            String field = fieldError.getField();
            String error = fieldError.getDefaultMessage();
            fieldErrors.put(field, error);
        }

        Map<String, String> globalErrors = new HashMap<>();
        List<ObjectError> globalErrorsList = ex.getBindingResult().getGlobalErrors();
        for (ObjectError objectError : globalErrorsList) {
            String error = objectError.getDefaultMessage();
            globalErrors.put("error", error);
        }

        Map<String, Object> validationErrors = new LinkedHashMap<>();
        validationErrors.put("fieldErrors", fieldErrors);
        validationErrors.put("globalErrors", globalErrors);
        return ResponseEntity.badRequest().headers(headers).body(validationErrors);
    }
}
