package ru.calendorny.authservice.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.calendorny.authservice.entity.Account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccountRowMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Account(
            UUID.fromString(rs.getString("id")),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getTimestamp("created_at").toInstant(),
            rs.getBoolean("is_active")
        );
    }
}
