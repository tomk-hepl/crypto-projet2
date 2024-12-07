package org.crypto_project.utils;

import java.io.IOException;

public class ParentServer {

    protected TCPServer server;
    protected final int port;
    protected final String ipAddress = "127.0.0.1";

    public ParentServer(int port) throws IOException {
        this.port = port;
        this.init();
    }

    public void init() throws IOException {
        // init server
         server = new TCPServer();
        server.start(this.port);
        System.out.println("Server started on port " + this.port);
    }

    public String read() throws IOException {
        System.out.println("---- SERVER LISTENING ---- ");
        server.listenToNewClient();
        return server.readMessage();
    }

    public void send(String message) throws IOException {
        System.out.println("sent message: " + message);
        server.sendMessage(message);
    }

    public void close() throws IOException {
        server.stop();
        System.out.println("Server stopped");
    }
}
