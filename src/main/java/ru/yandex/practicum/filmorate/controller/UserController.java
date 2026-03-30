package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Проверка данных на добавление нового пользователя");
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка в электронной почте пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.error("Ошибка в логине пользователя");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        LocalDate localDate = LocalDate.now();
        if (user.getBirthday().isAfter(localDate)) {
            log.error("Ошибка в дате рождения пользователя");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Так как поле имени пользователя было пустым, ему присвоено значение логина");
        }
        log.info("Данные соответствуют критериям");
        user.setId(getNextId());
        log.info("Пользователю присвоено id: {}", user.getId());
        users.put(user.getId(), user);
        log.info("Пользователь: {} добавлен в список", user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Проверка данных на обновление пользователя");
        if (newUser.getId() == null) {
            log.error("Ошибка в id пользователя");
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id {} не найден", newUser.getId());
            throw new ValidationException("Пользователь не найден");
        }
        User oldUser = users.get(newUser.getId());
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
            log.trace("Обновление логина пользователя");
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
            log.trace("Обновление электронной почты пользователя");
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
            log.trace("Обновление дня рождения пользователя");
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
            log.trace("Обновление имени пользователя");
        }
        log.info("Данные пользователя {} обновлены", oldUser);
        return oldUser;
    }
}
