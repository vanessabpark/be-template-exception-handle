package com.springboot.advice;


import com.springboot.reponse.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice // controller 계층에서 나오는 예외는 내가 처리한다
public class GlobalExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity handleException(MethodArgumentNotValidException e) {
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        BindingResult bindingResult = e.getBindingResult();
        ErrorResponse response = ErrorResponse.of(bindingResult);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);

//        List<ErrorResponse.FieldError> errors =
//                fieldErrors.stream()
//                .map(error -> new ErrorResponse.FieldError(
//                        error.getField(),
//                        error.getRejectedValue(),
//                        error.getDefaultMessage()
//                )).collect(Collectors.toList());
//
//        return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        ErrorResponse response = ErrorResponse.of(constraintViolations);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }
}
