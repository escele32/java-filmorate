package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.util.ApiPaths;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApiPaths.GENRES)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreController {
    GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> getGenres() {
        log.info("Получение списка genre.");
        return genreService.getGenres();
    }

    @GetMapping(ApiPaths.GENREID)
    @ResponseStatus(HttpStatus.OK)
    public Optional<Genre> getGenreById(@PathVariable Long genreId) {
        log.info("Получение genre по id {}.", genreId);
        return genreService.getGenreById(genreId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Genre createGenre(@RequestBody Genre genre) {
        log.info("Создание genre: {}.", genre);
        return genreService.createGenre(genre);
    }

    @DeleteMapping(ApiPaths.GENREID)
    public void deleteGenre(@PathVariable Long genreId) {
        log.info("Удаление Genre с id {}.", genreId);
        genreService.deleteGenre(genreId);
    }

    @PutMapping
    public Genre updateGenre(@RequestBody Genre genre) {
        log.info("Обновление жанра: {}", genre);
        return genreService.updateGenre(genre);
    }

}
