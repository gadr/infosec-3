package checker;


import org.apache.commons.io.FileUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

public class PrivateKeyChecker {

    public byte[] decryptPKCS5(byte[] encoded, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encoded);
    }

    public PrivateKey decryptPrivateKey(byte[] encodedPrivateKey, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56, random);
        byte[] pkcs8PrivateKey = decryptPKCS5(encodedPrivateKey, keyGen.generateKey());

        //System.out.println("PrivateKey decrypted" + new String(pkcs8PrivateKey));
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(pkcs8PrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    public byte[] sign(PrivateKey key, byte[] bytes) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature signature = Signature.getInstance("MD5WithRSA");
        //System.out.println("Signing " + new String(bytes));
        signature.initSign(key);
        signature.update(bytes);
        byte[] signatureBytes = signature.sign();
        System.out.println("Signature bytes: " + signatureBytes.length);
        return signatureBytes;
    }
}