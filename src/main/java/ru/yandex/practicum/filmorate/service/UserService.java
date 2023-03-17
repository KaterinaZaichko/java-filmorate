package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getUserById(int id);

    User save(User user);

    User update(User user) throws ValidationException;

    void addToFriends(int userId, int friendId);

    void removeFromFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int otherUserId);
}
