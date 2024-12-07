package org.crypto_project.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.crypto_project.databases.SQLLiteDatabase;

import java.io.IOException;

import static org.crypto_project.utils.HttpHandlerUtils.*;
import static org.crypto_project.utils.HttpHandlerUtils.sendResponse;

public class AuthHttpHandler implements HttpHandler
{
    private final SQLLiteDatabase database;

    public AuthHttpHandler(SQLLiteDatabase database) {
        this.database = database;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods","POST");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers","Content-Type");

        if (requestMethod.equals("POST")) {
            handlePostRequest(exchange);
        }
        sendResponse(exchange, 405, "Methode non autorisee !");
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        String[] credentials = parseValues(requestBody);
        if (credentials == null) {
            sendResponse(exchange, 400, "Invalid credentials format.");
            return;
        }

        String login = credentials[0];
        String password = credentials[1];
        boolean isValid = database.verifyUser(login, password);

        if (isValid) {
            sendResponse(exchange, 200, "Authentication successful!");
        } else {
            sendResponse(exchange, 401, "Invalid credentials.");
        }
    }
}
