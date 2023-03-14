package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.FilmDAO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.service.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    private final ValidateService validateService;
    private final FilmDAO repository;

    public FilmController(ValidateService validateService, FilmDAO repository) {
        this.validateService = validateService;
        this.repository = repository;
    }

    @GetMapping
    public List<Film> getFilms() {
        return repository.getFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) throws ValidationException {
        validateService.validateFilm(film);
        repository.save(film);
        log.info("Film had been created: {}", film);
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody @Valid Film film) throws ValidationException {
        validateService.validateFilm(film);
        repository.update(film);
        log.info("Film had been created or updated: {}", film);
        return film;
    }
}
