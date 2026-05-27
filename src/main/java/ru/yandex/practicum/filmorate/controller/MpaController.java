package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.util.ApiPaths;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApiPaths.MPA)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaController {
    MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Mpa> getMpa() {
        log.info("Получение списка mpa.");
        return mpaService.getMpa();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mpa createMpa(@RequestBody Mpa mpa) {
        log.info("Создание mpa: {}.", mpa);
        return mpaService.createMpa(mpa);
    }

    @GetMapping(ApiPaths.MPAID)
    @ResponseStatus(HttpStatus.OK)
    public Optional<Mpa> getMpaId(@PathVariable Long mpaId) {
        log.info("Получение mpa по id {}.", mpaId);
        return mpaService.getMpaById(mpaId);
    }

    @DeleteMapping(ApiPaths.MPAID)
    public void deleteMpa(@PathVariable Long mpaId) {
        log.info("Удаление Mpa с id {}.", mpaId);
        mpaService.deleteMpa(mpaId);
    }

}
