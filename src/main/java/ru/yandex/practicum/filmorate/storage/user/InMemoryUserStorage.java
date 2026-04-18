package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            System.out.println("Пользователь для обновления не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean delete(Long id) {
        return users.remove(id) != null;
    }

    @Override
    public Optional<User> getById(Long id) {
        if (!users.containsKey(id)) {
            System.out.printf("Пользователь с id %d не найден\n", id);
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void clear() {
        users.clear();
    }

}
