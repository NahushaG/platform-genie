package com.platformgenie.backend.exception;

import com.platformgenie.backend.assembly.exception.TerraformFileGenerationException;
import com.platformgenie.backend.delivery.exception.DeliveryException;
import com.platformgenie.backend.input.exception.InvalidSpecException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSpecException.class)
    public ResponseEntity<String> handleInvalidSpec(InvalidSpecException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError().body("Internal Error: " + ex.getMessage());
    }

    @ExceptionHandler(TerraformFileGenerationException.class)
    public ResponseEntity<String> handleTerraformException(TerraformFileGenerationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Terraform file generation failed: " + ex.getMessage());
    }

    @ExceptionHandler(DeliveryException.class)
    public ResponseEntity<String> handleDeliveryException(DeliveryException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Delivery Layer Error: " + ex.getMessage());
    }
}
