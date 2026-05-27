-- Вставляем жанры
INSERT INTO genres (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

-- Вставляем рейтинги MPA
INSERT INTO mpa (name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

-- Вставляем пользователей
INSERT INTO users (name, login, email, birthday)
VALUES ('Иван Иванов', 'ivanov', 'ivanov@example.com', '1990-05-15'),
       ('Мария Смирнова', 'maria', 'maria@example.com', '1985-08-22'),
       ('Петр Петров', 'petr', 'petr@example.com', '1992-11-30');

-- Вставляем фильмы
INSERT INTO films (name, description, duration, release_date, mpa_id)
VALUES ('Фильм 1', 'Описание фильма 1', 120, '2020-01-01', 3),
       ('Фильм 2', 'Описание фильма 2', 90, '2019-05-15', 2),
       ('Фильм 3', 'Описание фильма 3', 150, '2021-07-20', 4);

-- Вставляем связи фильмов и жанров (без дубликатов)
INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1),
       (1, 5),
       (2, 2),
       (3, 4);

-- Вставляем дружеские связи
INSERT INTO friends (user_id, friend_id, status)
VALUES (1, 2, 'CONFIRMED'),
       (1, 3, 'NOT_CONFIRMED');

-- Вставляем лайки
INSERT INTO likes (user_id, film_id)
VALUES (1, 1),
       (2, 1),
       (2, 3),
       (3, 2);
