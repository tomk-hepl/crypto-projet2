package org.crypto_project.servers;

import org.crypto_project.utils.ParentServer;
import org.crypto_project.utils.SecurityUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.util.*;

public class ACSServer {

    private static final int PORT = 10043; // Port défini pour l'ACQ
    private final Map<String, String> tokenStore = new HashMap<>();
    private final Set<String> tokenList = new HashSet<>();
    private KeyPair keyPair; // Clés pour la signature

    public ACSServer() throws Exception {
        keyPair = SecurityUtils.generateKeyPair(); // Génération des clés pour signature/validation
    }

    public static void main(String[] args) throws Exception {
        ParentServer server = new ParentServer(PORT);

        try {
            ACSServer acs = new ACSServer();
            while (true) {
                // En écoute de données
                String message = server.read();
                System.out.println("Received message: " + message);

                String response;
                if (message.startsWith("CLIENT")) {
                    response = acs.handleClientMessage(message);
                } else if (message.startsWith("SERVER")) {
                    response = acs.handleServerMessage(message);
                } else {
                    response = "UNKNOWN COMMAND";
                }

                for (String token : acs.tokenList) {
                    System.out.println(token);
                }

                server.send(response);
            }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
            server.close();
         }
    }

    private String handleClientMessage(String message) throws Exception {
        String[] values = message.split(";");
        if (values.length != 4) {
            return "INVALID MESSAGE FORMAT";
        }

        String cardId = values[1];
        String date = values[2];
        String signature = values[3];

//        if (!SecurityUtils.verifySignature(keyPair, signature, cardId + date)) {
//            return "INVALID SIGNATURE";
//        }

        // Génération et signature du code d'authentification
        String token = UUID.randomUUID().toString();
        tokenList.add(token);
//        tokenStore.put(cardId, token);
        String signedToken = SecurityUtils.sign(keyPair, token);

        return "TOKEN;" + signedToken;
    }

    private String handleServerMessage(String message) throws Exception {
        String[] values = message.split(";");
        if (values.length != 2) {
            return "INVALID MESSAGE FORMAT";
        }

        String token = values[1];

//        if (!SecurityUtils.verifySignature(keyPair , signature, token)) {
//            return "INVALID SIGNATURE";
//        }

        // Vérification du token
//        if (tokenStore.containsValue(token)) {
        if (tokenList.contains(token)) {
            return "ACK";
        } else {
            return "NACK";
        }
    }
}
