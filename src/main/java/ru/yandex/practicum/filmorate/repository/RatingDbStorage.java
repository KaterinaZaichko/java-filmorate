package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RatingDbStorage implements RatingRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> findAll() {
        String sqlQuery = "SELECT id, name FROM mpas ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating findRatingById(int id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet(
                "SELECT id, name FROM mpas WHERE id = ?", id);

        if (ratingRows.next()) {
            return new Rating(ratingRows.getInt("id"), ratingRows.getString("name"));
        } else {
            throw new FilmNotFoundException(String.format("Рейтинг c id %d не найден", id));
        }
    }

    private Rating mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return new Rating(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
