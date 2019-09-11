package com.imanage.aws.wrapper;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.encryptionsdk.kms.*;
import com.amazonaws.regions.*;

import java.security.*;
import java.security.spec.*;

public class CryptoKeyFactory {

    /** Use AWS credentials to obtain a KMS Provider
     * We use this + our pubKey for encryption and decryption
     */
    public KmsMasterKeyProvider getMasterKeyProvider() {  // TODO: see dev.env
        AWSCredentialsProvider creds = new DefaultAWSCredentialsProviderChain();
        Region region = new Region(new InMemoryRegionImpl(System.getenv("AWS_REGION"), "domain"));
        ClientConfiguration clientConfig = new ClientConfiguration();
        String keyId = System.getenv("KMS_KEY_ID");
        return new KmsMasterKeyProvider(creds, region, clientConfig, keyId);
    }

    /** Generates a random public/private keypair. In production, we'd used stored pub/priv keys accessed from
     * either EC2 or Vault.
     */
    public KeyPair generateEscrowKeyPair() {
        try {
            final KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");
            kg.initialize(4096); // Escrow keys should be very strong
            return kg.generateKeyPair();
        } catch (GeneralSecurityException e) {
            System.out.println(e);
            System.exit(1);
        }
        return null;
    }
}

// https://docs.oracle.com/javase/tutorial/security/apisign/step2.html