package org.crypto_project.servers;

import org.crypto_project.utils.ParentClient;
import org.crypto_project.utils.ParentServer;

import java.io.IOException;

public class ACQServer {

    private static final int PORT = 9043; // Port défini pour l'ACQ
    private static final int ACS_PORT = 10043;

    public static void main(String[] args) throws IOException {
        ParentServer server = new ParentServer(PORT);
        System.out.println("Server ACQ started on port " + PORT);
        ParentClient client = new ParentClient(ACS_PORT);

        try {
            // En écoute de données
            String message = server.read();
            System.out.println("Received message Server ACQ: " + message);
            //client.init(ACS_PORT);
            client.send("TOKEN;" + message);
            String ack = client.read();
            System.out.println("Received ack from ACS: " + ack);
            server.send(ack);

         } catch (Exception e) {
             e.printStackTrace();
         } finally {
            server.close();
            client.close();
         }
    }

}
