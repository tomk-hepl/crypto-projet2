package org.crypto_project.utils;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.ConnectException;

public class ParentClient  {

    protected TCPClient client;
    protected final int port;
    protected final String ipAddress = "127.0.0.1";

    public ParentClient(int port) throws IOException {
        this.port = port;
    }

    public void init(SSLContext sslContext) throws IOException {

        try {

            client = new TCPClient();
            if (sslContext != null) {
                client.startConnection(ipAddress, this.port, sslContext);
                System.out.println("Secure ParentClient connected to port " + port);
            } else {
                client.startConnection(ipAddress, this.port, null);
                System.out.println("ParentClient connected to port " + port);
            }
        } catch (ConnectException e) {
            throw new RuntimeException("Serveur non activé : " + e.getMessage());
        }
    }

    public String read() throws IOException {
        System.out.println("---- CLIENT LISTENING ---- ");
        return client.readMessage();
    }

    public void send(String message) {
        System.out.println("sent message: " + message);
        client.sendMessage(message);
    }

    public void close() throws IOException {
        client.stopConnection();
        System.out.println("Client stopped");
    }
}
