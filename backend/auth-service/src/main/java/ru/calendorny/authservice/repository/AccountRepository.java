package ru.calendorny.authservice.repository;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.calendorny.authservice.entity.Account;
import ru.calendorny.authservice.entity.Profile;
import ru.calendorny.authservice.mapper.AccountRowMapper;
import ru.calendorny.authservice.mapper.ProfileRowMapper;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ProfileRowMapper profileRowMapper = new ProfileRowMapper();

    private static final String SQL_FIND_BY_EMAIL =
        """
            SELECT *
            FROM accounts
            WHERE email = ?
            LIMIT 1
            """;
    private static final String SQL_FIND_BY_ID =
        """
            SELECT *
            FROM accounts
            WHERE id = ?
            LIMIT 1
            """;
    private static final String SQL_SAVE =
        """
            INSERT INTO accounts
            (id, email, password_hash)
            VALUES (?, ?, ?)
            """;
    private static final String SQL_FIND_BY_ID_WITH_PROFILE =
        """
            SELECT *
            FROM accounts a
            JOIN profiles p ON a.id = p.user_id
            WHERE id = ?
            LIMIT 1
            """;
    private static final String SQL_UPDATE =
        """
            UPDATE accounts
            SET email = ?, password_hash = ?
            WHERE id = ?
            """;


    private final RowMapper<Account> accountRowMapper = new AccountRowMapper();


    public Optional<Account> findByEmail(String email) {
        return jdbcTemplate.query(SQL_FIND_BY_EMAIL, accountRowMapper, email).stream()
            .findFirst();
    }

    public Optional<Account> findById(UUID id) {
        return jdbcTemplate.query(SQL_FIND_BY_ID, accountRowMapper, id).stream()
            .findFirst();
    }

    public void save(Account account) {
        jdbcTemplate.update(SQL_SAVE, account.getId(), account.getEmail(), account.getPasswordHash());
    }

    public void update(Account account) {
        jdbcTemplate.update(SQL_UPDATE, account.getEmail(), account.getPasswordHash(), account.getId());
    }

    public Optional<Account> findByIdWithProfile(UUID id) {
        return jdbcTemplate.query(
            SQL_FIND_BY_ID_WITH_PROFILE,
            rs -> {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Account account = accountRowMapper.mapRow(rs, 1);
                Profile profile = profileRowMapper.mapRow(rs, 1);
                return Optional.of(new Account(account, profile));
            },
            id
        );
    }

}
