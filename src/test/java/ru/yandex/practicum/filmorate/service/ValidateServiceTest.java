//package ru.yandex.practicum.filmorate.service;
//
//import org.junit.jupiter.api.Test;
//import ru.yandex.practicum.filmorate.exceptions.ValidationException;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ValidateServiceTest {
//    ValidateService validateService = new ValidateService();
//
//    @Test
//    public void shouldThrowExceptionWhenReleaseDateIsNotValid() {
//        Film film = new Film(1, "Name", "Description",
//                LocalDate.of(1895,12,27), 120);
//        assertThrows(ValidationException.class, () -> validateService.validateFilm(film));
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenLoginContainsSpaces() {
//        User user = new User(1, "mail@mail.ru", "login login", "Name",
//                LocalDate.of(1895,12,27));
//        assertThrows(ValidationException.class, () -> validateService.validateUser(user));
//    }
//
//    @Test
//    public void shouldTrueWhenNmeIsNull() throws ValidationException {
//        User user = new User(1, "mail@mail.ru", "login", null,
//                LocalDate.of(1895,12,27));
//        validateService.validateUser(user);
//        assertEquals(user.getLogin(), user.getName());
//    }
//}