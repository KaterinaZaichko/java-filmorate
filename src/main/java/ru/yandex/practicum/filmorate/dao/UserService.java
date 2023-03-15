package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final Map<Integer, User> users = new HashMap<>();
    private int usersCount = 0;

    public int generateId() {
        return ++usersCount;
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) throws ValidationException {
        if(!users.containsKey(user.getId())) {
            throw new ValidationException("User with this id doesn't exist");
        }
        users.put(user.getId(), user);
        return user;
    }
}
