package org.crypto_project.databases;

import org.crypto_project.utils.SecurityUtils;

import java.sql.*;

public class SQLLiteDatabase implements AutoCloseable {
    private final String url;
    private Connection connection;

    public SQLLiteDatabase(String url) {
        this.url = url;
    }


    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url);
        System.out.println("Successful connection to the SQLite database.");
    }

    @Override
    public void close()
    {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection to the database closed.");
            } catch (SQLException e) {
                System.err.println("Error closing the database : " + e.getMessage());
            }
        }
    }

    public boolean verifyUser(String login, String password) {
        String query = "SELECT salt, password_hash FROM users WHERE login = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Récupère le sel et le hash stockés
                    String salt = rs.getString("salt");
                    String storedHashedPassword = rs.getString("password_hash");

                    // Recrée le hash avec le sel et le mot de passe fourni
                    String calculatedHash = SecurityUtils.hashPassword(password, salt);

                    // Compare le hash calculé avec celui stocké
                    return calculatedHash.equals(storedHashedPassword);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createTable() throws Exception {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                login TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                salt TEXT NOT NULL,
                password_hash TEXT NOT NULL
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }


    public void addUser(String login, String password) throws Exception {
        String salt = SecurityUtils.generateSalt();
        String hashedPassword = SecurityUtils.hashPassword(password, salt);

        String insertUserSQL = "INSERT INTO users (login, password, salt, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, login);
            pstmt.setString(2,password);
            pstmt.setString(3, salt);
            pstmt.setString(4, hashedPassword);
            pstmt.executeUpdate();
        }
    }
}
