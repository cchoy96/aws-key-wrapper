package com.imanage.aws.wrapper;

import com.amazonaws.encryptionsdk.*;
import com.amazonaws.encryptionsdk.jce.*;
import com.amazonaws.encryptionsdk.kms.*;
import com.amazonaws.encryptionsdk.multi.*;

import java.security.*;

public class CryptoService {
    private static PublicKey publicEscrowKey;
    private static PrivateKey privateEscrowKey;
    private final CryptoKeyFactory keyFactory;

    public CryptoService() {
        keyFactory = new CryptoKeyFactory();
        KeyPair pair = keyFactory.generateEscrowKeyPair();
        publicEscrowKey = pair.getPublic();
        privateEscrowKey = pair.getPrivate();
    }

    /** Use our Public Key to encrypt our data
     *
     * @param plaintext - unencrypted data
     * @return ciphertext, encrypted data
     */
    public byte[] encryptBytes(final byte[] plaintext) {
        final AwsCrypto crypto = new AwsCrypto();
        final KmsMasterKeyProvider kms = keyFactory.getMasterKeyProvider();

        // Generate key provider factory using public key
        final JceMasterKey escrowPub = JceMasterKey.getInstance(publicEscrowKey, null,
                "Escrow", "Escrow", "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        final MasterKeyProvider<?> provider = MultipleProviderFactory.buildMultiProvider(kms, escrowPub);

        // Encrypt the data
        return crypto.encryptData(provider, plaintext).getResult();
    }

    /** Uses the MasterKeyProvider to decrypt the ciphertext
     *
     * @param ciphertext - encrypted data
     * @return plaintext, decrypted data
     */
    public byte[] decryptBytes(final byte[] ciphertext) {
        final AwsCrypto crypto = new AwsCrypto();
        final KmsMasterKeyProvider kms = keyFactory.getMasterKeyProvider();

        // Generate key provider factory using public key
        final JceMasterKey escrowPub = JceMasterKey.getInstance(publicEscrowKey, null, "Escrow", "Escrow",
                "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        final MasterKeyProvider<?> provider = MultipleProviderFactory.buildMultiProvider(kms, escrowPub);

        // Decrypt the data
        return crypto.decryptData(provider, ciphertext).getResult();
    }

    /** Uses our PrivateKey to decrypt the ciphertext.
     *
     * @param ciphertext - encrypted data
     * @return plaintext, decrypted data
     */
    public byte[] escrowDecryptBytes(final byte[] ciphertext) {
        final AwsCrypto crypto = new AwsCrypto();
        final JceMasterKey escrowPriv = JceMasterKey.getInstance(publicEscrowKey, privateEscrowKey,"Escrow", "Escrow",
                "RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        return crypto.decryptData(escrowPriv, ciphertext).getResult();
    }
}

// https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/java-example-code.html
