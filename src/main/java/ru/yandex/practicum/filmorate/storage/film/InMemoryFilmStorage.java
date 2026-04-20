package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> films = new HashMap<>();

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return films.values().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(),
                        film1.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean delete(Long filmId) {
        return films.remove(filmId) != null;
    }

    @Override
    public void clear() {
        films.clear();
    }

}
