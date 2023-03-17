package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@Data
@RequiredArgsConstructor
public class UserController {
    private final ValidateService validateService;
    private final InMemoryUserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) throws ValidationException {
        validateService.validateUser(user);
        userService.save(user);
        log.info("User had been created: {}", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        validateService.validateUser(user);
        userService.update(user);
        log.info("User had been created or updated: {}", user);
        return user;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addToFriends(@PathVariable int userId, @PathVariable int friendId) {
        if(friendId < 0) {
            throw new UserNotFoundException(String.format("id %d не найден", friendId));
        }
        userService.addToFriends(userId, friendId);
        log.info("User {} had been added to {}", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable int userId, @PathVariable @Positive int friendId) {
        userService.removeFromFriends(userId, friendId);
        log.info("Friend {} had been deleted from {}", friendId, userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable @Positive int otherUserId) {
        return userService.getMutualFriends(userId, otherUserId);
    }
}
