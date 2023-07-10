package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(value = CONFLICT)
public class ValidationException409 extends RuntimeException {
    public ValidationException409(String message) {
        super(message);
    }
}