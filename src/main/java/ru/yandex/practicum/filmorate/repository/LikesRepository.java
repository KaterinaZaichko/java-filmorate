package ru.yandex.practicum.filmorate.repository;

public interface LikesRepository {
    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
