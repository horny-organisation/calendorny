package ru.calendorny.authservice.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.calendorny.authservice.entity.Account;
import ru.calendorny.authservice.entity.Profile;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_FIND_BY_EMAIL =
            """
        SELECT * FROM accounts WHERE email = ? LIMIT 1
        """;
    private static final String SQL_FIND_BY_ID = """
        SELECT * FROM accounts WHERE id = ? LIMIT 1
        """;
    private static final String SQL_SAVE =
            """
        INSERT INTO accounts (id, email, password_hash) VALUES (?, ?, ?)
        """;
    private static final String SQL_FIND_BY_ID_WITH_PROFILE =
            """
        SELECT * FROM accounts a JOIN profiles p ON a.id = p.user_id WHERE id = ? LIMIT 1
        """;

    private RowMapper<Account> accountRowMapper() {
        return (rs, rowNum) -> mapAccount(rs);
    }

    private Account mapAccount(ResultSet rs) throws SQLException {
        return new Account(
                UUID.fromString(rs.getString("id")),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getBoolean("is_active"));
    }

    public Optional<Account> findByEmail(String email) {
        return jdbcTemplate.query(SQL_FIND_BY_EMAIL, accountRowMapper(), email).stream()
                .findFirst();
    }

    public Optional<Account> findById(UUID id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, accountRowMapper(), id).stream()
                .findFirst();
    }

    public void save(Account account) {
        jdbcTemplate.update(SQL_SAVE, account.getId(), account.getEmail(), account.getPasswordHash());
    }

    public Optional<Account> findByIdWithProfile(UUID id) {
        return jdbcTemplate.query(
                SQL_FIND_BY_ID_WITH_PROFILE,
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    Account account = mapAccount(rs);
                    Profile profile = mapProfile(rs);
                    return Optional.of(new Account(account, profile));
                },
                id);
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
}
