package org.crypto_project.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.crypto_project.databases.SQLLiteDatabase;
import org.crypto_project.utils.ParentClient;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.crypto_project.utils.HttpHandlerUtils.*;

public class AuthHttpHandler implements HttpHandler
{
    private final SQLLiteDatabase database;

    private static final  int ACQ_PORT = 9043;

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
        String urlDecodedBase64 = URLDecoder.decode(token, StandardCharsets.UTF_8.name());
        if (token == null) {
            sendResponse(exchange, 400, "Invalid token.");
        }
        System.out.println("token : " + token);
        System.out.println("token bon format : " + urlDecodedBase64);
        boolean isValid = database.verifyUser(login, password);

        if (isValid) {

            //sendResponse(exchange, 200, "Authentication successful!");
            try {

                ParentClient client = new ParentClient(ACQ_PORT, null);
                client.send(urlDecodedBase64);
                String ack = client.read();

                //à modif en fonction du statuscode de retour de l'ACQ
                if (ack.equals("ACK")) {

                    exchange.getResponseHeaders().set("Location", "https://127.0.0.1:8043/api/success");
                    exchange.sendResponseHeaders(302, -1); // 302 : Redirection Found
                } else if (ack.equals("NACK")) {
                    exchange.getResponseHeaders().set("Location", "https://127.0.0.1:8043/api/fail");
                    exchange.sendResponseHeaders(302, -1); // 302 : Redirection Found

                }


            } catch (IOException e) {
                System.err.println("Erreur lors de la communication avec le serveur ACQ : " + e.getMessage());
                sendResponse(exchange, 500, "Erreur interne lors de la communication avec ACQ.");
            }

        } else {
            sendResponse(exchange, 401, "Invalid credentials.");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        if ("/api/auth".equals(path))
        {
            sendResponse(exchange, 200, getAuthPage());
        }
        else if ("/api/success".equals(path))
        {
            sendResponse(exchange, 200, getSuccessPage());
        }
        else if ("/api/fail".equals(path))
        {
            sendResponse(exchange, 200, getFailPage());
        }
        else if ("/api/loading".equals(path))
        {

            sendResponse(exchange, 200, getLoadingPage());
        }
        else
        {
            sendResponse(exchange, 404, "Page not found.");
        }

    }

}
