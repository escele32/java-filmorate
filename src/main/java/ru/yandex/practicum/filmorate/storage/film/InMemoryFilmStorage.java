package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();
    Map<Long, Set<User>> filmLikes = new HashMap<>();

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((film1, film2) -> Integer.compare(
                        Optional.ofNullable(filmLikes.get(film2.getId())).map(Set::size).orElse(0),
                        Optional.ofNullable(filmLikes.get(film1.getId())).map(Set::size).orElse(0)
                ))
                .limit(count)
                .toList();
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        return films.remove(filmId) != null;
    }

    @Override
    public void addLike(Long filmId, Long userId) {

    }

    @Override
    public void removeLike(Long filmId, Long userId) {

    }

    @Override
    public void clear() {
        films.clear();
    }

    public boolean addLike(Long filmId, User user) {
        Set<User> likes = filmLikes.get(filmId);
        if (likes != null) {
            return likes.add(user);
        }
        return false;
    }

    public boolean removeLike(Long filmId, User user) {
        Set<User> likes = filmLikes.get(filmId);
        if (likes != null) {
            return likes.remove(user);
        }
        return false;
    }

}
