package ru.calendorny.authservice.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.calendorny.authservice.dto.request.UserProfileEdit;
import ru.calendorny.authservice.entity.Profile;

@Repository
@RequiredArgsConstructor
public class ProfileRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SQL_FIND_BY_USER_ID =
            """
            SELECT *
            FROM profiles
            WHERE user_id = ?
            LIMIT 1
            """;
    private static final String SQL_SAVE =
            """
            INSERT INTO profiles
            (user_id, first_name, last_name, birth_date, phone_number, telegram, timezone, language)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String SQL_MERGE =
            """
            MERGE INTO profiles AS target
            USING (VALUES (
                :userId::uuid,
                :firstName::text,
                :lastName::text,
                :birthDate::date,
                :phoneNumber::text,
                :telegram::text,
                :timezone::text,
                :language::text
            )) AS source (
                user_id,
                first_name,
                last_name,
                birth_date,
                phone_number,
                telegram,
                timezone,
                language
            )
            ON target.user_id = source.user_id
            WHEN MATCHED THEN
                UPDATE SET
                    first_name   = COALESCE(source.first_name, target.first_name),
                    last_name    = COALESCE(source.last_name, target.last_name),
                    birth_date   = COALESCE(source.birth_date, target.birth_date),
                    phone_number = COALESCE(source.phone_number, target.phone_number),
                    telegram     = COALESCE(source.telegram, target.telegram),
                    timezone     = COALESCE(source.timezone, target.timezone),
                    language     = COALESCE(source.language, target.language)
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

    public void merge(UUID id, UserProfileEdit userProfileEdit) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("userId", id)
            .addValue("firstName", emptyToNull(userProfileEdit.firstName()))
            .addValue("lastName", emptyToNull(userProfileEdit.lastName()))
            .addValue("birthDate", userProfileEdit.birthdate())
            .addValue("phoneNumber", emptyToNull(userProfileEdit.phoneNumber()))
            .addValue("telegram", emptyToNull(userProfileEdit.telegram()))
            .addValue("timezone", emptyToNull(userProfileEdit.timezone()))
            .addValue("language", emptyToNull(userProfileEdit.language()));

        namedParameterJdbcTemplate.update(SQL_MERGE, params);
    }

    private String emptyToNull(String str) {
        return (str == null || str.isBlank()) ? null : str;
    }

}
