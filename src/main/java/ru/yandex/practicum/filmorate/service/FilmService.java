package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmService {
    FilmStorage filmStorage;
    MpaStorage mpaStorage;
    GenreStorage genreStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       MpaStorage mpaStorage, GenreStorage genreStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
        this.userStorage = userStorage;
    }

    private void validateFilmCreate(Film film) {
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

    private void validatePairByIdsIsNull(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            log.warn("ID фильма или пользователя не может быть null");
            throw new ValidationException("ID фильма или пользователя не может быть null");
        }
    }

    private Film validateFilmByIdThrow(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> {
                    log.warn("Фильм не найден для обновления: {}", filmId);
                    throw new NotFoundException(format("Фильм с id %d не найден для обновления\n", filmId));
                });
    }

    private User validateUserByIdThrow(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь c id {} не найден.", userId);
                    throw new NotFoundException(format("Пользователь с id %d не найден.\n", userId));
                });
    }

    private void validateFilmByIdIsNull(Long filmId) {
        if (filmId == null) {
            log.warn("ID фильма не может быть null.");
            throw new ValidationException("ID фильма не может быть null.");
        }
    }

    private void validateFilmGenres(Set<Genre> genres) {
        //Извлечение ID и проверка на дубликаты
        Set<Long> genreIds = genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        //Проверка существования всех жанров в БД
        Set<Long> genreIdsExist = genreStorage.findAllByIds(genreIds);
        //Определение отсутствующих жанров
        Set<Long> missingIds = new HashSet<>(genreIds);
        missingIds.removeAll(genreIdsExist);
        if (!missingIds.isEmpty()) {
            log.warn("Жанры с ID не найдены: {}", missingIds);
            throw new NotFoundException(format("Жанры с ID %s не найдены в системе.", missingIds));
        }
    }

    public Film addFilm(Film film) {
        validateFilmCreate(film);
        log.info("Добавление фильма: {}", film);
        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId())
                .orElseThrow(() -> {
                    log.warn("MPA c id {} не найден.", film.getMpa().getId());
                    throw  new NotFoundException(format("MPA с id %d не найден.\n", film.getMpa().getId()));
                });
        film.setMpa(mpa);
        validateFilmGenres(film.getGenres());
        return filmStorage.createFilm(film);
    }

    public Optional<Film> getFilmById(Long filmId) {
        validateFilmByIdIsNull(filmId);
        log.info("Получение фильма по id {}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.findAllFilms().stream().toList();
    }

    public void deleteFilm(Long filmId) {
        validateFilmByIdIsNull(filmId);
        log.info("Удаление фильма с id {}", filmId);
        if (!filmStorage.deleteFilm(filmId)) {
            log.warn("Фильм с id {} не найден для удаления", filmId);
            throw new NotFoundException(format("Фильм с id %d не найден для удаления\n", filmId));
        }
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма: {}", film);
        validateFilmByIdIsNull(film.getId());
        Film oldFilm = validateFilmByIdThrow(film.getId());
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
        if (film.getMpa() != null) {
            oldFilm.setMpa(film.getMpa());
            log.trace("Обновление mpa фильма");
        }
        if (film.getGenres() != null) {
            validateFilmGenres(film.getGenres());
            oldFilm.setGenres(film.getGenres());
            log.trace("Обновлены жанры фильма");
        }
        log.info("Данные фильма {} обновлены", film);
        return filmStorage.updateFilm(oldFilm);
    }

    public void addLike(Long filmId, Long userId) {
        validatePairByIdsIsNull(filmId, userId);
        validateFilmByIdThrow(filmId);
        validateUserByIdThrow(userId);
        log.info("Пользователь{} ставит лайк фильму{}", userId, filmId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        validatePairByIdsIsNull(filmId, userId);
        validateFilmByIdThrow(filmId);
        validateUserByIdThrow(userId);
        log.info("Пользователь{} удаляет лайк у фильма{}", userId, filmId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            log.warn("Количество фильмов должно быть больше нуля");
            throw new IllegalArgumentException("Количество фильмов должно быть больше нуля");
        }
        return filmStorage.getPopularFilms(count).stream().toList();
    }

}
