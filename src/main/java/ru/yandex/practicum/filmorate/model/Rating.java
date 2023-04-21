package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Rating {
    private Integer id;
    private String name;

    public Rating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
