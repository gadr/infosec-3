package utils;

import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class KeyPairGenerator {

    public static void generateKeyPair(String username, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        String privateKeyPath = "test/" + username + ".priv";
        String publicKeyPath = "test/" + username + ".pub";

        byte[] password64Bits = Arrays.copyOf(password.getBytes(), 8); // use only first 64 bits

        // Generate key pair
        java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        // Extract the encoded private key, this is an unencrypted PKCS#8 private key
        byte[] encodedPrivateKey = keyPair.getPrivate().getEncoded();
        // Extract the encoded public key
        byte[] encodedPublicKey = keyPair.getPublic().getEncoded();

        // Encodes private key with PKCS5 using the password
        SecretKeySpec keySpec = new SecretKeySpec(password64Bits, "DES");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] pkcs5EncryptedKey = cipher.doFinal(encodedPrivateKey);

        // Write both keys to the file system
        FileUtils.writeByteArrayToFile(new File(privateKeyPath), pkcs5EncryptedKey);
        FileUtils.writeByteArrayToFile(new File(publicKeyPath), encodedPublicKey);
    }
}
