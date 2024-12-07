package org.crypto_project.utils;

import java.io.IOException;

public class ParentServer {

    protected TCPServer server;
    protected TCPClient client;
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
        // Instanciate client
        client = new TCPClient();
    }

    protected void initClient(int port) throws IOException {
        client.startConnection(ipAddress, port);
        System.out.println("Client started on port " + this.port);
    }

    public String clientRead() throws IOException {
        System.out.println("---- CLIENT LISTENING ---- ");
        return client.readMessage();
    }

    public String serverRead() throws IOException {
        System.out.println("---- SERVER LISTENING ---- ");
        server.listenToNewClient();
        return server.readMessage();
    }

    public void clientSend(String message) throws IOException {
        client.sendMessage(message);
    }
    public void serverSend(String message) throws IOException {
        server.sendMessage(message);
    }

    public void close() throws IOException {
        server.stop();
        System.out.println("Server stopped");
    }
}
