package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    boolean deleteUser(Long userId);

    Optional<User> getUserById(Long id);

    List<User> getCommonFriends(Long userId, Long otherId);

    List<User> getFriends(Long userId);

    boolean addFriend(Long userId, Long otherId, String status);

    boolean removeFriend(Long userId, Long friendId);

    boolean isFriend(Long userId, Long friendId);

    public void updateFriendStatus(Long userId, Long friendId, String status);

    void clear();

}
