package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAll();

    Film getFilmById(int id);

    Film save(Film film);

    Film update(Film film) throws ValidationException;

    void addLike(int filmId, int UserId);

    void deleteLike(int filmId, int UserId);

    List<Film> getTopFilms(int count);
}
