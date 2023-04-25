package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendsRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FriendsRepository friendsRepository) {
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
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
        friendsRepository.addToFriends(userId, friendId);
    }

    @Override
    public void deleteFromFriends(int userId, int friendId) {
        friendsRepository.deleteFromFriends(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        userRepository.findUserById(userId);
        return friendsRepository.getFriends(userId);
    }

    @Override
    public List<User> getMutualFriends(int userId, int otherUserId) {
        return friendsRepository.getMutualFriends(userId, otherUserId);
    }
}
