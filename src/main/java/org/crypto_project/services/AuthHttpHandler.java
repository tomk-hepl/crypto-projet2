package org.crypto_project.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.crypto_project.databases.SQLLiteDatabase;

import java.io.IOException;

import static org.crypto_project.utils.HttpHandlerUtils.*;

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

        switch (requestMethod) {
            case "POST":
                handlePostRequest(exchange);
            case "GET":
                handleGetRequest(exchange);
            default:
                sendResponse(exchange, 405, "Methode non autorisee !");

        }
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
        String token = credentials[2];
        boolean isValid = database.verifyUser(login, password);

        if (isValid) {
            // sendResponse(exchange, 200, "Authentication successful!");
            // à modifier, ici c'est pour tester l'affichage des pages
            if (token.equals("fail")) {
                sendResponse(exchange, 200, getFailPage());
            } else if (token.equals("success")) {
                sendResponse(exchange, 200, getSuccessPage());
            } else {
                sendResponse(exchange, 200, getLoadingPage());
            }
        } else {
            sendResponse(exchange, 401, "Invalid credentials.");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String token = getQueryParam(exchange, "token");
        if (token == null) {
            sendResponse(exchange, 400, "Invalid token.");
        } else {
            // à modifier, ici c'est pour tester l'affichage des pages
            sendResponse(exchange, 200, getAuthPage(token));
        }
    }
}
