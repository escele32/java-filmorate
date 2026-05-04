package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    boolean delete(Long id);

    Optional<User> getById(Long id);

    void clear();

}
