package org.crypto_project.utils;
import java.io.IOException;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class Utilities {

    // ------------------------- INIT / STOP SERVERS ------------------------- //

    public static TCPClient clientInit(String ip, int port) throws IOException {
        TCPClient client = new TCPClient();
        client.startConnection(ip,port);
        System.out.println("Client connected to the port: " + port);
        return client;
    }

    public static TCPServer serverInit(int port) throws IOException {
        TCPServer server = new TCPServer();
        server.start(port);
        System.out.println("The server is waiting for new client to port " + port + "...");
        return server;
    }

    // ------------------------- GLOBAL SERVERS METHODS  ------------------------- //

    public static String readData(TCPServer server) throws Exception {

        String message = server.readMessage();
        if(message != null) {
            System.out.println("received message : "+ message);
        }
        return message;
    }

    public static void sendDataResponse(TCPClient client, String data) throws Exception {

            // debug
            System.out.println("data received : "+ data);

            // Send message
            client.sendMessage(data);
            System.out.println("Data sent : " + data);
    }

}