package org.crypto_project.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.crypto_project.database.dbUserMethods.hashPassword;

public class ServerMethods
{


    static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException
    {
        System.out.println("Envoi de la réponse (" + statusCode + ") : --" + response + "--");
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    static String readRequestBody(HttpExchange exchange) throws IOException
    {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            requestBody.append(line);
        }
        reader.close();
        return requestBody.toString();
    }


    static String[] parseCredentials(String requestBody) {
        // Format attendu : login=username&password=password
        String[] parts = requestBody.split("&");
        if (parts.length == 2) {
            String login = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];
            return new String[]{login, password};
        }
        return null;
    }


    static boolean verifyUser(Connection connection, String login, String password) {
        String query = "SELECT salt, password_hash FROM users WHERE login = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Récupère le sel et le hash stockés
                    String salt = rs.getString("salt");
                    String storedHashedPassword = rs.getString("password_hash");

                    // Recrée le hash avec le sel et le mot de passe fourni
                    String calculatedHash = hashPassword(password, salt);

                    // Compare le hash calculé avec celui stocké
                    return calculatedHash.equals(storedHashedPassword);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
