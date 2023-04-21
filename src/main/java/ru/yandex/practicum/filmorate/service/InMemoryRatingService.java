package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.RatingRepository;

import java.util.List;

@Service
public class InMemoryRatingService implements RatingService{
    private final RatingRepository ratingRepository;

    @Autowired
    public InMemoryRatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }


    @Override
    public List<Rating> getRatings() {
        return ratingRepository.findAll();
    }

    @Override
    public Rating getRatingById(int id) {
        return ratingRepository.findRatingById(id);
    }
}
