package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private void validateFilm(Film film) {
        log.debug("Валидация фильма: {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не указано");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Описание фильма превышает 200 символов");
            throw new ValidationException("Описание слишком длинное");
        }
        if (film.getReleaseDate() == null
                || film.getReleaseDate().isBefore(java.time.LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 или пустой");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 или пустой");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("Продолжительность фильма неверная: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        log.info("Валидация фильма прошла успешно");
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        log.info("Добавление фильма: {}", film);
        return filmStorage.create(film);
    }

    public Optional<Film> getFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }
        log.info("Получение фильма по id {}", filmId);
        return filmStorage.getById(filmId);
    }

    public Collection<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.findAll();
    }

    public void deleteFilm(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("ID фильма не может быть null");
        }
        log.info("Удаление фильма с id {}", filmId);
        if (!filmStorage.delete(filmId)) {
            log.warn("Фильм с id {} не найден для удаления", filmId);
            throw new NotFoundException("Фильм не найден");
        }
    }

    public void addLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new ValidationException("ID фильма и пользователя не могут быть null");
        }
        Optional<User> userOptional = userStorage.getById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> {
                    log.warn("Фильм с id {} не найден", filmId);
                    throw new NotFoundException("Фильм не найден");
                });
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        if (film.getLikes().contains(userId)) {
            log.info("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Данный пользователь уже поставил лайк этому фильму");
        } else {
            film.getLikes().add(userId);
            filmStorage.update(film);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new ValidationException("ID фильма и пользователя не могут быть null");
        }
        Optional<User> userOptional = userStorage.getById(userId);
        if (userOptional.isEmpty()) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> {
                    log.warn("Фильм с id {} не найден", filmId);
                    throw new NotFoundException("Фильм не найден");
                });
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        if (!film.getLikes().contains(userId)) {
            log.warn("Лайк от пользователя {} у фильма {} не найден", userId, filmId);
            throw new NotFoundException("Лайк от пользователя не найден");
        } else {
            film.getLikes().remove(userId);
            filmStorage.update(film);
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество популярных фильмов должно быть положительным");
        }
        log.info("Запрос на получение {} популярных фильмов", count);
        return filmStorage.getPopular(count);
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма: {}", film);
        if (film.getId() == null) {
            log.warn("Id фильма не должен быть пустым");
            throw new ValidationException("Id фильма не должен быть пустым");
        }
        Film oldFilm = filmStorage.getById(film.getId()).orElseThrow(
                () -> {
                    log.warn("Фильм не найден для обновления: {}", film);
                    throw new NotFoundException(String.format("Фильм с id %d не найден для обновления\n",
                            film.getId()));
                });
        if (film.getName() != null) {
            oldFilm.setName(film.getName());
            log.trace("Обновление названия фильма");
        }
        if (film.getDescription() != null) {
            oldFilm.setDescription(film.getDescription());
            log.trace("Обновление описания фильма");
        }
        if (film.getDuration() != null) {
            oldFilm.setDuration(film.getDuration());
            log.trace("Обновление продолжительности фильма");
        }
        if (film.getReleaseDate() != null) {
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.trace("Обновление даты релиза фильма");
        }
        filmStorage.update(oldFilm);
        log.info("Данные фильма {} обновлены", oldFilm);
        return oldFilm;
    }

}
