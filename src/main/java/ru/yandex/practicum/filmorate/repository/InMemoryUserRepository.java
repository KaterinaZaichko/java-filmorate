package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserRepository implements UserRepository{
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
    public User save(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) throws ValidationException {
        if(!users.containsKey(user.getId())) {
            throw new ValidationException("User with this id doesn't exist");
        }
        users.put(user.getId(), user);
        return user;
    }
}
