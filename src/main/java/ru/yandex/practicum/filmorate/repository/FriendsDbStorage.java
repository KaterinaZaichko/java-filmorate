package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsRepository {
    private final JdbcTemplate jdbcTemplate;

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
                "FROM users AS u, m2m_friends AS f, m2m_friends AS o " +
                "WHERE u.id = f.friend_id AND u.id = o.friend_id AND f.u_id = ? AND o.u_id = ?";
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
