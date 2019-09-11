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
    public KmsMasterKeyProvider getMasterKeyProvider() {
        // Currently, this is setup to look for AWS_ACCESS_ID_KEY and AWS_SECRET_ACCESS_KEY from the env.
        // In production, we might aim to have these env variables be set through VAULT or Amazon EC2
        // instance profiles. The key has also been hard-coded in here which won't be the case.
        AWSCredentialsProvider creds = new DefaultAWSCredentialsProviderChain(); // IAM user creds
        String keyId = "f74681da-d1d7-4c06-a412-4da7d3b79e9d";  // obtained through KMS
        Region region = new Region(new InMemoryRegionImpl("us-east-2", "domain")); // = Ohio
        ClientConfiguration clientConfig = new ClientConfiguration();
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

    /** Should have have our keys in byte streams already, we can decode them to a KeyPair here */
    public KeyPair decodeKeys(byte[] privKeyBits,byte[] pubKeyBits) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
        PrivateKey privKey=keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privKeyBits));
        PublicKey pubKey=keyFactory.generatePublic(new X509EncodedKeySpec(pubKeyBits));
        return new KeyPair(pubKey,privKey);
    }
}

// https://docs.oracle.com/javase/tutorial/security/apisign/step2.html