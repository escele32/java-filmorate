package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);

    Collection<Genre> getAllGenre();

    Optional<Genre> getGenreById(Long genreId);

    boolean deleteGenre(Long genreId);

    Set<Long> findAllByIds(Set<Long> genreIds);

    void clear();

}
