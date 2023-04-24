package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findUserById(int id) {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } else {
            throw new UserNotFoundException(String.format("Пользователь c id %d не найден", id));
        }
    }

    @Override
    public User save(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        user.setId(simpleJdbcInsert.executeAndReturnKey(values).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user.getId());
        if (userRows.next()) {
            sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE  id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId()
            );
            return user;
        } else {
            throw new UserNotFoundException(String.format("Пользователь c id %d не найден", user.getId()));
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());
    }
}