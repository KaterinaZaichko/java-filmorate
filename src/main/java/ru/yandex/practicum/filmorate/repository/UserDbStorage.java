package ru.yandex.practicum.filmorate.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("userDbStorage")
public class UserDbStorage implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users ORDER BY id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findUserById(int id) {
        validateUserId(id);
        String sqlQuery = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
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
    public User update(User user) throws ValidationException {
        validateUserId(user.getId());
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE  id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return findUserById(user.getId());
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT u_id, friend_id, status FROM m2m_friends " +
                        "WHERE u_id = ? AND friend_id = ? AND status = false", friendId, userId);
        if (!userRows.next()) {
            String sqlQuery = "INSERT INTO m2m_friends (u_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId, false);
        } else {
            String sqlQuery = "UPDATE m2m_friends SET status = ? WHERE u_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, true, friendId, userId);
            sqlQuery = "INSERT INTO m2m_friends (u_id, friend_id, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sqlQuery, userId, friendId, true);
        }
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT u_id, friend_id, status FROM m2m_friends " +
                        "WHERE u_id = ? AND friend_id = ? AND status = false", friendId, userId);
        if (userRows.next()) {
            String sqlQuery = "UPDATE m2m_friends SET status = ? WHERE u_id = ? AND friend_id = ?";
            jdbcTemplate.update(sqlQuery, false, friendId, userId);
        }
        String sqlQuery = "DELETE FROM m2m_friends WHERE u_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        validateUserId(userId);
        String sqlQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday\n" +
                "FROM m2m_friends AS m2mf\n" +
                "JOIN users AS u ON u.id = m2mf.friend_id\n" +
                "WHERE m2mf.u_id = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        String sqlQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM (SELECT m2mf.friend_id AS friends " +
                "       FROM users AS u " +
                "       JOIN m2m_friends AS m2mf ON u.id = m2mf.u_id" +
                "       WHERE u.id IN (?, ?)" +
                "       GROUP by m2mf.friend_id" +
                "       HAVING COUNT(m2mf.friend_id) > 1) AS t " +
                "LEFT JOIN users AS u ON t.friends = u.id " +
                "GROUP BY u.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());
    }

    private void validateUserId(int userId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "SELECT id FROM users WHERE id = ?", userId);
        if (!userRows.next()) {
            throw new UserNotFoundException(String.format("Пользователь c id %d не найден", userId));
        }
    }
}