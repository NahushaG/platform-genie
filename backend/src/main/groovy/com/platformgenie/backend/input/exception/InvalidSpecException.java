package com.platformgenie.backend.input.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSpecException extends RuntimeException {
    public InvalidSpecException(String message) {
        super(message);
    }
}
