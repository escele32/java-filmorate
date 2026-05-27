package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.row_mappers.MpaRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaDbStorage implements MpaStorage {
    JdbcTemplate jdbcTemplate;
    MpaRowMapper mpaRowMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mpaRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Optional<Mpa> getMpaById(Long mpaId) {
        String sqlGetMpaById = "SELECT * FROM mpa WHERE id = ?";
        List<Mpa> mpa = jdbcTemplate.query(sqlGetMpaById, mpaRowMapper, mpaId);
        return mpa.stream().findFirst();
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sqlGetAllMpa = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlGetAllMpa, mpaRowMapper);
    }

    @Override
    public Mpa createMpa(Mpa mpa) {
        String sqlCreateMpa = "INSERT INTO mpa (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlCreateMpa, new String[] {"id"});
            statement.setString(1, mpa.getName());
            return statement;
        }, keyHolder);
        Long generatedId = keyHolder.getKey().longValue();
        mpa.setId(generatedId);
        return mpa;
    }

    @Override
    public boolean deleteMpa(Long mpaId) {
        String sqlDeleteMpa = "DELETE FROM mpa WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sqlDeleteMpa, mpaId);
        return rowsAffected > 0;
    }

    @Override
    public void clear() {
        String sqlClearTableMpa = "DELETE FROM mpa";
        jdbcTemplate.update(sqlClearTableMpa);
    }

}
