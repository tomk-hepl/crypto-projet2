package org.crypto_project.servers;

import org.crypto_project.utils.ParentClient;
import org.crypto_project.utils.SSLConfig;
import org.crypto_project.utils.SecurityUtils;
import javax.net.ssl.SSLContext;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Client {
    private static final int ACS_PORT = 10042;
    private static final String DATE = "04/2026";
    private static final String CARD_ID = "5425233430109903";
    private final KeyStore keyStore;
    private final KeyStore trustStore;

    public Client() throws Exception {
        this.keyStore = SecurityUtils.loadStore("client_keystore.jks", "client-key");
        this.trustStore = SecurityUtils.loadStore("client_truststore.jks", "acq-client");
    }

    public static void main(String[] args) throws Exception {

        SSLContext sslContext = SSLConfig.setupSSLContextFromProperties(
                "config.properties",
                "client.keystore.path",
                "client.truststore.path"
        );

        Client app = new Client();
        ParentClient client = new ParentClient(ACS_PORT);

        try {

            client.init(sslContext);

            PublicKey publicKey = SecurityUtils.loadPublicKeyFromCertificate(app.trustStore, "acs-cert");
            PrivateKey privateKey = SecurityUtils.loadPrivateKey(app.keyStore, "client-keystore", "client-key");

            // Create the message to send to the ACS
            String message = "CLIENT;" + CARD_ID + ";" + DATE + ";" + SecurityUtils.sign(privateKey, CARD_ID + DATE);
            client.send(message);

            // Read and process the response
            String response = client.read();
            if (response.startsWith("TOKEN")) {
                String[] values = response.split(";");
                String token = values[1];
                String signedToken = values[2];
                if (!SecurityUtils.verifySignature(publicKey, signedToken, token)) {
                    System.out.println("INVALID SIGNATURE");
                } else {
                    System.out.println("Received token: " + token);
                }
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