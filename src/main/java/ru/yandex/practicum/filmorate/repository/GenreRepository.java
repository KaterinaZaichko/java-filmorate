package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {

    List<Genre> findAll();

    Genre findGenreById(int id);
}
