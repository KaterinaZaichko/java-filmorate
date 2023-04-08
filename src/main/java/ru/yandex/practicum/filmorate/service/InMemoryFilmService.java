package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

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
        filmRepository.findFilmById(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        if (!getFilmById(filmId).getLikes().contains(userId))  {
            throw new UserNotFoundException(String.format("id %d не найден", userId));
        }
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
