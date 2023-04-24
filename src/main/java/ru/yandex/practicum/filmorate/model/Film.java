package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.ReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    @Size(max = 200)
    private String description;
    @NotNull
    @ReleaseDate(1895 - 12 - 28)
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Genre> genres;
    @NotNull
    private Rating mpa;

    public Film(Integer id, String name, String description, LocalDate releaseDate,
                int duration, Rating mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public void addGenre(Genre genre) {
        getGenres().add(genre);
    }
}
