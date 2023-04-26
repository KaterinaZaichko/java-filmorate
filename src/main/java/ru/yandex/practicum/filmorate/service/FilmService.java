package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> getAll();

    Film getFilmById(int id);

    Film save(Film film);

    Film update(Film film);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getTopFilms(int count);
}
