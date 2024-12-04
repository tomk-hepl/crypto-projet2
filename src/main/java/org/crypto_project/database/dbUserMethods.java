package org.crypto_project.database;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Base64;

public class dbUserMethods
{



    static void createTable(Connection connection) throws Exception {

        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                login TEXT NOT NULL UNIQUE,
                salt TEXT NOT NULL,
                password_hash TEXT NOT NULL
            );
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }

    }


    static void addUser(Connection connection, String login, String password) throws Exception {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        String insertUserSQL = "INSERT INTO users (login, salt, password_hash) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, login);
            pstmt.setString(2, salt);
            pstmt.setString(3, hashedPassword);
            pstmt.executeUpdate();
        }
    }


    static String generateSalt() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16]; // 16 octets pour le sel
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }


    static String hashPassword(String password, String salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt.getBytes());
        byte[] hashBytes = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
