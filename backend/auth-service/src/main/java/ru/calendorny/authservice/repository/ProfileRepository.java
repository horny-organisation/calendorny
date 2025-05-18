package ru.calendorny.authservice.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.calendorny.authservice.entity.Profile;

@Repository
@RequiredArgsConstructor
public class ProfileRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_FIND_BY_USER_ID =
            """
            SELECT * FROM profiles WHERE user_id = ? LIMIT 1
        """;
    private static final String SQL_SAVE =
            """
            INSERT INTO profiles (user_id, first_name, last_name, birth_date, phone_number, telegram, timezone, language)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private RowMapper<Profile> profileRowMapper() {
        return (rs, rowNum) -> mapProfile(rs);
    }

    private Profile mapProfile(ResultSet rs) throws SQLException {
        return new Profile(
                UUID.fromString(rs.getString("user_id")),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
                rs.getString("phone_number"),
                rs.getString("telegram"),
                rs.getString("timezone"),
                rs.getString("language"));
    }

    public Optional<Profile> findByUserId(UUID userId) {
        return jdbcTemplate.query(SQL_FIND_BY_USER_ID, profileRowMapper(), userId).stream()
                .findFirst();
    }

    public void save(Profile profile) {
        jdbcTemplate.update(
                SQL_SAVE,
                profile.getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getBirthDate(),
                profile.getPhoneNumber(),
                profile.getTelegram(),
                profile.getTimezone(),
                profile.getLanguage());
    }
}
