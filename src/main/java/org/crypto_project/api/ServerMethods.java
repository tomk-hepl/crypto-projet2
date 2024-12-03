package org.crypto_project.api;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

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
        // Format attendu : login=username&password=hashedpassword
        String[] parts = requestBody.split("&");
        if (parts.length == 2) {
            String login = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];
            return new String[]{login, password};
        }
        return null;
    }


    static boolean verifyUser(String login, String password) {
        // Exemple fictif de vérification
        String storedHashedPassword = "hashed_password"; // Récupéré depuis la BD
        return login.equals("user") && password.equals(storedHashedPassword);
    }

}
