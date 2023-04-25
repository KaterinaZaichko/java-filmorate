package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public class ReleaseDateConstraintValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    public static final LocalDate RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private int releaseDate;

    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        this.releaseDate = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return !localDate.isBefore(LocalDate.of(1895, Month.DECEMBER, 28));
    }
}
