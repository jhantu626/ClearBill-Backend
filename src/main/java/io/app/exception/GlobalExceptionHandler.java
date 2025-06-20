package io.app.exception;

import io.app.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleResouceNotFoundException(ResourceNotFoundException ex){
        return ApiResponse.builder()
                .message(ex.getMessage())
                .status(false)
                .build();
    }

    @ExceptionHandler(UnAuthrizeException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ApiResponse hadnleUnAuthrizeException(UnAuthrizeException exception){
        return ApiResponse.builder()
                .message(exception.getMessage())
                .status(false)
                .build();
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse handleDuplicateResourceException(DuplicateResourceException exception){
        return ApiResponse.builder()
                .message(exception.getMessage())
                .status(false)
                .build();
    }
}
