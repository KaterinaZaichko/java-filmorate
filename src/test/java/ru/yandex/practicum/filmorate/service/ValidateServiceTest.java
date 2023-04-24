package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidateServiceTest {
    ValidateService validateService = new ValidateService();

    @Test
    public void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User(1, "mail@mail.ru", "login login", "Name",
                LocalDate.of(1895, 12, 28));
        assertThrows(ConstraintViolationException.class, () -> validateService.validateUser(user));
    }

    @Test
    public void shouldTrueWhenNameIsNull() {
        User user = new User(1, "mail@mail.ru", "login", null,
                LocalDate.of(1895, 12, 28));
        validateService.validateUser(user);
        assertEquals(user.getLogin(), user.getName());
    }
}