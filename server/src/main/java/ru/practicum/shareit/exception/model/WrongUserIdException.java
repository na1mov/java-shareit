package ru.practicum.shareit.exception.model;

public class WrongUserIdException extends RuntimeException {
    public WrongUserIdException(String message) {
        super(message);
    }
}
