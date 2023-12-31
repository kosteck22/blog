package com.example.blog.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class DefaultExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleException(ResourceNotFoundException e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                List.of(e.getMessage()),
                HttpStatus.NOT_FOUND.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleException(DuplicateResourceException e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                List.of(e.getMessage()),
                HttpStatus.CONFLICT.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidUsernameOrPasswordException.class)
    public ResponseEntity<ApiError> handleException(InvalidUsernameOrPasswordException e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                List.of(e.getMessage()),
                HttpStatus.UNAUTHORIZED.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ApiError> handleException(RequestValidationException e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                List.of(e.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomAuthorizationException.class)
    public ResponseEntity<ApiError> handleException(CustomAuthorizationException e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                List.of(e.getMessage()),
                HttpStatus.FORBIDDEN.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        List<String> fieldErrors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .toList();

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                fieldErrors,
                HttpStatus.BAD_REQUEST.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e, HttpServletRequest request) {
        LOGGER.error(e.getMessage(), e);

        ApiError apiError = new ApiError(
                request.getRequestURI(),
                List.of(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date()
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
