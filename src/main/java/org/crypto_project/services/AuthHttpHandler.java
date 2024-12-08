package org.crypto_project.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.crypto_project.databases.SQLLiteDatabase;
import org.crypto_project.utils.ParentClient;

import java.io.IOException;

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
        /*if (token == null) {
            sendResponse(exchange, 400, "Invalid token.");
        }*/
        boolean isValid = database.verifyUser(login, password);

        if (isValid) {

            sendResponse(exchange, 200, "Authentication successful!");
            sendResponse(exchange, 200, getPaymentPage());
            /*try {
                sendResponse(exchange,200 , getLoadingPage());
                ParentClient client = new ParentClient(ACQ_PORT);
                client.init(ACQ_PORT);
                client.send(token);
                String ack = client.read();

                //à modif en fonction du statuscode de retour de l'ACQ
                if ("fail".equals(ack)) {
                    sendResponse(exchange, 200, getFailPage());
                } else if ("success".equals(ack)) {
                    sendResponse(exchange, 200, getSuccessPage());
                }

            } catch (IOException e) {
                System.err.println("Erreur lors de la communication avec le serveur ACQ : " + e.getMessage());
                sendResponse(exchange, 500, "Erreur interne lors de la communication avec ACQ.");
            }*/

        } else {
            sendResponse(exchange, 401, "Invalid credentials.");
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {

        sendResponse(exchange, 200, getAuthPage());

    }

}
