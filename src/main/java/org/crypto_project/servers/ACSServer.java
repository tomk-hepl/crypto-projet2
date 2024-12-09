package org.crypto_project.servers;

import org.crypto_project.utils.ParentServer;
import org.crypto_project.utils.SecurityUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.*;

public class ACSServer {

    private static final int APP_PORT = 10042;
    private static final int ACQ_PORT = 10043;
    private final Map<String, String> tokenStore = new HashMap<>();

    //private final Set<String> tokenList = new HashSet<>();
    private KeyPair keyPair; // Clés pour la signature

    public ACSServer() throws Exception {
        keyPair = SecurityUtils.generateKeyPair(); // Génération des clés pour signature/validation
    }

    public static void main(String[] args) throws Exception {
        ACSServer acs = new ACSServer();

        Runnable handleApp = () -> {
            try {
                ParentServer serverToApp = new ParentServer(APP_PORT);
                try {
                    while (true) {
                        acs.handleClientMessage(serverToApp);
                    }
                } finally {
                    serverToApp.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Runnable handleAcq = () -> {
            try {
                ParentServer serverToAcq = new ParentServer(ACQ_PORT);
                try {
                    while (true) {
                        acs.handleServerMessage(serverToAcq);
                    }
                } finally {
                    serverToAcq.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread1 = new Thread(handleApp);
        Thread thread2 = new Thread(handleAcq);
        // Start both threads
        thread1.start();
        thread2.start();
    }

    private void handleClientMessage(ParentServer serverToApp) throws Exception {
        String message = serverToApp.read();
        String[] values = message.split(";");
        if (values.length != 4) {
            serverToApp.send("INVALID MESSAGE FORMAT");
            return;
        }

        String cardId = values[1];
        String date = values[2];
        String signature = values[3];

//        if (!SecurityUtils.verifySignature(keyPair, signature, cardId + date)) {
//            return "INVALID SIGNATURE";
//        }

        // Génération et signature du code d'authentification
        String token = UUID.randomUUID().toString();

        String signedToken = SecurityUtils.sign(keyPair, token);
        tokenStore.put(signedToken, token);

        if (message.startsWith("CLIENT")) {
            serverToApp.send("TOKEN;" + signedToken);
        } else {
            serverToApp.send("UNKNOWN COMMAND");
        }
    }

    private void handleServerMessage(ParentServer serverToAcq) throws Exception {
        String message = serverToAcq.read();

        String[] values = message.split(";");
        if (values.length != 2) {
            serverToAcq.send("INVALID MESSAGE FORMAT");
            return;
        }


        String token = values[1];
        System.out.println("token : " + token);


//        if (!SecurityUtils.verifySignature(keyPair , signature, token)) {
//            return "INVALID SIGNATURE";
//        }

        // Vérification du token
        if (tokenStore.containsKey(token)) {
            serverToAcq.send("ACK");
        } else {
            serverToAcq.send("NACK");
        }
    }
}
