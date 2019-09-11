package com.imanage.aws.wrapper;

import java.util.*;

public class Application {

    public static void main(final String[] args) {
        byte[] data = "sampleDatatoEncrypt".getBytes();

        CryptoService cs = new CryptoService();

        byte[] ciphertext = cs.encryptBytes(data);
        byte[] plaintext = cs.decryptBytes(ciphertext);
        byte[] plaintext2 = cs.escrowDecryptBytes(ciphertext);

        System.out.println("InputData: " + Arrays.toString(data));
        System.out.println("Encrypted: " + Arrays.toString(ciphertext));
        System.out.println("Decrypted: " + Arrays.toString(plaintext));
        System.out.println("Decrypted w PrivateKey: " + Arrays.toString(plaintext2));
    }

}
