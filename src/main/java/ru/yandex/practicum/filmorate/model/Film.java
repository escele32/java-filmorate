package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
