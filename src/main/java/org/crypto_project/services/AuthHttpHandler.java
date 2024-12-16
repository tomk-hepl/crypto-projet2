package org.crypto_project.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.crypto_project.databases.SQLLiteDatabase;
import org.crypto_project.utils.ParentClient;
import org.crypto_project.utils.SSLConfig;

import javax.net.ssl.SSLContext;
import java.io.IOException;

import static org.crypto_project.utils.HttpHandlerUtils.*;

public class AuthHttpHandler implements HttpHandler
{
    private final SQLLiteDatabase database;

    private static final  int ACQ_PORT = 9043;
    private final SSLContext sslContext;

    public AuthHttpHandler(SQLLiteDatabase database) {
        this.database = database;

        try {
            this.sslContext = SSLConfig.setupSSLContextFromProperties(
                    "config.properties",
                    "https.keystore.path",
                    "https.truststore.path"
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        if (token == null) {
            sendResponse(exchange, 400, "Invalid token.");
        }

        System.out.println("token : " + token);

        boolean isValid = database.verifyUser(login, password);

        if (isValid) {
            try {

                ParentClient client = new ParentClient(ACQ_PORT);
                client.init(this.sslContext);
                client.send(token);
                String ack = client.read();

                //à modif en fonction du statuscode de retour de l'ACQ
                switch (ack) {
                    case "ACK" -> {
                        exchange.getResponseHeaders().set("Location", "https://127.0.0.1:8043/api/success");
                        exchange.sendResponseHeaders(302, -1); // 302 : Redirection Found
                    }
                    case "USED" -> {
                        exchange.getResponseHeaders().set("Location", "https://127.0.0.1:8043/api/used");
                        exchange.sendResponseHeaders(302, -1); // 302 : Redirection Found
                    }
                    case "NACK" -> {
                        exchange.getResponseHeaders().set("Location", "https://127.0.0.1:8043/api/fail");
                        exchange.sendResponseHeaders(302, -1); // 302 : Redirection Found
                    }
                }

            } catch (IOException e) {
                System.err.println("Erreur lors de la communication avec le serveur ACQ : " + e.getMessage());
                try {
                    sendResponse(exchange, 500, "Erreur interne lors de la communication avec ACQ.");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        } else {
            sendResponse(exchange, 401, "Invalid credentials.");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {

        String path = exchange.getRequestURI().getPath();

        switch (path) {
            case "/api/auth" -> sendResponse(exchange, 200, getAuthPage());
            case "/api/success" -> sendResponse(exchange, 200, getSuccessPage());
            case "/api/fail" -> sendResponse(exchange, 200, getFailPage());
            case "/api/used" -> sendResponse(exchange, 200, getFailPage("⏳",
                    "Token Expiré",
                    "Votre token d'authentification a expiré. Veuillez réessayer avec un nouveau token."));
            case "/api/loading" -> sendResponse(exchange, 200, getLoadingPage());
            case null, default -> sendResponse(exchange, 404, "Page not found.");
        }

    }

}
