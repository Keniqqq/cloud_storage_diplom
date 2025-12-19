package ru.netology.backend.exception;

public class UnauthorizedException extends Throwable {
    public UnauthorizedException(String message) {
        super(message);
    }
}
