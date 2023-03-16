package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@Component
@Slf4j
public class ValidateService {
    public void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Release date is before 28.12.1895: {}", film.getReleaseDate());
            throw new ValidationException("Invalid release date");
        }
    }

    public void validateUser(User user) throws ValidationException {
        if (user.getLogin().contains(" ")) {
            log.warn("User login contains spaces: {}", user.getLogin());
            throw new ValidationException("Invalid login");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
