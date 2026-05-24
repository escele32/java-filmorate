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
        String sqlGetGenreById = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlGetGenreById, genreRowMapper, genreId);
        return genres.stream().findFirst();
    }

    @Override
    public Collection<Genre> getAllGenre() {
        String sqlGetAllGenre = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlGetAllGenre, genreRowMapper);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        String sqlUpdateGenre = "UPDATE genres SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlUpdateGenre,
                genre.getName(),
                genre.getId()
        );
        return genre;
    }

    @Override
    public Genre createGenre(Genre genre) {
        String sqlCreateGenre = "INSERT INTO genres (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlCreateGenre, new String[] {"id"});
            statement.setString(1, genre.getName());
            return statement;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        genre.setId(generatedId);
        return genre;
    }

    @Override
    public boolean deleteGenre(Long genreId) {
        String sqlDeleteGenre = "DELETE FROM genres WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sqlDeleteGenre, genreId);
        return rowsAffected > 0;
    }

    @Override
    public void clear() {
        String sqlClearTableGenres = "DELETE FROM genres";
        jdbcTemplate.update(sqlClearTableGenres);
    }

    @Override
    public Set<Long> findAllByIds(Set<Long> genreIds) {
        if (genreIds.isEmpty()) {
            return new HashSet<>();
        }
        String sqlFindAllByIds = "SELECT id FROM genres WHERE id IN (:genreIds)";
        MapSqlParameterSource parameters = new MapSqlParameterSource("genreIds", genreIds);
        List<Long> result = namedJdbcTemplate.query(
                sqlFindAllByIds,
                parameters,
                (resultSet, rowNum) -> resultSet.getLong("id")
        );
        return new HashSet<>(result);
    }

}
