package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.service.ValidateService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final ValidateService validateService;
    private final UserServiceImpl userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        validateService.validateUser(user);
        userService.save(user);
        log.info("User had been created: {}", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody @Valid User user) {
        validateService.validateUser(user);
        userService.update(user);
        log.info("User had been created or updated: {}", user);
        return user;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addToFriends(@PathVariable int userId, @PathVariable int friendId) {
        userService.getUserById(friendId);
        userService.addToFriends(userId, friendId);
        log.info("User {} had been added to {}", userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable int userId, @PathVariable int friendId) {
        userService.deleteFromFriends(userId, friendId);
        log.info("Friend {} had been deleted from {}", friendId, userId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable int userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable int userId, @PathVariable int otherUserId) {
        return userService.getMutualFriends(userId, otherUserId);
    }
}
