package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService{
    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) throws ValidationException {
        return userRepository.update(user);
    }

    @Override
    public void addToFriends(int userId, int friendId) {
        userRepository.findUserById(userId).getFriends().add(friendId);
        userRepository.findUserById(friendId).getFriends().add(userId);
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        if (!getUserById(userId).getFriends().contains(friendId))  {
            throw new UserNotFoundException(String.format("id %d не найден", friendId));
        }
        if (!getUserById(friendId).getFriends().contains(userId))  {
            throw new UserNotFoundException(String.format("id %d не найден", friendId));
        }
        userRepository.findUserById(userId).getFriends().remove(friendId);
        userRepository.findUserById(friendId).getFriends().remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        return userRepository.findUserById(userId).getFriends().stream()
                .map(userRepository::findUserById)
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