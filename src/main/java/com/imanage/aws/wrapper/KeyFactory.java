package com.imanage.aws.wrapper;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.encryptionsdk.kms.*;
import com.amazonaws.regions.*;

import java.security.*;

public class KeyFactory {

    /** Generates a random public/private keypair. In production, we'd used stored pub/priv keys.
     * DSA = Digital Signature Algorithm
     * SUN = Java's built-in provider
     * SHA1PRNG = algorithm from SUN for a cryptographically strong random number generator
     */
    public KeyPair generateRandomKeyPair() {
        KeyPair pair = null;
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keygen.initialize(1024, random);
            pair = keygen.generateKeyPair();
        } catch (Exception e) {
            System.out.println("EXCEPTION CAUGHT: " + e);
            System.exit(1);
        }
        return pair;
    }

    public KmsMasterKeyProvider getMasterKeyProvider() {
        // Currently, this is setup to look for AWS_ACCESS_ID_KEY and AWS_SECRET_ACCESS_KEY from the env.
        // In production, we might aim to have these env variables be set through VAULT or Amazon EC2
        // instance profiles.
        AWSCredentialsProvider creds = new DefaultAWSCredentialsProviderChain();
        Region region = new Region(new InMemoryRegionImpl("us-east-2", "domain"));
        ClientConfiguration clientConfig = new ClientConfiguration();
        String keyId = "Example";
        return new KmsMasterKeyProvider(creds, region, clientConfig, keyId);
    }
}

// https://docs.oracle.com/javase/tutorial/security/apisign/step2.html