package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private int usersCount = 0;

    public int generateId() {
        return ++usersCount;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(int id) {
        return users.values().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь № %d не найден", id)));
    }

    @Override
    public User save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", user.getId()));
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        findUserById(userId).getFriends().add(friendId);
        findUserById(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        findUserById(userId).getFriends().remove(friendId);
        findUserById(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        return findUserById(userId).getFriends().stream()
                .map(this::findUserById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        List<User> userFriends = getFriends(userId);
        List<User> otherUserFriends = getFriends(otherUserId);
        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .collect(Collectors.toList());
    }
}
