package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateServiceTest {
    ValidateService validateService = new ValidateService();

    @Test
    public void shouldTrueWhenNameIsNull() {
        User user = new User(1, "mail@mail.ru", "login", null,
                LocalDate.of(1895, 12, 28));
        validateService.validateUser(user);
        assertEquals(user.getLogin(), user.getName());
    }
}