package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmRepository implements FilmRepository{
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmsCount = 0;

    public int generateId() {
        return ++filmsCount;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(int id) {
        return films.values().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм № %d не найден", id)));
    }

    @Override
    public Film save(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        if(!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Film with this id doesn't exist");
        }
        films.put(film.getId(), film);
        return film;
    }
}
