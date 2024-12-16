package org.crypto_project.utils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port, SSLContext sslContext) throws IOException {
        if (sslContext != null) {
            SSLSocketFactory factory = sslContext.getSocketFactory();
            clientSocket = factory.createSocket(ip, port);
            System.out.println("SSL client connection etablie.");
        } else {
            clientSocket = new Socket(ip, port);
            System.out.println("TCP client connection etablie.");
        }

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String readMessage() throws IOException {
        return in.readLine();
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

}