package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getUserById(int id);

    User save(User user);

    User update(User user);

    void addToFriends(int userId, int friendId);

    void deleteFromFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int otherUserId);
}
