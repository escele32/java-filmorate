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

import java.util.Collection;
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
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @PutMapping(ApiPaths.USER_ID + ApiPaths.FRIENDS + ApiPaths.FRIEND_ID)
    public void addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Добавление друга: пользователь {} и пользователь {} друзья", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping(ApiPaths.USER_ID + ApiPaths.FRIENDS + ApiPaths.FRIEND_ID)
    public void removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Удаление друга: пользователь {} и пользователь {} больше не друзья", userId, friendId);
        userService.removeFriend(userId, friendId);
    }

    @GetMapping(ApiPaths.USER_ID + ApiPaths.FRIENDS)
    public Collection<User> getFriends(@PathVariable Long userId) {
        log.info("Получение друзей пользователя {}", userId);
        return userService.getFriends(userId);
    }

    @GetMapping(ApiPaths.USER_ID + ApiPaths.FRIENDS + ApiPaths.COMMON_FRIENDS)
    public Collection<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherId) {
        log.info("Получение общих друзей: пользователь {} и пользователь {}", userId, otherId);
        return userService.getCommonFriends(userId, otherId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping(ApiPaths.USER_ID)
    public Optional<User> getUserById(@PathVariable Long userId) {
        log.info("Получение пользователя по id {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping(ApiPaths.USER_ID)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        userService.deleteUser(userId);
    }

}
