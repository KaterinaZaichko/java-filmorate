package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        validateFilmId(filmId);
        validateUserId(userId);
        String sqlQuery = "INSERT INTO m2m_likes (f_id, u_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateFilmId(filmId);
        validateUserId(userId);
        String sqlQuery = "DELETE FROM m2m_likes WHERE f_id = ? AND u_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private void validateFilmId(int filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "SELECT id FROM films WHERE id = ?", filmId);
        if (!filmRows.next()) {
            throw new FilmNotFoundException(String.format("Фильм c id %d не найден", filmId));
        }
    }

    private void validateUserId(int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT id FROM users WHERE id = ?", userId);
        if (!userRows.next()) {
            throw new UserNotFoundException(String.format("Пользователь c id %d не найден", userId));
        }
    }
}
