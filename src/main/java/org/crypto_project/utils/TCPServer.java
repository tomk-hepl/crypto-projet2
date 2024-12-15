package org.crypto_project.utils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public TCPServer() {}
    public TCPServer(ServerSocket serverSocket) {
        if(serverSocket != null) this.serverSocket = serverSocket;
    }

    public void start(int port, SSLContext sslContext) throws IOException {
        if (sslContext != null) {
            SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
            serverSocket = factory.createServerSocket(port);
            System.out.println("Secure SSL server started on port " + port);
        } else {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP server started on port " + port);
        }
    }

    public void listenToNewClient() throws IOException {
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String readMessage() throws IOException {
        boolean clientDisconnected = clientSocket == null || clientSocket.isClosed();
        String message;

        // waiting for new client
        if (clientDisconnected || (message = in.readLine()) == null) {
            throw new IOException("Client disconnected");
        }

        return message;
    }

    public void sendMessage(String message) {
        // if there is no client
        if (clientSocket == null || clientSocket.isClosed()) {
            throw new IllegalStateException("Client not connected");
        }
        out.println(message);
    }

    public void closeClient() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public void stop() throws IOException {
        closeClient();
        serverSocket.close();
    }
}