package checker;


import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

public class PrivateKeyChecker {

    public byte[] decryptPKCS5(byte[] encoded, byte[] pass) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] password64Bits = Arrays.copyOf(pass, 8); // use only first 64 bits

        SecretKeySpec keySpec = new SecretKeySpec(password64Bits, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return cipher.doFinal(encoded);
    }

    public PrivateKey decryptPrivateKey(byte[] encodedPrivateKey, String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeySpecException {
        byte[] pkcs8PrivateKey = decryptPKCS5(encodedPrivateKey, password.getBytes());

        System.out.println("PrivateKey decrypted" + new String(pkcs8PrivateKey));
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(pkcs8PrivateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    public byte[] sign(PrivateKey key, byte[] bytes) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        Signature signature = Signature.getInstance("MD5WithRSA");
        System.out.println("Signing " + new String(bytes));
        signature.initSign(key);
        signature.update(bytes);
        byte[] signatureBytes = signature.sign();
        System.out.println("Signature bytes: " + signatureBytes.length);
        return signatureBytes;
    }
}