package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RatingDbStorage ratingDbStorage;
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, RatingDbStorage ratingDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingDbStorage = ratingDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "           f.duration, f.mpa, GROUP_CONCAT(g.id) AS genres\n" +
                "FROM films AS f\n" +
                "LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id\n" +
                "LEFT JOIN genres AS g ON g.id = m2mfg.g_id\n" +
                "GROUP BY f.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findFilmById(int id) {
        validateFilmId(id);
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "           f.duration, f.mpa, GROUP_CONCAT(g.id) AS genres " +
                "FROM films AS f " +
                "LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id " +
                "LEFT JOIN genres AS g ON g.id = m2mfg.g_id " +
                "WHERE f.id = ? " +
                "GROUP BY f.id";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    @Override
    public Film save(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa", film.getMpa().getId());
        film.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "INSERT INTO m2m_film_genre (f_id, g_id) VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) throws ValidationException {
        validateFilmId(film.getId());
        String sqlQuery = "UPDATE films " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "WHERE id = ? ";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            sqlQuery = "DELETE FROM m2m_film_genre WHERE f_id = ?";
            jdbcTemplate.update(sqlQuery, film.getId());

            if (film.getGenres().size() != 0) {
                for (Genre genre : film.getGenres()) {
                    sqlQuery = "INSERT INTO m2m_film_genre (f_id, g_id) VALUES (?, ?)";
                    jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
                }
            }
        }
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        genres.addAll(genreDbStorage.findGenresByFilmId(film.getId()));
        film.setGenres(genres);

        return findFilmById(film.getId());
    }

    @Override
    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "f.duration, f.mpa, GROUP_CONCAT(g.id) AS genres, COUNT(m2ml.f_id) AS popularity\n" +
                "FROM films AS f\n" +
                "LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id\n" +
                "LEFT JOIN genres AS g ON g.id = m2mfg.g_id\n" +
                "LEFT JOIN m2m_likes AS m2ml ON f.id = m2ml.f_id\n" +
                "GROUP BY f.id\n" +
                "ORDER BY f.id DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

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

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"),
                ratingDbStorage.findRatingById(resultSet.getInt("mpa")),
                new HashSet<>(genreDbStorage.findGenresByFilmId(resultSet.getInt("id"))));
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
