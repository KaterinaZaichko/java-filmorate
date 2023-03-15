package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserService;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.service.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
@Data
@RequiredArgsConstructor
public class UserController {
    private final ValidateService validateService;
    private final UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.findAll();
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
}
