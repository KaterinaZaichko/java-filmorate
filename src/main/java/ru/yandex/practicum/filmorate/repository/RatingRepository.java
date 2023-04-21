package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingRepository {
    List<Rating> findAll();

    Rating findRatingById(int id);
}
