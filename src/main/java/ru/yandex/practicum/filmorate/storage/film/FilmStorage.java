package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Optional<Film> getById(Long filmId);

    Collection<Film> getPopular(int count);

    Film update(Film film);

    boolean delete(Long filmId);

    void clear();

}
