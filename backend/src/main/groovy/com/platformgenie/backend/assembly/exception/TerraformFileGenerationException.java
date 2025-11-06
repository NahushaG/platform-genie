package com.platformgenie.backend.assembly.exception;

public class TerraformFileGenerationException extends RuntimeException {
    public TerraformFileGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
