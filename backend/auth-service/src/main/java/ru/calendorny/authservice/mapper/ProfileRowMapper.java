package ru.calendorny.authservice.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import ru.calendorny.authservice.entity.Profile;

public class ProfileRowMapper implements RowMapper<Profile> {

    @Override
    public Profile mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Profile(
            UUID.fromString(rs.getString("user_id")),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
            rs.getString("phone_number"),
            rs.getString("telegram"),
            rs.getString("timezone"),
            rs.getString("language")
        );
    }
}
