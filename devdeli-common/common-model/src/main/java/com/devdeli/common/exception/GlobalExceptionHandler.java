package com.devdeli.common.exception;

import com.devdeli.common.dto.response.ApiResponse;
import com.devdeli.common.enums.ErrorCode;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<?>> handleRuntimeExceptions(Exception exception){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .result(exception.getMessage())
                .build();
        log.error(exception.toString());
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handleForbiddenExceptions(){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(ErrorCode.FORBIDDEN_EXCEPTION.getCode())
                .message(ErrorCode.FORBIDDEN_EXCEPTION.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.FORBIDDEN_EXCEPTION.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AppExceptions.class)
    ResponseEntity<ApiResponse<?>> handleAppExceptions(AppExceptions exceptions){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(exceptions.getErrorCode().getCode())
                .message(exceptions.getErrorCode().getMessage())
                .build();
        return ResponseEntity.status(exceptions.getErrorCode().getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception){
        String enumKey = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try{
            errorCode = ErrorCode.valueOf(enumKey);
        }
        catch (Exception ignored){}
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = JsonParseException.class)
    ResponseEntity<ApiResponse<?>> handleJsonParseException(JsonParseException exception){
        ErrorCode errorCode = ErrorCode.UNKNOWN_REQUEST;
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .result(exception.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
