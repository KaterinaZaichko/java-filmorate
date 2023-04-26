package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsRepository {
    void addToFriends(int userId, int friendId);

    void deleteFromFriends(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getMutualFriends(int userId, int otherUserId);
}
