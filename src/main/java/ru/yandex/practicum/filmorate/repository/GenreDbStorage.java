package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreRepository {
    private final JdbcTemplate jdbcTemplate;

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

    @Override
    public void findGenresForAllFilms(List<Film> films) {
        Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sqlQuery = "SELECT m2mfg.f_id, g.id, g.name " +
                "FROM genres AS g, m2m_film_genre AS m2mfg " +
                "WHERE m2mfg.g_id = g.id AND m2mfg.f_id IN (" + inSql + ")";
        jdbcTemplate.query(sqlQuery, (rs) -> {
                    Film film = filmById.get(rs.getInt("f_id"));
                    film.addGenre(mapRowToGenre(rs, 0));
                },
                films.stream().map(Film::getId).toArray());
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
