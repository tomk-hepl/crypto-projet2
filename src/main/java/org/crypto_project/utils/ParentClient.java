package org.crypto_project.utils;

import javax.net.ssl.*;
import java.io.IOException;

public class ParentClient  {

    protected TCPClient client;
    protected final String ipAddress = "127.0.0.1";

    /*
    public ParentClient(int port) throws IOException {
        this.init(port, null);
    }
    */

    public ParentClient(int port, SSLContext sslContext) throws IOException {
        this.init(port, sslContext);
    }

    /*
    private void init(int port) throws IOException {
        client = new TCPClient();
        client.startConnection(ipAddress, port);
        System.out.println("Client started on port " + port);
    } */

    private void init(int port, SSLContext sslContext) throws IOException {
        client = new TCPClient();
        if (sslContext != null) {
            client.startConnection(ipAddress, port, sslContext);
            System.out.println("Secure ParentClient connected to port " + port);
        } else {
            client.startConnection(ipAddress, port, null);
            System.out.println("ParentClient connected to port " + port);
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
