package applet;

import checker.DigitalSignatureChecker;
import checker.EnvelopeChecker;
import checker.PrivateKeyChecker;
import org.apache.commons.io.FileUtils;

import javax.crypto.*;
import java.applet.Applet;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class InfosecApplet extends Applet {
    public static byte[] sign(String password, byte[] privateKeyBytes, byte[] randomBytes) {
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        System.out.println("Password: " + password);
        System.out.println("File: " + new String(privateKeyBytes));
        System.out.println("Random: " + new String(randomBytes));
        PrivateKey privateKey;
        String signature = "";
        try {
            privateKey = privateKeyChecker.decryptPrivateKey(privateKeyBytes, password);
            System.out.println("PrivateKey: " + privateKey.toString());
            return privateKeyChecker.sign(privateKey, randomBytes);
        } catch (Exception e) {
            System.out.println("Error!");
            e.printStackTrace();
        }
        return new byte[]{0};
    }

    public static byte[] decryptPrivateKey(byte[] PKCS5EncodedPrivateKey, String password)
            throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {

        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56, random);

        return privateKeyChecker.decryptPKCS5(PKCS5EncodedPrivateKey, keyGen.generateKey());
    }

    public static String getIndex(byte[] privateKeyContent, byte[] publicKeyContent,
                                  byte[] envelopeContent, byte[] signatureContent, byte[] encryptedContent)
            throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException,
            IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, SignatureException {

        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();

        PublicKey publicKey = digitalSignatureChecker.readPublicKey(publicKeyContent);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyContent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);
        Key key = EnvelopeChecker.getKeyFromSeed(seed);
        byte[] content = privateKeyChecker.decryptPKCS5(encryptedContent, key);

        boolean result = digitalSignatureChecker.verifySignature(publicKey, signatureContent, content);

        return result? "true" : "false";
    }
}
