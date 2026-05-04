package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * User.
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    final Set<Long> friends = new HashSet<>();
}
