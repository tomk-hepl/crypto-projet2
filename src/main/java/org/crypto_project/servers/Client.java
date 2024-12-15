package org.crypto_project.servers;

import org.crypto_project.utils.ParentClient;
import org.crypto_project.utils.ParentServer;
import org.crypto_project.utils.SecurityUtils;
import java.security.KeyPair;

public class Client {
    private static final int ACS_PORT = 10042;
    private static final String DATE = "04/2026";
    private static final String CARD_ID = "5425233430109903";
    private final KeyPair keyPair;

    public Client() throws Exception {
        this.keyPair = SecurityUtils.generateKeyPair(); // Generate key pair for signing messages
    }

    public static void main(String[] args) throws Exception {
        Client app = new Client();
        ParentClient client = new ParentClient(ACS_PORT, null);

        try {
            // Create the message to send to the ACS
            String message = "CLIENT;" + CARD_ID + ";" + DATE + ";" + SecurityUtils.sign(app.keyPair, CARD_ID + ";" + DATE);
            client.send(message);

            // Read and process the response
            String response = client.read();
            if (response.startsWith("TOKEN")) {
                System.out.println("Received token: " + response.split(";")[1]);
            } else {
                System.err.println("Failed to receive a valid token: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}
