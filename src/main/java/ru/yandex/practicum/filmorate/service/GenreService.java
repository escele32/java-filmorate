package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    private void validateGenreByIdIsNull(Long genreId) {
        if (genreId == null) {
            log.warn("ID genre не может быть null");
            throw new ValidationException("ID genre не может быть null");
        }
    }

    private Genre validateGenreThrow(Genre genre) {
        return genreStorage.getGenreById(genre.getId())
                .orElseThrow(() -> {
                    log.warn("Genre c id {} не найден.", genre.getId());
                    throw new NotFoundException(format("Genre с id %d не найден.\n", genre.getId()));
                });
    }

    private Genre validateGenreIdThrow(Long genreId) {
        return genreStorage.getGenreById(genreId)
                .orElseThrow(() -> {
                    log.warn("Genre c id {} не найден.", genreId);
                    throw  new NotFoundException(format("Genre с id %d не найден.\n", genreId));
                });
    }

    public List<Genre> getGenres() {
        log.info("Получение списка genre.");
        return genreStorage.getAllGenre().stream().toList();
    }

    public Genre updateGenre(Genre genre) {
        validateGenreByIdIsNull(genre.getId());
        validateGenreThrow(genre);
        log.info("Данные жанра {} обновлены", genre);
        return genreStorage.updateGenre(genre);
    }

    public Optional<Genre> getGenreById(Long genreId) {
        validateGenreByIdIsNull(genreId);
        log.info("Получение Genre по id {}", genreId);
        return Optional.ofNullable(validateGenreIdThrow(genreId));
    }

    public Genre createGenre(Genre genre) {
        if (genre.getName() == null || genre.getName().isBlank()) {
            throw new ValidationException("Название genre должно быть указано.");
        }
        log.info("Создание genre.");
        return genreStorage.createGenre(genre);
    }

    public void deleteGenre(Long genreId) {
        validateGenreByIdIsNull(genreId);
        log.info("Удаление genre с id {}.", genreId);
        if (!genreStorage.deleteGenre(genreId)) {
            log.warn("Genre с id {} не найден для удаления.", genreId);
            throw new NotFoundException(format("Genre с id %d не найден для удаления\n", genreId));
        }
    }

}
