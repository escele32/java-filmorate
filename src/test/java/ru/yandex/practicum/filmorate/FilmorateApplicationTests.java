package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	private static UserController userController = new UserController();
	private static FilmController filmController = new FilmController();

	@Test
	void testUserCreate() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		User userNoName = User.builder()
				.login("Wolf")
				.email("zxc@gmail.com")
				.birthday(LocalDate.of(2009, 1, 25))
				.build();
		User createdUser = userController.create(user);
		User createdUserNoName = userController.create(userNoName);
		assertNotNull(createdUser);
		assertNotNull(createdUserNoName);
		assertNotNull(createdUser.getId());
		assertNotNull(createdUserNoName.getId());
		assertEquals("Rafael", createdUser.getName());
		assertEquals("Wolf", createdUserNoName.getName());
	}

	@Test
	void testUserUpdate() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		User createdUser = userController.create(user);
		User newUser = User.builder()
				.id(createdUser.getId())
				.login("Wolf")
				.build();
		User updateUser = userController.update(newUser);
		assertEquals("Wolf", updateUser.getLogin());
	}

	@Test
	void testFilmCreate() {
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		Film createdFilm = filmController.create(film);
		assertNotNull(createdFilm);
		assertNotNull(createdFilm.getId());
		assertEquals("Titanic", createdFilm.getName());
	}

	@Test
	void testFilmUpdate() {
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		Film createdFilm = filmController.create(film);
		Film newFilm = Film.builder()
				.id(createdFilm.getId())
				.description("Тру-ля-ля!!")
				.build();
		Film updateFilm = filmController.update(newFilm);
		assertEquals("Тру-ля-ля!!", updateFilm.getDescription());
	}

	@Test
	void testFailUserCreate() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asdya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		System.out.println(user);
		assertThrows(ValidationException.class, () -> {
			userController.create(user);
		});
	}

	@Test
	void testFailFilmCreate() {
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(-128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		System.out.println(film);
		assertThrows(ValidationException.class, () -> {
			filmController.create(film);
		});
	}

	@Test
	void testFailUserUpdate() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		userController.create(user);
		User newUser = User.builder()
				.id(100L)
				.login("Wolf")
				.build();
		assertThrows(ValidationException.class, () -> {
			userController.update(newUser);
		});
	}

	@Test
	void testFailFilmUpdate() {
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		filmController.create(film);
		Film newFilm = Film.builder()
				.id(200L)
				.description("Тру-ля-ля!!")
				.build();
		assertThrows(ValidationException.class, () -> {
			filmController.update(newFilm);
		});
	}
}
