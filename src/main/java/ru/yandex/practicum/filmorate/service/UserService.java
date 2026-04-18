package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void validateUser(User user) {
        log.debug("Валидация пользователя: {}", user);
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Некорректный email: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Некорректный логин: логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя не указано, установлено имя логина");
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(java.time.LocalDate.now())) {
            log.warn("Некорректная дата рождения: {}", user.getBirthday());
            throw new ValidationException("Некорректная дата рождения");
        }
        log.info("Валидация пользователя прошла успешно");
    }

    public User addUser(User user) {
        validateUser(user);
        log.info("Добавление пользователя: {}", user);
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        log.debug("Обновление пользователя: {}", user);
        if (user.getId() == null || userStorage.getById(user.getId()).isEmpty()) {
            log.warn("Пользователь не найден для обновления: {}", user);
            throw new NotFoundException(String.format("Пользователь с id %d не найден\n", user.getId()));
        }
        User oldUser = userStorage.getById(user.getId()).get();
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
            }
            oldUser.setEmail(user.getEmail());
            log.trace("Обновление электронной почты пользователя");
        }

        if (user.getName() != null) {
            oldUser.setName(user.getName());
            log.trace("Обновление имени пользователя");
        }

        if (user.getLogin() != null) {
            oldUser.setLogin(user.getLogin());
            log.trace("Обновление логина пользователя");
        }

        if (user.getBirthday() != null) {
            oldUser.setBirthday(user.getBirthday());
            log.trace("Обновление дня рождения пользователя");
        }
        userStorage.update(oldUser);
        log.info("Данные пользователя {} обновлены", oldUser);
        return oldUser;
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("ID пользователя и друга не могут быть null");
        }
        log.info("Добавление друга: пользователь {} и пользователь {} друзья", userId, friendId);
        User user = userStorage.getById(userId).orElseThrow(
                () -> {
                    log.warn("Пользователь c id {} не найден", userId);
                    throw new NotFoundException("Пользователь не найден");
                });
        User friend = userStorage.getById(friendId).orElseThrow(
                () -> {
                    log.warn("Друг c id {} не найден", friendId);
                    throw new NotFoundException("Друг не найден");
                });
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("ID пользователя и друга не могут быть null");
        }
        log.info("Удаление друга: пользователь {} и пользователь {} больше не друзья", userId, friendId);
        User user = userStorage.getById(userId).orElseThrow(
                () -> {
                    log.warn("Пользователь c id {} не найден", userId);
                    throw new NotFoundException("Пользователь не найден");
                });
        User friend = userStorage.getById(friendId).orElseThrow(
                () -> {
                    log.warn("Друг c id {} не найден", friendId);
                    throw new NotFoundException("Друг не найден");
                });
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public Collection<User> getFriends(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        log.info("Получение друзей пользователя {}", userId);
        User user = userStorage.getById(userId).orElseThrow(
                () -> {
                    log.warn("Пользователь c id {} не найден", userId);
                    throw new NotFoundException("Пользователь не найден");
                });
        return user.getFriends().stream()
                .map(id -> userStorage.getById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        if (userId == null || otherId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        log.info("Получение общих друзей: пользователь {} и пользователь {}", userId, otherId);
        User user = userStorage.getById(userId).orElseThrow(
                () -> {
                    log.warn("Пользователь c id {} не найден", userId);
                    throw new NotFoundException("Пользователь не найден");
                });
        User otherUser = userStorage.getById(otherId).orElseThrow(
                () -> {
                    log.warn("Другой пользователь c id {} не найден", otherId);
                    throw new NotFoundException("Другой пользователь не найден");
                });
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(id -> userStorage.getById(id).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        log.info("Получение пользователя по id {}", userId);
        return userStorage.getById(userId);
    }

    public void deleteUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        log.info("Удаление пользователя с id {}", userId);
        if (!userStorage.delete(userId)) {
            log.warn("Пользователь с id {} не найден для удаления", userId);
            throw new NotFoundException("Фильм не найден");
        }
    }

}
