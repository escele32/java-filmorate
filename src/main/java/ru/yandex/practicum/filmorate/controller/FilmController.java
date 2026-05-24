package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.ApiPaths;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApiPaths.FILMS)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@RequestBody Film film) {
        log.info("Создание фильма: {}", film);
        return filmService.addFilm(film);
    }

    @GetMapping(ApiPaths.FILMID)
    @ResponseStatus(HttpStatus.OK)
    public Optional<Film> getFilmById(@PathVariable Long filmId) {
        log.info("Получение фильма по id {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilms() {
        log.info("Получение всех фильмов");
        return filmService.getFilms();
    }

    @DeleteMapping(ApiPaths.FILMID)
    public void deleteFilm(@PathVariable Long filmId) {
        log.info("Удаление фильма с id {}", filmId);
        filmService.deleteFilm(filmId);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping(ApiPaths.FILMID_LIKE_USERID)
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(ApiPaths.FILMID_LIKE_USERID)
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, filmId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping(ApiPaths.POPULAR)
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Вывод топ-{} фильмов", count);
        return filmService.getPopularFilms(count);
    }

}
