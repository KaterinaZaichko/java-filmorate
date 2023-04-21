package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class InMemoryUserService implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public InMemoryUserService(@Qualifier("userDbStorage") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        userRepository.addToFriends(userId, friendId);
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        userRepository.deleteFromFriends(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        return userRepository.getFriends(userId);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        return userRepository.getMutualFriends(userId, otherUserId);
    }
}
