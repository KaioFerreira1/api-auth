package com.apiauth.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:auth.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            init();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite driver not found. Add lib/sqlite-jdbc.jar to the classpath.", e);
        }
    }

    private static void init() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT NOT NULL UNIQUE, " +
                    "doc_number TEXT NOT NULL UNIQUE, " +
                    "password TEXT NOT NULL, " +
                    "username TEXT, " +
                    "full_name TEXT, " +
                    "loggedin INTEGER DEFAULT 0, " +
                    "failed_attempts INTEGER DEFAULT 0, " +
                    "locked_until TEXT, " +
                    "created_at TEXT, " +
                    "updated_at TEXT" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS tokens (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "token TEXT NOT NULL, " +
                    "created_at TEXT, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")");

            ensureColumn(statement, "users", "failed_attempts", "INTEGER DEFAULT 0");
            ensureColumn(statement, "users", "locked_until", "TEXT");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private static void ensureColumn(Statement statement, String table, String column, String definition) {
        try {
            statement.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
        } catch (SQLException e) {
            String message = e.getMessage();
            if (message == null || !message.toLowerCase().contains("duplicate column name")) {
                e.printStackTrace();
            }
        }
    }
}
