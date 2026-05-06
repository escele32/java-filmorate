CREATE TYPE friendship_status AS ENUM ('PENDING', 'CONFIRMED');
INSERT INTO genres(name)
VALUES ('COMEDY'),
       ('CARTOON'),
       ('THRILLER'),
       ('MELODRAMA'),
       ('ACTION'),
       ('FANTASTIC');
INSERT INTO mpa(name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
INSERT INTO users(name, login, email, birthday)
VALUES ('Иван Иванов', 'ivanov', 'ivanov@example.com', '1990-05-15'),
       ('Мария Смирнова', 'maria', 'maria@example.com', '1985-08-22'),
       ('Петр Петров', 'petr', 'petr@example.com', '1992-11-30');
INSERT INTO films(title, description, duration, release_date, mpa_id)
VALUES ('Фильм 1', 'Описание фильма 1', 120, '2020-01-01', 3),
       ('Фильм 2', 'Описание фильма 2', 90, '2019-05-15', 2),
       ('Фильм 3', 'Описание фильма 3', 150, '2021-07-20', 4);
INSERT INTO film_genre(film_id, genre_id)
VALUES (1, 1), -- Фильм 1 - COMEDY
       (1, 5), -- Фильм 1 - ACTION
       (2, 2), -- Фильм 2 - CARTOON
       (3, 4); -- Фильм 3 - MELODRAMA
INSERT INTO friends(user_id, friend_id, status)
VALUES (1, 2, 'CONFIRMED'), -- Иван и Мария - подтверждено
       (1, 3, 'PENDING'); -- Иван и Петр - ожидает подтверждения
INSERT INTO likes(user_id, film_id)
VALUES (1, 1), -- Иван поставил лайк Фильм 1
       (2, 1), -- Мария поставила лайк Фильм 1
       (2, 3), -- Мария поставила лайк Фильм 3
       (3, 2); -- Петр поставил лайк Фильм 2
