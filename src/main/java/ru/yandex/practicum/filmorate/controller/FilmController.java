package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Проверка данных на добавление нового фильма");
        if (film.getName().isEmpty()) {
            log.error("Ошибка в названии фильма");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Ошибка в описании фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getDuration() <= 0) {
            log.error("Ошибка в продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        LocalDate localDate = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(localDate)) {
            log.error("Ошибка в дате релиза фильма");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        log.info("Данные соответствуют критериям");
        film.setId(getNextId());
        log.info("Фильму присвоено id: {}", film.getId());
        films.put(film.getId(), film);
        log.info("Фильм: {} добавлен в список", film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Проверка данных на обновление фильма");
        if (newFilm.getId() == null) {
            log.error("Ошибка в id фильма");
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(newFilm.getId())) {
            log.error("Фильм с id {} не найден", newFilm.getId());
            throw new ValidationException("Фильм не найден");
        }
        Film oldFilm = films.get(newFilm.getId());
        log.trace("Получение фильма для обновления из списка согласно введённому id");
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
            log.trace("Обновление названия фильма");
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
            log.trace("Обновление описания фильма");
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
            log.trace("Обновление продолжительности фильма");
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.trace("Обновление даты релиза фильма");
        }
        log.info("Данные фильма {} обновлены", oldFilm);
        return oldFilm;
    }
}
