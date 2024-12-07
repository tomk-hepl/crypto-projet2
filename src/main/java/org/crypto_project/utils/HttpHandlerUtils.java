package org.crypto_project.utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HttpHandlerUtils
{
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException
    {
        System.out.println("Envoi de la réponse (" + statusCode + ") : --" + response + "--");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException
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


    public static String[] parseValues(String requestBody) {
        // Format attendu : login=username&password=password
        String[] parts = requestBody.split("&");
        if (parts.length == 2) {
            String login = parts[0].split("=")[1];
            String password = parts[1].split("=")[1];
            return new String[]{login, password};
        }
        return null;
    }
}
