package com.springboot.reponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Getter
//@AllArgsConstructor

//public class ErrorResponse {
//    private List<FieldError> fieldErrors;
//
//    @Getter
//    @AllArgsConstructor
//    public static class FieldError {
//        private String field;
//        private Object rejectedValue;
//        private String reason;
//    }
//}

@Getter
public class ErrorResponse {
    private List<FieldError> fieldErrors; // (1)
    private List<ConstraintViolationError> violationErrors;  // (2)

    // (3)는 (4) (5)에서 생성자를 쓴다. 해당 번호 코드에 보면 new 다음으로 사용하고 있다.
    private ErrorResponse(List<FieldError> fieldErrors, List<ConstraintViolationError> violationErrors) {
        this.fieldErrors = fieldErrors;
        this.violationErrors = violationErrors;
    }

    // (4) BindingResult에 대한 ErrorResponse 객체 생성
    public static ErrorResponse of(BindingResult bindingResult) {
        return new ErrorResponse(FieldError.of(bindingResult), null);
    }

    // (5) Set<ConstraintViolation<?>> 객체에 대한 ErrorResponse 객체 생성
    public static ErrorResponse of(Set<ConstraintViolation<?>> violations) {
        return new ErrorResponse(null, ConstraintViolationError.of(violations));
    }

    // (6) Field Error 가공
    @Getter
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String reason;

        private FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors =
                    bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ?
                                    "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }

    // (7) ConstraintViolation Error 가공
    @Getter
    public static class ConstraintViolationError { // iterator 속성이 자체적으로 없다 그래서 Set 안에 넣는거야. entrySet
        private String propertyPath; // 멤버에
        private Object rejectedValue; // 1번이
        private String reason; // 왜 틀렸는지

        private ConstraintViolationError(String propertyPath, Object rejectedValue,
                                         String reason) {
            this.propertyPath = propertyPath;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static List<ConstraintViolationError> of( // static이 나오면 new 안쓰고도 쓸 수 있다
                Set<ConstraintViolation<?>> constraintViolations) {
            return constraintViolations.stream()
                    .map(constraintViolation -> new ConstraintViolationError(
                            constraintViolation.getPropertyPath().toString(), //propertyPath 받고
                            constraintViolation.getInvalidValue().toString(), //rejectedValue를 받고
                            constraintViolation.getMessage() // reason을 받는다
                    )).collect(Collectors.toList());
        }
    }
}

