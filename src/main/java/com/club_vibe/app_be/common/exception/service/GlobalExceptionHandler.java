package com.club_vibe.app_be.common.exception.service;

import com.club_vibe.app_be.common.exception.EmailAlreadyExistsException;
import com.club_vibe.app_be.common.exception.ErrorResponse;
import com.club_vibe.app_be.common.exception.InvalidTokenException;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.stripe.exception.StripeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            ItemNotFoundException ex
    ) {
        ErrorResponse errorResponse = new ErrorResponse(
                "ITEM_NOT_FOUND",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(
            InvalidTokenException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<String> handleStripeException(
            StripeException ex
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe API error: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred" + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}