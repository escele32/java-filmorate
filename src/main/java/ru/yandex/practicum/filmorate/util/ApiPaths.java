package ru.yandex.practicum.filmorate.util;

public class ApiPaths {
    public static final String USERS = "/users";
    public static final String USERID = "/{userId}";
    public static final String FILMS = "/films";
    public static final String FILMID = "/{filmId}";
    public static final String POPULAR = "/popular";
    public static final String MPA = "/mpa";
    public static final String GENRES = "/genres";
    public static final String MPAID = "/{mpaId}";
    public static final String GENREID = "/{genreId}";
    public static final String USERID_FRIENDS_FRIENDID = "/{userId}/friends/{friendId}";
    public static final String USERID_FRIENDS = "/{userId}/friends";
    public static final String USERID_FRIENDS_COMMON_FRIENDS = "/{userId}/friends/common/{otherId}";
    public static final String FILMID_LIKE_USERID = "/{filmId}/like/{userId}";
}
