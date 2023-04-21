package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void validateName() {
        Film film = new Film(1, "", "Description",
                LocalDate.of(1895, 12, 27), 120,
                new Rating(1, "Rating"), new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Name is empty");
    }

    @Test
    public void validateDescription() {
        Film film = new Film(1, "Name", "Description".repeat(20),
                LocalDate.of(1895, 12, 27), 120,
                new Rating(1, "Rating"), new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Description size should be no more than 200 characters");
    }

    @Test
    public void validateDuration() {
        Film film = new Film(1, "Name", "Description",
                LocalDate.of(1895, 12, 27), -120,
                new Rating(1, "Rating"), new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size(), "Duration must be positive");
    }
}