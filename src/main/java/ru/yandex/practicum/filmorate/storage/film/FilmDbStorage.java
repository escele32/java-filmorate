package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.row_mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.row_mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.row_mappers.MpaRowMapper;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmDbStorage implements FilmStorage {
    JdbcTemplate jdbcTemplate;
    FilmRowMapper filmRowMapper;
    GenreRowMapper genreRowMapper;
    MpaRowMapper mpaRowMapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper,
                         GenreRowMapper genreRowMapper, MpaRowMapper mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    private void loadingLikes(Film film) {
        String sqlLoadingLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> likes = jdbcTemplate.queryForList(sqlLoadingLikes, Long.class, film.getId());
        film.setLikes(new HashSet<>(likes));
    }

    private void loadingMpa(Film film) {
        String sqlLoadingMpa = "SELECT id, name FROM mpa WHERE id = (SELECT mpa_id FROM films WHERE id = ?)";
        List<Mpa> result = jdbcTemplate.query(sqlLoadingMpa, mpaRowMapper, film.getId());
        if (!result.isEmpty()) {
            film.setMpa(result.getFirst());
        }
    }

    private void loadingGenres(Film film) {
        String sqlLoadingGenres = """
                SELECT g.id, g.name
                FROM genres g
                JOIN film_genre fg ON g.id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.id
                """;
        List<Genre> genres = jdbcTemplate.query(sqlLoadingGenres, genreRowMapper, film.getId());
        film.setGenres((new LinkedHashSet<>(genres)));
    }

    private void updateGenres(Long filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
        if (genres != null && !genres.isEmpty()) {
            List<Long> genreIds = genres.stream()
                    .map(Genre::getId)
                    .distinct()
                    .toList();
            genreIds.forEach(genreId ->
                    jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                            filmId, genreId));
        }
    }

    private void updateLikes(Long filmId, Set<Long> likes) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", filmId);
        if (likes != null && !likes.isEmpty()) {
            List<Object[]> batchArgs = likes.stream()
                    .map(userId -> new Object[]{filmId, userId})
                    .collect(Collectors.toList());
            String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sqlFindAllFilms = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sqlFindAllFilms, filmRowMapper);
        films.forEach(film -> {
            loadingMpa(film);
            loadingGenres(film);
            loadingLikes(film);
        });
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        String sqlCreateFilm = """
            INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlCreateFilm, new String[] {"id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setObject(3, film.getReleaseDate());
            statement.setInt(4, film.getDuration());
            statement.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return statement;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        film.setId(generatedId);
        updateGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        String sqlGetFilmById = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sqlGetFilmById, filmRowMapper, filmId);
        Optional<Film> filmOptional = films.stream().findFirst();
        if (filmOptional.isPresent()) {
            Film film = filmOptional.get();
            loadingMpa(film);
            loadingGenres(film);
            loadingLikes(film);
            return Optional.of(film);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String sqlGetPopularFilms = """
                SELECT f.*, COUNT(l.user_id) as likes_count
                FROM films f
                LEFT JOIN likes l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY likes_count DESC, f.id ASC
                LIMIT ?
                """;
        List<Film> films = jdbcTemplate.query(sqlGetPopularFilms, filmRowMapper, count);
        films.forEach(film -> {
            loadingMpa(film);
            loadingGenres(film);
            loadingLikes(film);
        });
        return films;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdateFilm = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?
            """;
        jdbcTemplate.update(sqlUpdateFilm,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());
        updateGenres(film.getId(), film.getGenres());
        updateLikes(film.getId(), film.getLikes());
        return film;
    }

    @Override
    public boolean deleteFilm(Long filmId) {
        String deleteGenresSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, filmId);
        String deleteFilmSql = "DELETE FROM films WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(deleteFilmSql, filmId);
        return rowsAffected > 0;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sqlAddLike = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddLike, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
    String sqlRemoveLike = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    jdbcTemplate.update(sqlRemoveLike, filmId, userId);
    }

    @Override
    public void clear() {
        String sqlClearTableFilms = "DELETE FROM films";
        String sqlClearTableLikes = "DELETE FROM likes";
        String sqlClearTableFilmGenre = "DELETE FROM film_genre";
        jdbcTemplate.update(sqlClearTableLikes);
        jdbcTemplate.update(sqlClearTableFilmGenre);
        jdbcTemplate.update(sqlClearTableFilms);
    }

}
