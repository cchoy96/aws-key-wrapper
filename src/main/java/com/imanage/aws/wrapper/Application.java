package com.imanage.aws.wrapper;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.MasterKeyProvider;
import com.amazonaws.encryptionsdk.jce.JceMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.amazonaws.encryptionsdk.multi.MultipleProviderFactory;
import com.amazonaws.regions.InMemoryRegionImpl;
import com.amazonaws.regions.Region;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

// https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/java-example-code.html

public class Application {
    private static PublicKey publicEscrowKey; // todo set these
    private static PrivateKey privateEscrowKey;

    public static void main(final String[] args) {
        String data = "sampleDatatoEncrypt";

        Application app = new Application();
        app.decryptBytes(app.encryptBytes(data.getBytes()));
        app.escrowDecryptBytes(app.encryptBytes(data.getBytes()));
    }

    private KmsMasterKeyProvider getMasterKeyProvider() {
        AWSCredentialsProvider creds = new DefaultAWSCredentialsProviderChain(); // todo set AWS_ACCESS_KEY & AWS_SECRET_KEY
        Region region = new Region(new InMemoryRegionImpl("us-east-1", "domain"));
        ClientConfiguration clientConfig = new ClientConfiguration();
        String keyId = "Example";
        return new KmsMasterKeyProvider(creds, region, clientConfig, keyId);
    }

    public byte[] encryptBytes(final byte[] plaintext) {
        final AwsCrypto crypto = new AwsCrypto();
        final KmsMasterKeyProvider kms = getMasterKeyProvider();

        // Generate key provider factory using public key
        final JceMasterKey escrowPub = JceMasterKey.getInstance(publicEscrowKey, null, "Escrow", "Escrow",
                "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        final MasterKeyProvider<?> provider = MultipleProviderFactory.buildMultiProvider(kms, escrowPub);

        // Encrypt the data
        byte[] ciphertext = crypto.encryptData(provider, plaintext).getResult();

        System.out.println("Ciphertext: " + Arrays.toString(ciphertext));
        return ciphertext;
    }

    public byte[] decryptBytes(final byte[] ciphertext) {
        final AwsCrypto crypto = new AwsCrypto();
        final KmsMasterKeyProvider kms = getMasterKeyProvider();

        // Generate key provider factory using public key
        final JceMasterKey escrowPub = JceMasterKey.getInstance(publicEscrowKey, null, "Escrow", "Escrow",
                "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        final MasterKeyProvider<?> provider = MultipleProviderFactory.buildMultiProvider(kms, escrowPub);

        // Decrypt the data
        byte[] plaintext = crypto.decryptData(provider, ciphertext).getResult();

        System.out.println("Decrypted: " + Arrays.toString(plaintext));
        return plaintext;
    }

    public byte[] escrowDecryptBytes(final byte[] ciphertext) {
        final AwsCrypto crypto = new AwsCrypto();
        final JceMasterKey escrowPriv = JceMasterKey.getInstance(publicEscrowKey, privateEscrowKey,"Escrow", "Escrow",
                "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        byte[] plaintext = crypto.decryptData(escrowPriv, ciphertext).getResult();
        System.out.println("Decrypted: " + Arrays.toString(plaintext));
        return plaintext;
    }
}
