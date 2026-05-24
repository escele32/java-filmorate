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
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        return users.remove(id) != null;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return List.of();
    }

    @Override
    public List<User> getFriends(Long userId) {
        return List.of();
    }

    @Override
    public boolean addFriend(Long userId, Long otherId, String status) {
        return false;
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        return false;
    }

    @Override
    public void updateFriendStatus(Long userId, Long friendId, String status) {

    }

    @Override
    public void clear() {
        users.clear();
    }

}
