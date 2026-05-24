package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = {
        "ru.yandex.practicum.filmorate.controller",
        "ru.yandex.practicum.filmorate.service",
        "ru.yandex.practicum.filmorate.storage",
        "ru.yandex.practicum.filmorate.row_mappers"
})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class FilmorateApplicationDbTests {
    UserController userController;
    FilmController filmController;
    MpaController mpaController;
    GenreController genreController;

    @Test
    void testFindUserById() {
        Optional<User> userOptional = userController.getUserById(1L);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        System.out.println(userOptional);
    }

    @Test
    void testFindFilmById() {
        Optional<Film> filmOptional = filmController.getFilmById(2L);
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2L));
        System.out.println(filmOptional);
    }

    @Test
    void testFindMpaById() {
        Optional<Mpa> mpaOptional = mpaController.getMpaId(3L);
        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).hasFieldOrPropertyWithValue("id", 3L));
        System.out.println(mpaOptional);
    }

    @Test
    void testFindGenreById() {
        Optional<Genre> genreOptional = genreController.getGenreById(4L);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 4L));
        System.out.println(genreOptional);
    }

    @Test
    void testGetPopularFilms() {
        System.out.println(filmController.getFilms());
        System.out.println(filmController.getPopularFilms(1));
        assertEquals(List.of(filmController.getFilmById(1L).get()), filmController.getPopularFilms(1));
    }

    @Test
    void testGetUserFriends() {
        System.out.println(userController.getAllUsers());
        System.out.println(userController.getFriends(1L));
        assertEquals(List.of(userController.getUserById(2L).get(), userController.getUserById(3L).get()),
                userController.getFriends(1L));
    }

    @Test
    void testAllMpaCount() {
        System.out.println(mpaController.getMpa());
        assertEquals(5, mpaController.getMpa().size());
    }

    @Test
    void testAllGenresCount() {
        System.out.println(genreController.getGenres());
            assertEquals(6, genreController.getGenres().size());
    }

    @Test
    void testAllUsersCount() {
        System.out.println(userController.getAllUsers());
        assertEquals(3, userController.getAllUsers().size());
    }

    @Test
    void testAllFilmsCount() {
        System.out.println(filmController.getFilms());
        assertEquals(3, filmController.getFilms().size());
    }

}