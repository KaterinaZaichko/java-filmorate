package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmRepository filmRepository;

    @Autowired
    public InMemoryFilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public List<Film> getAll() {
        return filmRepository.findAll();
    }

    @Override
    public Film getFilmById(int id) {
        return filmRepository.findFilmById(id);
    }

    @Override
    public Film save(Film film) {
        return filmRepository.save(film);
    }

    @Override
    public Film update(Film film) throws ValidationException {
        return filmRepository.update(film);
    }

    @Override
    public void addLike(int filmId, int userId) {
        filmRepository.findFilmById(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        filmRepository.findFilmById(filmId).getLikes().remove(userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmRepository.findAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
