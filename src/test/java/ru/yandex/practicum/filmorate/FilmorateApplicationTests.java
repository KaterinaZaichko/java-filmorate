package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FilmDbStorage;
import ru.yandex.practicum.filmorate.repository.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest // перед запуском этих тестов необходим запуск всего приложения
@AutoConfigureTestDatabase // перед запуском теста необходимо сконфигурировать тестовую БД вместо основной
@RequiredArgsConstructor(onConstructor_ = @Autowired) // конструктор, созданный с помощью библиотеки Lombok,
        // сможет получать зависимости через механизм @Autowired
class FilmorateApplicationTests {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    Rating mpa = new Rating(1, null);
    Set<Genre> genres = new HashSet<>(List.of(new Genre(1, null), new Genre(2, null)));
    Film film = new Film(0, "Name", "Description", LocalDate.of(1990, 5, 7),
            120, mpa, genres);
    Rating mpa2 = new Rating(2, null);
    Set<Genre> genres2 = new HashSet<>(List.of(new Genre(3, null), new Genre(4, null)));
    Film film2 = new Film(0, "Name2", "Description2", LocalDate.of(1990, 5, 7),
            120, mpa2, genres2);
    Rating updateMpa = new Rating(3, null);
    Set<Genre> updateGenres = new HashSet<>(List.of(new Genre(5, null),
            new Genre(6, null)));
    Film updateFilm = new Film(1, "updateName", "updateDescription",
            LocalDate.of(1990, 11, 23), 125, updateMpa, updateGenres);
    User user = new User(0, "name@mail.ru", "Login", "Name",
            LocalDate.of(1990, 5, 7));
    User user2 = new User(0, "name2@mail.ru", "Login2", "Name2",
            LocalDate.of(1990, 5, 7));
    User updateUser = new User(1, "updatename@mail.ru", "updateLogin", "updateName",
            LocalDate.of(1990, 11, 23));

    @Test
    public void testSaveFilm() {
        assertThat(Optional.ofNullable(filmStorage.save(film)))
                .isPresent()
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Name")
                                .hasFieldOrPropertyWithValue("description", "Description")
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1990, 5, 7))
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getMpa()).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", null)
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getGenres().contains(new Genre(1, null))).isTrue()
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getGenres().contains(new Genre(2, null))).isTrue()
                );
    }

    @Test
    public void testFindAllFilms() {
        filmStorage.save(film2);
        assertThat(Optional.ofNullable(filmStorage.findAll()))
                .isPresent()
                .hasValueSatisfying(films ->
                        assertThat(films.size() == 2).isTrue()
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Name")
                                .hasFieldOrPropertyWithValue("description", "Description")
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .hasFieldOrPropertyWithValue("releaseDate",
                                        LocalDate.of(1990, 5, 7))
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(0).getMpa()).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "G")
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(0).getGenres().contains(new Genre(1, "Комедия"))).isTrue()
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(0).getGenres().contains(new Genre(2, "Драма"))).isTrue()
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("name", "Name2")
                                .hasFieldOrPropertyWithValue("description", "Description2")
                                .hasFieldOrPropertyWithValue("duration", 120)
                                .hasFieldOrPropertyWithValue("releaseDate",
                                        LocalDate.of(1990, 5, 7))
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(1).getMpa()).hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("name", "PG")
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(1).getGenres().contains(new Genre(3, "Мультфильм"))).isTrue()
                )
                .hasValueSatisfying(films ->
                        assertThat(films.get(1).getGenres().contains(new Genre(4, "Триллер"))).isTrue()
                );
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        assertThat(Optional.ofNullable(filmStorage.update(updateFilm)))
                .isPresent()
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "updateName")
                                .hasFieldOrPropertyWithValue("description", "updateDescription")
                                .hasFieldOrPropertyWithValue("duration", 125)
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1990, 11, 23))

                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getMpa()).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("name", "PG-13")
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getGenres().contains(new Genre(5, "Документальный"))).isTrue()
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getGenres().contains(new Genre(6, "Боевик"))).isTrue()
                );
    }

    @Test
    public void testFindFilmById() {
        assertThat(Optional.ofNullable(filmStorage.findFilmById(1)))
                .isPresent()
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "updateName")
                                .hasFieldOrPropertyWithValue("description", "updateDescription")
                                .hasFieldOrPropertyWithValue("duration", 125)
                                .hasFieldOrPropertyWithValue("releaseDate",
                                        LocalDate.of(1990, 11, 23))
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getMpa()).hasFieldOrPropertyWithValue("id", 3)
                                .hasFieldOrPropertyWithValue("name", "PG-13")
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getGenres().contains(new Genre(5, "Документальный"))).isTrue()
                )
                .hasValueSatisfying(testFilm ->
                        assertThat(testFilm.getGenres().contains(new Genre(6, "Боевик"))).isTrue()
                );
    }


    @Test
    public void testSaveUser() {
        assertThat(Optional.ofNullable(userStorage.save(user)))
                .isPresent()
                .hasValueSatisfying(testUser ->
                        assertThat(testUser).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("email", "name@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "Login")
                                .hasFieldOrPropertyWithValue("name", "Name")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1990, 5, 7)));
    }

    @Test
    public void testFindAllUsers() {
        userStorage.save(user2);
        assertThat(Optional.ofNullable(userStorage.findAll()))
                .isPresent()
                .hasValueSatisfying(users ->
                        assertThat(users.size() == 2).isTrue()
                )
                .hasValueSatisfying(users ->
                        assertThat(users.get(0)).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("email", "name@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "Login")
                                .hasFieldOrPropertyWithValue("name", "Name")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1990, 5, 7)))
                .hasValueSatisfying(users ->
                        assertThat(users.get(1)).hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("email", "name2@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "Login2")
                                .hasFieldOrPropertyWithValue("name", "Name2")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1990, 5, 7)));
    }

    @Test
    public void testUpdateUser() throws ValidationException {
        assertThat(Optional.ofNullable(userStorage.update(updateUser)))
                .isPresent()
                .hasValueSatisfying(testUser ->
                        assertThat(testUser).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("email", "updatename@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "updateLogin")
                                .hasFieldOrPropertyWithValue("name", "updateName")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1990, 11, 23)));
    }

    @Test
    public void testFindUserById() {
        assertThat(Optional.ofNullable(userStorage.findUserById(1)))
                .isPresent()
                .hasValueSatisfying(testUser ->
                        assertThat(testUser).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("email", "updatename@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "updateLogin")
                                .hasFieldOrPropertyWithValue("name", "updateName")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1990, 11, 23)));
    }
}
