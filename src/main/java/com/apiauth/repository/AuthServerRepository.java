package com.apiauth.repository;

import com.apiauth.database.Database;
import com.apiauth.model.Token;
import com.apiauth.model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class AuthServerRepository {
    public User createUser(User user) {
        String createdAt = LocalDate.now().toString();
        String updatedAt = LocalDateTime.now().toString();
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        String sql = "INSERT INTO users (email, doc_number, password, username, full_name, loggedin, created_at, updated_at) VALUES ('"
                + user.getEmail() + "', '"
                + user.getDocNumber() + "', '"
                + user.getPassword() + "', '"
                + user.getUsername() + "', '"
                + user.getFullName() + "', "
                + (Boolean.TRUE.equals(user.getLoggedin()) ? 1 : 0) + ", '"
                + createdAt + "', '"
                + updatedAt + "')";

        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS id");
            if (rs.next()) {
                user.setId(rs.getLong("id"));
            }
            user.setFailedAttempts(0);
            user.setLockedUntil(null);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public Optional<User> findUserByEmail(String email) {
        return findSingle("SELECT * FROM users WHERE email = '" + email + "'");
    }

    public Optional<User> findUserByDocument(String document) {
        return findSingle("SELECT * FROM users WHERE doc_number = '" + document + "'");
    }

    public Optional<User> findUserByEmailAndDocument(String email, String document) {
        return findSingle("SELECT * FROM users WHERE email = '" + email + "' AND doc_number = '" + document + "'");
    }

    public Optional<User> findUserByToken(String token) {
        return findSingle("SELECT u.* FROM users u JOIN tokens t ON t.user_id = u.id WHERE t.token = '" + token + "'");
    }

    public Token saveToken(Long userId, String rawToken) {
        String createdAt = LocalDateTime.now().toString();
        String sql = "INSERT INTO tokens (user_id, token, created_at) VALUES (" + userId + ", '" + rawToken + "', '" + createdAt + "')";
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            ResultSet rs = statement.executeQuery("SELECT last_insert_rowid() AS id");
            Long id = rs.next() ? rs.getLong("id") : null;
            return new Token(id, userId, rawToken, createdAt);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to store token: " + e.getMessage(), e);
        }
    }

    public boolean deleteToken(String rawToken) {
        String sql = "DELETE FROM tokens WHERE token = '" + rawToken + "'";
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            return statement.executeUpdate(sql) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete token: " + e.getMessage(), e);
        }
    }

    public void updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE users SET password = '" + newPassword + "', updated_at = '" + LocalDateTime.now() + "' WHERE id = " + userId;
        executeUpdate(sql);
    }

    public void setLoggedIn(Long userId, boolean loggedIn) {
        String sql = "UPDATE users SET loggedin = " + (loggedIn ? 1 : 0) + ", updated_at = '" + LocalDateTime.now() + "' WHERE id = " + userId;
        executeUpdate(sql);
    }

    public void updateFailedAttempts(Long userId, int failedAttempts) {
        String sql = "UPDATE users SET failed_attempts = " + failedAttempts + ", updated_at = '" + LocalDateTime.now() + "' WHERE id = " + userId;
        executeUpdate(sql);
    }

    public void lockUser(Long userId, int failedAttempts, LocalDateTime lockedUntil) {
        String sql = "UPDATE users SET failed_attempts = " + failedAttempts + ", locked_until = '" + lockedUntil + "', updated_at = '" + LocalDateTime.now() + "' WHERE id = " + userId;
        executeUpdate(sql);
    }

    public void resetFailedAttempts(Long userId) {
        String sql = "UPDATE users SET failed_attempts = 0, locked_until = NULL, updated_at = '" + LocalDateTime.now() + "' WHERE id = " + userId;
        executeUpdate(sql);
    }

    private void executeUpdate(String sql) {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to run SQL: " + sql + " cause: " + e.getMessage(), e);
        }
    }

    private Optional<User> findSingle(String sql) {
        try (Connection connection = Database.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query SQL: " + sql + " cause: " + e.getMessage(), e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setDocNumber(rs.getString("doc_number"));
        user.setPassword(rs.getString("password"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        user.setLoggedin(rs.getInt("loggedin") == 1);
        Object attempts = rs.getObject("failed_attempts");
        user.setFailedAttempts(attempts == null ? null : ((Number) attempts).intValue());
        user.setLockedUntil(rs.getString("locked_until"));
        user.setCreatedAt(rs.getString("created_at"));
        user.setUpdatedAt(rs.getString("updated_at"));
        return user;
    }
}
