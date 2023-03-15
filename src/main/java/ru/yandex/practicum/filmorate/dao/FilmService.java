package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmsCount = 0;

    private int generateId() {
        return ++filmsCount;
    }

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film save(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film film) throws ValidationException {
        if(!films.containsKey(film.getId())) {
            throw new ValidationException("Film with this id doesn't exist");
        } else {
            films.put(film.getId(), film);
        }
        return film;
    }
}
