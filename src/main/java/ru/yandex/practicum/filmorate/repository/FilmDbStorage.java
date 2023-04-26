package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "           f.duration, mp.id AS mpa_id, mp.name AS mpa_name, GROUP_CONCAT(g.id) AS genres\n" +
                "FROM films AS f\n" +
                "LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id\n" +
                "LEFT JOIN genres AS g ON g.id = m2mfg.g_id\n" +
                "JOIN mpas AS mp ON mp.id = f.mpa\n" +
                "GROUP BY f.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findFilmById(int id) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "           f.duration, mp.id AS mpa_id, mp.name AS mpa_name, GROUP_CONCAT(g.id) AS genres\n" +
                "FROM films AS f " +
                "LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id " +
                "LEFT JOIN genres AS g ON g.id = m2mfg.g_id " +
                "JOIN mpas AS mp ON mp.id = f.mpa " +
                "WHERE f.id = ? " +
                "GROUP BY f.id";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } else {
            throw new FilmNotFoundException(String.format("Фильм c id %d не найден", id));
        }
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
            batchUpdate(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "SELECT id FROM films WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, film.getId());
        if (filmRows.next()) {
            sqlQuery = "UPDATE films " +
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
                    batchUpdate(film);
                }
            }
            Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
            genres.addAll(findGenresByFilmId(film.getId()));
            film.setGenres(genres);

            return film;
        } else {
            throw new FilmNotFoundException(String.format("Фильм c id %d не найден", film.getId()));
        }
    }

    public List<Film> getTopFilms(int count) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, " +
                "f.duration, mp.id AS mpa_id, mp.name AS mpa_name, GROUP_CONCAT(g.id) AS genres, " +
                "COUNT(m2ml.f_id) AS popularity\n" +
                "FROM films AS f\n" +
                "LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id\n" +
                "LEFT JOIN genres AS g ON g.id = m2mfg.g_id\n" +
                "LEFT JOIN m2m_likes AS m2ml ON f.id = m2ml.f_id\n" +
                "JOIN mpas AS mp ON mp.id = f.mpa\n" +
                "GROUP BY f.id\n" +
                "ORDER BY f.id DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    public List<Genre> findGenresByFilmId(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT id FROM films WHERE id = ?", id);
        if (genreRows.next()) {
            String sqlQuery = "SELECT g.id, g.name " +
                    "FROM genres AS g " +
                    "JOIN m2m_film_genre AS m2mfg ON m2mfg.g_id = g.id " +
                    "WHERE m2mfg.f_id = ?";
            return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        } else {
            throw new FilmNotFoundException(String.format("Фильм c id %d не найден", id));
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"),
                new Rating(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")),
                new HashSet<>());
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }

    private void batchUpdate(Film film) {
        List<Object[]> batch = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            Object[] values = new Object[]{
                    film.getId(), genre.getId()};
            batch.add(values);
        }
        jdbcTemplate.batchUpdate("INSERT INTO m2m_film_genre (f_id, g_id) VALUES (?, ?)", batch);
    }
}
