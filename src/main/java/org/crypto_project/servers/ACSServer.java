package org.crypto_project.servers;

import org.crypto_project.utils.ParentServer;
import org.crypto_project.utils.SSLConfig;
import org.crypto_project.utils.SecurityUtils;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

public class ACSServer {

    private static final int APP_PORT = 10042;
    private static final int ACQ_PORT = 10043;
    private final Set<String> tokenStore = new HashSet<>();

    private final KeyStore keyStore;
    private final KeyStore trustStore;

    public ACSServer() throws Exception {
        this.trustStore = SecurityUtils.loadStore("acs_truststore.jks", "acq-cert");
        this.keyStore = SecurityUtils.loadStore("acs_keystore.jks", "acs-key");
    }

    public static void main(String[] args) throws Exception {
        SSLContext sslContext = SSLConfig.setupSSLContextFromProperties(
                "config.properties",
                "acs.keystore.path",
                "acs.truststore.path"
        );

        ACSServer acs = new ACSServer();

        Runnable handleApp = () -> {
            try {
                ParentServer serverToApp = new ParentServer(APP_PORT);
                serverToApp.init(sslContext);
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
                serverToAcq.init(sslContext);
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

        PublicKey publicKey = SecurityUtils.loadPublicKeyFromCertificate(this.trustStore, "client-cert");

        if (!SecurityUtils.verifySignature(publicKey, signature, cardId + date)) {
           serverToApp.send("INVALID SIGNATURE");
           return;
       }

        // Génération et signature du code d'authentification
        String token = UUID.randomUUID().toString();

        PrivateKey privateKey = SecurityUtils.loadPrivateKey(this.keyStore, "acs-keystore", "acs-key");

        String signedToken = SecurityUtils.sign(privateKey, token);
        tokenStore.add(token);

        if (message.startsWith("CLIENT")) {
            serverToApp.send("TOKEN;" + token + ";" + signedToken);
        } else {
            serverToApp.send("UNKNOWN COMMAND");
        }
    }

    private void handleServerMessage(ParentServer serverToAcq) throws Exception {
        String message = serverToAcq.read();
         Thread.sleep(2500);
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
        if (tokenStore.contains(token)) {
            serverToAcq.send("ACK");
        } else {
            serverToAcq.send("NACK");
        }
    }
}
