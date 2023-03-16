package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.List;

public interface FilmRepository{
    List<Film> findAll();

    Film save(Film film);

    Film update(Film film) throws ValidationException;
}
