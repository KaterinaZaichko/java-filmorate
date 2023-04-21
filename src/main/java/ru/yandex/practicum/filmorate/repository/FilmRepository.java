package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmRepository {
    List<Film> findAll();

    Film findFilmById(int id);

    Film save(Film film);

    Film update(Film film) throws ValidationException;

    List<Film> getTopFilms(int count);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
