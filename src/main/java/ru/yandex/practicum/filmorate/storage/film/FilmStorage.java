package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film createFilm(Film film);

    Optional<Film> getFilmById(Long filmId);

    Collection<Film> getPopularFilms(int count);

    Film updateFilm(Film film);

    boolean deleteFilm(Long filmId);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    void clear();

}
