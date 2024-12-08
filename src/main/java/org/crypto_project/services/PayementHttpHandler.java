package org.crypto_project.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.crypto_project.utils.ParentClient;

import java.io.IOException;

import static org.crypto_project.utils.HttpHandlerUtils.*;


public class PayementHttpHandler  implements HttpHandler
{

    private static final  int ACQ_PORT = 9043;
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

        String token = getQueryParam(exchange, "token");
        if (token == null) {
            sendResponse(exchange, 400, "Invalid token.");
        }

        try {
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
        }


    }
}
