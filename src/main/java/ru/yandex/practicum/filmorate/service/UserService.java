package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void validateUserCreate(User user) {
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

    private User validateUserThrow(User user) {
        return userStorage.getUserById(user.getId()).orElseThrow(() -> {
            log.warn("Пользователь c id {} не найден.", user.getId());
            throw new NotFoundException(format("Пользователь с id %d не найден.\n", user.getId()));
        });
    }

    private User validateUserByIdThrow(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь c id {} не найден.", userId);
                    throw  new NotFoundException(format("Пользователь с id %d не найден.\n", userId));
                });
    }

    private void validateUserByIdIsNull(Long userId) {
        if (userId == null) {
            log.warn("Id пользователя не должен быть пустым");
            throw new ValidationException("Id пользователя не должен быть пустым");
        }
    }

    private User validateUserUpdate(User user) {
        User oldUser = validateUserThrow(user);
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
        return oldUser;
    }

    private void validatePairUserByIdsIsNull(Long userId, Long otherId) {
        if (userId == null || otherId == null) {
            log.warn("ID пользователя или предполагаемого друга не может быть null.");
            throw new ValidationException("ID пользователя или предполагаемого друга не может быть null.");
        }
    }

    public User createUser(User user) {
        validateUserCreate(user);
        log.info("Добавление пользователя: {}", user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.debug("Обновление пользователя: {}", user);
        validateUserByIdIsNull(user.getId());
        User updateUser = validateUserUpdate(user);
        log.info("Данные пользователя {} обновлены", user);
        return userStorage.updateUser(updateUser);
    }

    public List<User> getUsers() {
        log.info("Получение всех пользователей.");
        return userStorage.findAllUsers().stream().toList();
    }

    public Optional<User> getUserById(Long userId) {
        validateUserByIdIsNull(userId);
        log.info("Получение пользователя по id {}", userId);
        return userStorage.getUserById(userId);
    }

    public void deleteUser(Long userId) {
        validateUserByIdIsNull(userId);
        log.info("Удаление пользователя с id {}", userId);
        if (!userStorage.deleteUser(userId)) {
            log.warn("Пользователь с id {} не найден для удаления.", userId);
            throw new NotFoundException(format("Пользователь с id %d не найден для удаления.\n", userId));
        }
    }

    public void addFriend(Long userId, Long otherId) {
        if (userId.equals(otherId)) {
            throw new ValidationException("Нельзя самого себя добавить в друзья");
        }
        validatePairUserByIdsIsNull(userId, otherId);
        validateUserByIdThrow(userId);
        validateUserByIdThrow(otherId);
        if (userStorage.isFriend(userId, otherId)) {
            log.info("Подтверждена взаимная дружба {} с {}", userId, otherId);
            userStorage.updateFriendStatus(userId, otherId, String.valueOf(FriendStatus.CONFIRMED));
            userStorage.updateFriendStatus(otherId, userId, String.valueOf(FriendStatus.CONFIRMED));
        } else {

            log.info("Одностороняя дружба {} с {}", userId, otherId);
            userStorage.addFriend(userId, otherId, String.valueOf(FriendStatus.NOT_CONFIRMED));
        }
    }

    public List<User> getFriends(Long userId) {
        validateUserByIdIsNull(userId);
        validateUserByIdThrow(userId);
        return userStorage.getFriends(userId);
    }

    public boolean removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя самого себя удалить из друзей");
        }
        validatePairUserByIdsIsNull(userId, friendId);
        validateUserByIdThrow(userId);
        validateUserByIdThrow(friendId);
        return userStorage.removeFriend(userId, friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        if (userId.equals(otherId)) {
            throw new ValidationException("Нельзя у самого себя искать общих друзей");
        }
        validatePairUserByIdsIsNull(userId, otherId);
        validateUserByIdThrow(userId);
        validateUserByIdThrow(otherId);
        return userStorage.getCommonFriends(userId, otherId);
    }

}
