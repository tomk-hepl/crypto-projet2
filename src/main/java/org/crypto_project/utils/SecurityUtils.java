package org.crypto_project.utils;

import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;

public class SecurityUtils {
    public static String generateSalt() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16]; // 16 bytes for the salt
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public static String hashPassword(String password, String salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt.getBytes());
        byte[] hashBytes = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static String sign(PrivateKey privateKey, String data) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(data.getBytes());
        return Base64.getEncoder().encodeToString(privateSignature.sign());
    }

    public static boolean verifySignature(PublicKey publicKey, String signature, String data) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(data.getBytes());
        return publicSignature.verify(Base64.getDecoder().decode(signature));
    }

    public static PublicKey loadPublicKeyFromCertificate(KeyStore store, String alias) throws Exception {
        Certificate cert = store.getCertificate(alias);
        return cert.getPublicKey();
    }
    public static PrivateKey loadPrivateKey(KeyStore store, String alias, String password) throws Exception {
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) store.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
        return pkEntry.getPrivateKey();
    }

    public static KeyStore loadStore(String storePath, String storePassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(storePath)) {
            keystore.load(fis, storePassword.toCharArray());
        }
        return keystore;
    }
}
