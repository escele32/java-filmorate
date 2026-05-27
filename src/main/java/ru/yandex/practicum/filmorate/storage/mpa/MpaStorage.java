package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Optional<Mpa> getMpaById(Long mpaId);

    List<Mpa> getAllMpa();

    Mpa createMpa(Mpa mpa);

    boolean deleteMpa(Long mpaId);

    void clear();

}
