package ru.practicum.shareit.exception.model;

public class MyValidationException extends RuntimeException {
    public MyValidationException(final String message) {
        super(message);
    }
}
