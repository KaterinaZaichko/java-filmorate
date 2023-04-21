package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT id, name FROM genres ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre findGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(
                "SELECT id, name FROM genres WHERE id = ?", id);

        if (genreRows.next()) {
            return new Genre(genreRows.getInt("id"), genreRows.getString("name"));
        } else {
            throw new FilmNotFoundException(String.format("Жанр c id %d не найден", id));
        }
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

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
