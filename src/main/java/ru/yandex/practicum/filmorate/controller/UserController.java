package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.UserDAO;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ValidateService;
import ru.yandex.practicum.filmorate.service.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {
    private final ValidateService validateService;
    private final UserDAO repository;

    public UserController(ValidateService validateService, UserDAO repository) {
        this.validateService = validateService;
        this.repository = repository;
    }

    @GetMapping
    public List<User> getUsers() {
        return repository.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody @Valid User user) throws ValidationException {
        validateService.validateUser(user);
        repository.save(user);
        log.info("User had been created: {}", user);
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody @Valid User user) throws ValidationException {
        validateService.validateUser(user);
        repository.update(user);
        log.info("User had been created or updated: {}", user);
        return user;
    }
}
