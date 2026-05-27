package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaService {
    MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    private void validateMpaIdIsNull(Long mpaId) {
        if (mpaId == null) {
            throw new ValidationException("Id mpa не может быть null.");
        }
    }

    private Mpa validateMpaIdThrow(Long mpaId) {
        return mpaStorage.getMpaById(mpaId)
                .orElseThrow(() -> {
                    log.warn("MPA с id {} не найден.", mpaId);
                    throw new NotFoundException(format("MPA с id %d не найден.\n", mpaId));
                });
    }

    public List<Mpa> getMpa() {
        log.info("Получение списка mpa.");
        return mpaStorage.getAllMpa().stream().toList();
    }

    public Optional<Mpa> getMpaById(Long mpaId) {
        validateMpaIdIsNull(mpaId);
        validateMpaIdThrow(mpaId);
        log.info("Получение mpa по id {}.", mpaId);
        return Optional.ofNullable(validateMpaIdThrow(mpaId));
    }

    public Mpa createMpa(Mpa mpa) {
        if (mpa.getName() == null || mpa.getName().isBlank()) {
            throw new ValidationException("Название mpa должно быть указано.");
        }
        log.info("Создание mpa.");
        return mpaStorage.createMpa(mpa);
    }

    public void deleteMpa(Long mpaId) {
        validateMpaIdIsNull(mpaId);
        log.info("Удаление mpa с id {}.", mpaId);
        if (!mpaStorage.deleteMpa(mpaId)) {
            log.warn("MPA с id {} не найден для удаления.", mpaId);
            throw new NotFoundException(format("MPA с id %d не найден для удаления\n", mpaId));
        }
    }

}
