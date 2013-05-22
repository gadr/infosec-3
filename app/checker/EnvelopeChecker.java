package checker;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static javax.crypto.KeyGenerator.getInstance;

public class EnvelopeChecker {

    public static byte[] getSeedFromEnvelope(byte[] envelopeContent, Key privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(envelopeContent);
    }

    public static Key getKeyFromSeed(byte[] seed) throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(seed);
        KeyGenerator keyGen = getInstance("DES");
        keyGen.init(56, random);
        return keyGen.generateKey();
    }
}
