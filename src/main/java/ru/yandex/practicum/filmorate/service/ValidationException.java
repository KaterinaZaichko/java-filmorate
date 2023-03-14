package ru.yandex.practicum.filmorate.service;

public class ValidationException extends Exception{
    public ValidationException(String message) {
        super(message);
    }
}
