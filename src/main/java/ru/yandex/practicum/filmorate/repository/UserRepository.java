package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User findUserById(int id);

    User save(User user);

    User update(User user) throws ValidationException;

    void addToFriends(int userId, int friendId);

    void deleteFromFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int otherUserId);
}
