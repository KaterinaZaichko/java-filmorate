package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.LikesRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public FilmServiceImpl(FilmRepository filmRepository, UserRepository userRepository, LikesRepository likesRepository, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.likesRepository = likesRepository;
        this.genreRepository = genreRepository;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = filmRepository.findAll();
        genreRepository.findGenresForAllFilms(films);
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = filmRepository.findFilmById(id);
        genreRepository.findGenresForAllFilms(List.of(film));
        return film;
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
        likesRepository.addLike(filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        likesRepository.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(int count) {
        return filmRepository.getTopFilms(count);
    }
}
