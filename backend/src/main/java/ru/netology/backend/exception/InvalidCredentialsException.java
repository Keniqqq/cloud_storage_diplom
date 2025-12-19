package ru.netology.backend.exception;

public class InvalidCredentialsException extends Throwable {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
