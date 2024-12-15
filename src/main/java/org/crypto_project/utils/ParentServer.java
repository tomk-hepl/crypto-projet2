package org.crypto_project.utils;

import javax.net.ssl.*;
import java.io.IOException;

public class ParentServer {

    protected TCPServer server;
    protected final int port;
    protected final String ipAddress = "127.0.0.1";

    public ParentServer(int port) throws IOException {
        this.port = port;
    }

    public void init(SSLContext sslContext) throws IOException {
        if (sslContext != null) {
            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) factory.createServerSocket(this.port);
            this.server = new TCPServer(sslServerSocket);
            System.out.println("Secure ParentServer started on port " + this.port);
        } else {
            this.server = new TCPServer();
            this.server.start(this.port, null);
            System.out.println("ParentServer started on port " + this.port);
        }
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
