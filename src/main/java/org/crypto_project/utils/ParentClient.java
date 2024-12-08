package org.crypto_project.utils;

import java.io.IOException;

public class ParentClient {

    protected TCPClient client;
    protected final String ipAddress = "127.0.0.1";

    public ParentClient(int port) throws IOException {
        this.init(port);
    }

    public void init(int port) throws IOException {
        client = new TCPClient();
        client.startConnection(ipAddress, port);
        System.out.println("Client started on port " + port);
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
