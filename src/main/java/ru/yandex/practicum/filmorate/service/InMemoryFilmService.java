package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class InMemoryFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    @Autowired
    public InMemoryFilmService(@Qualifier("filmDbStorage") FilmRepository filmRepository,
                               @Qualifier("userDbStorage") UserRepository userRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
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
        userRepository.findUserById(userId);
        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        filmRepository.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmRepository.getTopFilms(count);
    }
}
