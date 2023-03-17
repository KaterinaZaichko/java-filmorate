package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
@Data
@RequiredArgsConstructor
public class FilmController {
    private final ValidateService validateService;
    private final InMemoryFilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) throws ValidationException {
        validateService.validateFilm(film);
        filmService.save(film);
        log.info("Film had been created: {}", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) throws ValidationException {
        validateService.validateFilm(film);
        filmService.update(film);
        log.info("Film had been created or updated: {}", film);
        return film;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLikeToFilm(@PathVariable int filmId, @PathVariable @Positive int userId) {
        filmService.addLike(filmId, userId);
        log.info("User{} liked film {}", userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLikeFromFilm(@PathVariable int filmId, @PathVariable int userId) {
        if(userId < 0) {
            throw new UserNotFoundException(String.format("id %d не найден", userId));
        }
        filmService.deleteLike(filmId, userId);
        log.info("User{} disliked film {}", userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("Top films has been formed");
        return filmService.getTopFilms(count);
    }
}
