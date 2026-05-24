package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.row_mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.util.*;

@Component("userDbStorage")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDbStorage implements UserStorage {
    JdbcTemplate jdbcTemplate;
    UserRowMapper userRowMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    private void loadingFriends(User user) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Long> friends = jdbcTemplate.queryForList(sql, Long.class, user.getId());
        user.setFriends(new HashSet<>(friends));
    }

    private void updateFriends(Long userId, Set<Long> friends) {
        jdbcTemplate.update("DELETE FROM friends WHERE user_id = ?", userId);
        if (friends != null && !friends.isEmpty()) {
            List<Object[]> batchArgs = friends.stream()
                    .map(friendId -> new Object[]{userId, friendId})
                    .toList();
            jdbcTemplate.batchUpdate(
                    "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)",
                    batchArgs
            );
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        users.forEach(this::loadingFriends);
        return users;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (name, login, email, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getEmail());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);
        updateFriends(user.getId(), user.getFriends());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        updateFriends(user.getId(), user.getFriends());
        return user;
    }

    @Override
    public boolean deleteUser(Long userId) {
        String deleteFriendSql = "DELETE FROM friends WHERE user_id = ?";
        jdbcTemplate.update(deleteFriendSql, userId);
        String sql = "DELETE FROM users WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId);
        return rowsAffected > 0;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId);
        Optional<User> userOptional = users.stream().findFirst();
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            loadingFriends(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friends f1 ON u.id = f1.friend_id AND f1.user_id = ?
                JOIN friends f2 ON u.id = f2.friend_id AND f2.user_id = ?
                """;
        List<User> users = jdbcTemplate.query(sql, userRowMapper, userId, otherId);
        users.forEach(this::loadingFriends);
        return users;
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friends f ON u.id = f.friend_id
                WHERE f.user_id = ?
                ORDER BY u.id
                """;
        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public boolean addFriend(Long userId, Long otherId, String status) {
        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'NOT_CONFIRMED')";
        int rowsAffected = jdbcTemplate.update(sql, userId, otherId);
        return rowsAffected > 0;
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, friendId);
        return rowsAffected > 0;
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        String sql = """
            SELECT COUNT(*)
            FROM friends
            WHERE user_id = ? AND friend_id = ?
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count > 0;
    }

    @Override
    public void clear() {
        String sqlFriends = "DELETE FROM friends";
        String sqlLikes = "DELETE FROM likes";
        String sqlUsers = "DELETE FROM users";
        jdbcTemplate.update(sqlFriends);
        jdbcTemplate.update(sqlLikes);
        jdbcTemplate.update(sqlUsers);
    }

    @Override
    public void updateFriendStatus(Long userId, Long friendId, String status) {
        String sql = "UPDATE friends SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, status == String.valueOf(FriendStatus.CONFIRMED), userId, friendId);
    }

}
