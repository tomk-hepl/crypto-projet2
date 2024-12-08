package org.crypto_project.servers;

import com.sun.net.httpserver.HttpsConfigurator;
import org.crypto_project.databases.SQLLiteDatabase;
import org.crypto_project.services.AuthHttpHandler;
import org.crypto_project.utils.SSLConfig;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpsServer
{

    private static final int port =  8043;
    private static final String DB_URL = "jdbc:sqlite:database.users";
    private final SQLLiteDatabase database;
    com.sun.net.httpserver.HttpsServer server = null;

    public HttpsServer(String url) {
        database = new SQLLiteDatabase(url);
    }

    public void init() throws Exception {
        connectToDatabase();
        createServer();
        setupSSLContext();
        registerContexts();
        startServer();
    }

    private void connectToDatabase() throws Exception {
        database.connect();
        System.out.println("Database connected.");
    }

    private void createServer() throws Exception {
        server = com.sun.net.httpserver.HttpsServer.create(new InetSocketAddress(port), 0);
        System.out.println("HTTPS server created.");
    }

    private void setupSSLContext() throws Exception {
        SSLContext sslContext = SSLConfig.setupSSLContext("config.properties", "keystore.jks");
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext));
        System.out.println("SSL context configured.");
    }

    private void registerContexts() {
        server.createContext("/api/auth", new AuthHttpHandler(database));
        // server.createContext("/api/payment", new PaymentHandler());
        System.out.println("Server contexts registered.");
    }

    private void startServer() {
        System.out.println("Starting the HTTPS server...");
        server.start();
        System.out.println("HTTPS server started.");
    }

    public void close() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            database.close();
            if (server != null) {
                server.stop(0);
                System.out.println("Server HTTPS stopped.");
            }
        }));
    }

    public static void main(String[] args) {
        HttpsServer server = new HttpsServer(DB_URL);
        //ParentClient client = new ParentClient(ACQ_PORT);
        try {
            server.init();
            //client.init(ACQ_PORT);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.close();

        }
    }
}
