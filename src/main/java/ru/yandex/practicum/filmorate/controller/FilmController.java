package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmService;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.service.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
@Data
@RequiredArgsConstructor
public class FilmController {
    private final ValidateService validateService;
    private final FilmService filmService;

    @GetMapping
    public List<Film> getFilms() {
        return filmService.findAll();
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
}
