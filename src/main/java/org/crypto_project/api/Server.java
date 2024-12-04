package org.crypto_project.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.crypto_project.api.ServerMethods.*;

public class Server
{

    private final static  int port =  8043;
    private static final String DB_URL = "jdbc:sqlite:database.users";
    public static void main(String[] args)
    {

        HttpsServer server = null;
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Successful connection to the SQLite database.");

            server = HttpsServer.create(new InetSocketAddress(port),0);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                properties.load(fis);
            }
            char[] password = properties.getProperty("keystore.password").toCharArray();


            KeyStore ks = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream("keystore.jks")) {
                ks.load(fis, password);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext));


            server.createContext("/api/auth",new AuthHandler(connection));
            //server.createContext("/api/payement",new PaymentHandler());
            System.out.println("Starting the HTTPS server...");
            server.start();
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e.getMessage());
        } catch (UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException | KeyStoreException |
                 KeyManagementException | SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {

            Connection finalConnection = connection;
            HttpsServer finalServer = server;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (finalConnection != null) {
                    try {
                        finalConnection.close();
                        System.out.println("Connection to the database closed.");
                    } catch (SQLException e) {
                        System.err.println("Error closing the database : " + e.getMessage());
                    }
                }
                if (finalServer != null) {
                    finalServer.stop(0);
                    System.out.println("Server HTTPS stopped.");
                }
            }));
        }
    }

    static class AuthHandler implements HttpHandler
    {
        private final Connection connection;

        AuthHandler(Connection connection) {
            this.connection = connection;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException
        {

            String requestMethod = exchange.getRequestMethod();
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods","POST");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","Content-Type");
            if (requestMethod.equalsIgnoreCase("POST"))
            {
                System.out.println("--- Requête POST reçue (authentification) ---");
                String requestBody = readRequestBody(exchange);
                System.out.println("Données reçues : " + requestBody);

                String[] credentials = ServerMethods.parseCredentials(requestBody);
                if (credentials == null) {
                    sendResponse(exchange, 400, "Format des identifiants incorrect.");
                    return;
                }

                String login = credentials[0];
                String password = credentials[1];

                boolean isValid = ServerMethods.verifyUser(connection, login, password);

                if (isValid) {
                    sendResponse(exchange, 200, "Authentification réussie !");
                } else {
                    sendResponse(exchange, 401, "Identifiants invalides.");
                }
            }else sendResponse(exchange, 405, "Methode non autorisee !");
        }

    }


   /* static class PaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods","GET");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","Content-Type");

            if (requestMethod.equalsIgnoreCase("GET")) {
                System.out.println("--- Requête GET reçue (paiement) ---");
                String response = "<html><body><h1>Page de paiement</h1>" +
                        "<button>Payer</button></body></html>";
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "Méthode non autorisée.");
            }
        }
    }*/

}
