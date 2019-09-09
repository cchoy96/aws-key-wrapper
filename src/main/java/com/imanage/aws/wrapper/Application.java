package com.imanage.aws.wrapper;


import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;

import java.util.Collections;
import java.util.Map;

public class Application {
    private static String keyARN;
    private static String data;

    public static void main(final String[] args) {
        keyARN = args[0];
        data = args[1];

        final AwsCrypto crypto = new AwsCrypto();
        final KmsMasterKeyProvider prov = new KmsMasterKeyProvider(keyARN);

        // Encrypt the data
        final Map<String,String> context = Collections.singletonMap("Example", "String");
        final String ciphertext = crypto.encryptString(prov, data, context).getResult();
        System.out.println("Ciphertext: " + ciphertext);

        // Decrypt the data
        final CryptoResult<String, KmsMasterKey> decryptResult = crypto.decryptString(prov, ciphertext);
        if (!decryptResult.getMasterKeyIds().get(0).equals(keyARN)) {
            throw new IllegalStateException("Wrong key ID!");
        }
        for (final Map.Entry<String,String> e : context.entrySet()) {
            if (!e.getValue().equals(decryptResult.getEncryptionContext().get(e.getKey()))) {
                throw new IllegalStateException("Wrong encryption context!");
            }
        }
    })
}
