package org.crypto_project.utils;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.Properties;

public class SSLConfig {
    public static SSLContext setupSSLContext(String propertiesFile, String keystoreFile) throws Exception {
        Properties properties = loadProperties(propertiesFile);
        try (FileInputStream fis = new FileInputStream(propertiesFile)) {
            properties.load(fis);
        }
        char[] password = properties.getProperty("keystore.password").toCharArray();

        KeyStore ks = loadKeyStore(keystoreFile, password);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return sslContext;
    }

    private static Properties loadProperties(String filePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
        return properties;
    }

    private static KeyStore loadKeyStore(String filePath, char[] password) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(filePath)) {
            ks.load(fis, password);
        }
        return ks;
    }
}
