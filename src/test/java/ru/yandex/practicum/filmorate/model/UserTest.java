package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void validateEmail() {
        User user = new User(1, "mail.ru", "login", "Name",
                LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Email doesn't have format of email");
    }

    @Test
    public void validateLogin() {
        User user = new User(1, "mail@mail.ru", "login login", "Name",
                LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "User login contains spaces");
    }

    @Test
    public void validateName() {
        User user = new User(1, "mail@mail.ru", "", "Name",
                LocalDate.of(1895, 12, 28));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Login is empty");
    }

    @Test
    public void validateBirthday() {
        User user = new User(1, "mail@mail.ru", "login", "Name",
                LocalDate.of(2895, 12, 28));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size(), "Birthday must be in past");
    }
}