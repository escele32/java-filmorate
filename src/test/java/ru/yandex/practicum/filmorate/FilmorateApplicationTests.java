package ru.yandex.practicum.filmorate;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class FilmorateApplicationTests {

	@Autowired
	UserStorage userStorage;

	@Autowired
	FilmStorage filmStorage;

	@Autowired
	UserController userController;

	@Autowired
	FilmController filmController;

	@BeforeEach
	void setup() {
		userStorage.clear();
		filmStorage.clear();
	}

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
		User createdUser = userController.createUser(user);
		User createdUserNoName = userController.createUser(userNoName);
		System.out.println(userController.getAllUsers());
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
		User createdUser = userController.createUser(user);
		System.out.println(userController.getAllUsers());
		User newUser = User.builder()
				.id(createdUser.getId())
				.login("Wolf")
				.build();
		User updateUser = userController.updateUser(newUser, newUser.getId());
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
		Film createdFilm = filmController.addFilm(film);
		System.out.println(filmController.getAllFilms());
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
		Film createdFilm = filmController.addFilm(film);
		System.out.println(filmController.getAllFilms());
		Film newFilm = Film.builder()
				.id(createdFilm.getId())
				.description("Тру-ля-ля!!")
				.build();
		Film updateFilm = filmController.updateFilm(newFilm, newFilm.getId());
		assertEquals("Тру-ля-ля!!", updateFilm.getDescription());
	}

	@Test
	void testFailUserCreate() {
		User failEmailUser = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asdya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		ValidationException validationException = assertThrows(ValidationException.class,
				() -> userController.createUser(failEmailUser));
		assertEquals("Электронная почта не может быть пустой и должна содержать символ @",
				validationException.getMessage());
	}

	@Test
	void testFailFilmCreate() {
		Film failDurationFilm = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(-128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		ValidationException validationException =  assertThrows(ValidationException.class,
				() -> filmController.addFilm(failDurationFilm));
		assertEquals("Продолжительность фильма должна быть положительным числом",
				validationException.getMessage());
	}

	@Test
	void testFailUserUpdate() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		userController.createUser(user);
		User failIdUser = User.builder()
				.id(100L)
				.login("Wolf")
				.build();
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> userController.updateUser(failIdUser, failIdUser.getId()));
		assertEquals(String.format("Пользователь с id %d не найден\n", failIdUser.getId()),
				notFoundException.getMessage());
	}

	@Test
	void testFailFilmUpdate() {
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		filmController.addFilm(film);
		Film failIdFilm = Film.builder()
				.id(200L)
				.description("Тру-ля-ля!!")
				.build();
		NotFoundException notFoundException = assertThrows(NotFoundException.class,
				() -> filmController.updateFilm(failIdFilm, failIdFilm.getId()));
		assertEquals(String.format("Фильм с id %d не найден\n", failIdFilm.getId()),
				notFoundException.getMessage());
	}

	@Test
	void testDeleteUser() {
		User user1 = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		User user2 = User.builder()
				.name("Mariya")
				.login("Lion")
				.email("zxc@nm.bg")
				.birthday(LocalDate.of(2005, 10, 23))
				.build();
		userController.createUser(user1);
		userController.createUser(user2);
		System.out.println(userController.getAllUsers());
		userController.deleteUser(user1.getId());
		assertEquals(Optional.empty(), userController.getUserById(user1.getId()));
		System.out.println(userController.getAllUsers());
	}

	@Test
	void testDeleteFilm() {
		Film film1 = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		Film film2 = Film.builder()
				.name("Скалолаз")
				.description("Залезли...")
				.duration(28)
				.releaseDate(LocalDate.of(2003, 1, 7))
				.build();
		filmController.addFilm(film1);
		filmController.addFilm(film2);
		System.out.println(filmController.getAllFilms());
		filmController.deleteFilm(film1.getId());
		assertEquals(Optional.empty(), filmController.getFilmById(film1.getId()));
		System.out.println(filmController.getAllFilms());
	}

	@Test
	void testAddFriend() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		User friend = User.builder()
				.name("Mariya")
				.login("Lion")
				.email("zxc@nm.bg")
				.birthday(LocalDate.of(2005, 10, 23))
				.build();
		userController.createUser(user);
		userController.createUser(friend);
		System.out.println(userController.getAllUsers());
		userController.addFriend(user.getId(), friend.getId());
		System.out.println(userController.getAllUsers());
		assertEquals(List.of(userController.getUserById(friend.getId()).get()),
				userController.getFriends(user.getId()));
	}

	@Test
	void testRemoveFriend() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		User friend = User.builder()
				.name("Mariya")
				.login("Lion")
				.email("zxc@nm.bg")
				.birthday(LocalDate.of(2005, 10, 23))
				.build();
		userController.createUser(user);
		userController.createUser(friend);
		System.out.println(userController.getAllUsers());
		userController.addFriend(user.getId(), friend.getId());
		System.out.println(userController.getAllUsers());
		userController.removeFriend(user.getId(), friend.getId());
		System.out.println(userController.getAllUsers());
		assertEquals(List.of(), userController.getFriends(user.getId()));
		System.out.println(userController.getFriends(user.getId()));
	}

	@Test
	void testCommonFriends() {
		User user1 = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		User user2 = User.builder()
				.name("Mariya")
				.login("Lion")
				.email("zxc@nm.bg")
				.birthday(LocalDate.of(2005, 10, 23))
				.build();
		User user3 = User.builder()
				.name("Mila")
				.login("Dove")
				.email("qwe@ml.com")
				.birthday(LocalDate.of(2016, 3, 15))
				.build();
		User user4 = User.builder()
				.name("Gun")
				.login("Hanter")
				.email("lkj@vbnm.out")
				.birthday(LocalDate.of(1990, 8, 30))
				.build();
		userController.createUser(user1);
		userController.createUser(user2);
		userController.createUser(user3);
		userController.createUser(user4);
		System.out.println(userController.getAllUsers());
		userController.addFriend(user1.getId(), user2.getId());
		userController.addFriend(user1.getId(), user3.getId());
		userController.addFriend(user2.getId(), user4.getId());
		System.out.println(userController.getCommonFriends(user1.getId(), user4.getId()));
		assertEquals(List.of(userController.getUserById(user2.getId()).get()),
				userController.getCommonFriends(user1.getId(), user4.getId()));
		System.out.println(userController.getAllUsers());
	}

	@Test
	void testAddLike() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		userController.createUser(user);
		filmController.addFilm(film);
		System.out.println(userController.getAllUsers());
		System.out.println(filmController.getAllFilms());
		filmController.addLike(film.getId(), user.getId());
		System.out.println(filmController.getFilmById(film.getId()));
	}

	@Test
	void testRemoveLike() {
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		Film film = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		userController.createUser(user);
		filmController.addFilm(film);
		System.out.println(userController.getAllUsers());
		System.out.println(filmController.getAllFilms());
		filmController.addLike(film.getId(), user.getId());
		System.out.println(filmController.getFilmById(film.getId()));
		filmController.removeLike(film.getId(),user.getId());
		assertEquals(Set.of(), film.getLikes());
		System.out.println(filmController.getFilmById(film.getId()));
	}

	@Test
	void testGetPopularFilms() {
		Film film1 = Film.builder()
				.name("Titanic")
				.description("Приплыли...")
				.duration(128)
				.releaseDate(LocalDate.of(1994, 9, 23))
				.build();
		Film film2 = Film.builder()
				.name("Lion")
				.description("Пристрелили...")
				.duration(145)
				.releaseDate(LocalDate.of(2004, 1, 4))
				.build();
		Film film3 = Film.builder()
				.name("Java")
				.description("Покодили...")
				.duration(93)
				.releaseDate(LocalDate.of(2016, 7, 11))
				.build();
		Film film4 = Film.builder()
				.name("Voodoo")
				.description("Пошаманили...")
				.duration(104)
				.releaseDate(LocalDate.of(1965, 12, 30))
				.build();
		User user = User.builder()
				.name("Rafael")
				.login("Rabbit")
				.email("asd@ya.ru")
				.birthday(LocalDate.of(1999, 5, 11))
				.build();
		filmController.addFilm(film1);
		filmController.addFilm(film2);
		filmController.addFilm(film3);
		filmController.addFilm(film4);
		userController.createUser(user);
		System.out.println(filmController.getAllFilms());
		filmController.addLike(film2.getId(), user.getId());
		filmController.addLike(film4.getId(), user.getId());
		assertEquals(List.of(film2, film4), filmController.getPopular(2));
		System.out.println(filmController.getPopular(2));
	}

}
