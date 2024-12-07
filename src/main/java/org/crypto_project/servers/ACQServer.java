package org.crypto_project.servers;

import org.crypto_project.utils.ParentClient;
import org.crypto_project.utils.ParentServer;

import java.io.IOException;

public class ACQServer {

    private static final int PORT = 9043; // Port défini pour l'ACQ
    private static final int ACS_PORT = 10043;

    public static void main(String[] args) throws IOException {
        ParentServer server = new ParentServer(PORT);
        ParentClient client = new ParentClient(ACS_PORT);

        try {
            // En écoute de données
            String message = server.read();
            client.init(ACS_PORT);
            client.send(message);
            String ack = client.read();
            server.send(ack);

         } catch (Exception e) {
             e.printStackTrace();
         } finally {
            server.close();
            client.close();
         }
    }

}
