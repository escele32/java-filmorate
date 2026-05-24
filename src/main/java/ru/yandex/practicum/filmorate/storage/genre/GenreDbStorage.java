package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.row_mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreDbStorage implements GenreStorage {
    JdbcTemplate jdbcTemplate;
    GenreRowMapper genreRowMapper;
    NamedParameterJdbcTemplate namedJdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper,
                          NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Override
    public Optional<Genre> getGenreById(Long genreId) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, genreRowMapper, genreId);
        return genres.stream().findFirst();
    }

    @Override
    public Collection<Genre> getAllGenre() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        String sql = "UPDATE genres SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                genre.getName(),
                genre.getId()
        );
        return genre;
    }

    @Override
    public Genre createGenre(Genre genre) {
        String sql = "INSERT INTO genres (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[] {"id"});
            statement.setString(1, genre.getName());
            return statement;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        genre.setId(generatedId);
        return genre;
    }

    @Override
    public boolean deleteGenre(Long genreId) {
        String sql = "DELETE FROM genres WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, genreId);
        return rowsAffected > 0;
    }

    @Override
    public void clear() {
        String sql = "DELETE FROM genres";
        jdbcTemplate.update(sql);
    }

    @Override
    public Set<Long> findAllByIds(Set<Long> genreIds) {
        if (genreIds.isEmpty()) {
            return new HashSet<>();
        }
        String sql = "SELECT id FROM genres WHERE id IN (:genreIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("genreIds", genreIds);
        List<Long> result = namedJdbcTemplate.query(
                sql,
                parameters,
                (resultSet, rowNum) -> resultSet.getLong("id")
        );
        return new HashSet<>(result);
    }

}
