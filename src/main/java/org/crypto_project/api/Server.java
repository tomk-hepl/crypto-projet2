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
import java.sql.SQLException;
import java.util.Properties;

import static org.crypto_project.api.ServerMethods.readRequestBody;
import static org.crypto_project.api.ServerMethods.sendResponse;

public class Server
{

    private final static  int port =  8043;
    public static void main(String[] args)
    {

        HttpsServer server;
        try
        {
            server = HttpsServer.create(new InetSocketAddress(port),0);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("config.properties")) {
                properties.load(fis);
            }
            char[] password = properties.getProperty("keystore.password").toCharArray();

            // Charger le keystore contenant le certificat auto-signé
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


            server.createContext("/api/auth",new AuthHandler());
            server.createContext("/api/payement",new PaymentHandler());
            System.out.println("Demarrage du serveur HTTPS...");
            server.start();
        }
        catch (IOException e)
        {
            System.out.println("Erreur: " + e.getMessage());
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    static class AuthHandler implements HttpHandler
    {
        @Override
        public void handle(HttpExchange exchange) throws IOException
        {
            String requestMethod = exchange.getRequestMethod();
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin","*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods","GET, POST");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers","Content-Type");
            if (requestMethod.equalsIgnoreCase("GET"))
            {
                System.out.println("--- Requête GET  ---");
                String response = null;
               /* try {
                    //response = ;
                } catch (SQLException | ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }*/
                sendResponse(exchange, 200, response);
            }
            else if (requestMethod.equalsIgnoreCase("POST"))
            {

                System.out.println("--- Requête POST reçue ---");


                String requestBody = readRequestBody(exchange);
                System.out.println("requestBody = " + requestBody);
              /*  try {
                    boolean maj = Update(requestBody);
                    if(maj)
                    {
                        System.out.println("");
                        sendResponse(exchange, 200, "");
                    }
                    else
                    {
                        System.out.println("");
                        sendResponse(exchange, 400, "");
                    }
                }
                catch (SQLException | ClassNotFoundException e)
                {
                    throw new RuntimeException(e);
                }*/

            }
            else sendResponse(exchange, 405, "Methode non autorisee !");
        }

    }


    static class PaymentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (requestMethod.equalsIgnoreCase("GET")) {
                System.out.println("--- Requête GET reçue (paiement) ---");
                String response = "<html><body><h1>Page de paiement</h1>" +
                        "<button>Payer</button></body></html>";
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 405, "Méthode non autorisée.");
            }
        }
    }

}
