package com.example.expense_tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotOwnerException extends RuntimeException {

    public NotOwnerException(String message) {
        super(message);
    }


}