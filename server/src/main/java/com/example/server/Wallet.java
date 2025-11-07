package com.example.server;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class Wallet {
    public final PrivateKey privateKey;
    public final PublicKey publicKey;
    public Wallet() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            keyGen.initialize(new ECGenParameterSpec("prime192v1"), SecureRandom.getInstance("SHA1PRNG"));
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}