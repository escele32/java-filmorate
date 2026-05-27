package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.util.ApiPaths;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApiPaths.USERS)
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        log.info("Создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userService.getUsers();
    }

    @GetMapping(ApiPaths.USERID)
    @ResponseStatus(HttpStatus.OK)
    public Optional<User> getUserById(@PathVariable Long userId) {
        log.info("Получение пользователя по id {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping(ApiPaths.USERID)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
    }

    @PutMapping(ApiPaths.USERID_FRIENDS_FRIENDID)
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Добавление в друзья от пользователя {} пользователю {}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping(ApiPaths.USERID_FRIENDS_FRIENDID)
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Получен запрос на удаление друга {} у {}", friendId, userId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping(ApiPaths.USERID_FRIENDS)
    public List<User> getFriends(@PathVariable Long userId) {
        log.info("Вывод друзей пользователя {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping(ApiPaths.USERID_FRIENDS_COMMON_FRIENDS)
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        log.info("Поиск общих друзей у пользователя {} и пользователя {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

}
