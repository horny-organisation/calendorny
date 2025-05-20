package ru.calendorny.authservice.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.calendorny.authservice.entity.RefreshToken;

@Repository
public class RefreshTokenRepository {

    private static final String SQL_FIND_BY_TOKEN =
            """
            SELECT *
            FROM refresh_tokens
            WHERE token = ?
            LIMIT 1
            """;

    private static final String SQL_SAVE =
            """
            INSERT INTO refresh_tokens
            (token, user_id)
            VALUES (?, ?)
            """;

    private static final String SQL_DELETE_BY_TOKEN =
            """
            DELETE FROM refresh_tokens
           WHERE token = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<RefreshToken> rowMapper() {
        return (rs, rowNum) -> mapRefreshToken(rs);
    }

    private RefreshToken mapRefreshToken(ResultSet rs) throws SQLException {
        return new RefreshToken(
                rs.getString("token"),
                UUID.fromString(rs.getString("user_id")),
                rs.getTimestamp("created_at").toInstant());
    }

    public Optional<RefreshToken> findByToken(String token) {
        return jdbcTemplate.query(SQL_FIND_BY_TOKEN, rowMapper(), token).stream()
                .findFirst();
    }

    public void save(RefreshToken refreshToken) {
        jdbcTemplate.update(SQL_SAVE, refreshToken.getToken(), refreshToken.getUserId());
    }

    public void deleteByToken(String token) {
        jdbcTemplate.update(SQL_DELETE_BY_TOKEN, token);
    }
}
