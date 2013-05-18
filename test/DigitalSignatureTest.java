import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import applet.PrivateKeyChecker;
import controllers.routes;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import play.mvc.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class DigitalSignatureTest {

    @Test
    public void checkDigitalSignature() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, SignatureException {
        // The Private Key Chair used by our applet
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        // Path to the private key
        String privateKeyPath = "test/gadr.priv";
        String publicKeyPath = "test/gadr.pub";
        String password = "superfrasemuitogrande";
        byte[] password64Bits = Arrays.copyOf(password.getBytes(), 8); // use only first 64 bits
        assertThat(password64Bits.length == 8);

        // Generate random bytes. Will be used for the Digital Signature
        Result generateRandomBytes = callAction(routes.ref.Application.generateRandom512Bytes());
        byte[] randomBytes = contentAsString(generateRandomBytes).getBytes();
        assertThat(randomBytes.length == 512);

        // Generate key pair
        System.out.println("Start generating key pair");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
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

        assertThat(new File(privateKeyPath).exists());
        assertThat(new File(publicKeyPath).exists());

        // Use the private key checker to decrypt the pkcs5 key into a encoded pkcs8 key
        PrivateKey decryptedKey = privateKeyChecker.decryptPrivateKey(privateKeyPath, password);
        assertThat(Arrays.equals(decryptedKey.getEncoded(), encodedPrivateKey));

        // Sign the random bytes with the key
        byte[] signatureBytes = privateKeyChecker.sign(decryptedKey, randomBytes);
        assertThat(signatureBytes.length > 0);

        // Get the public key from File System
        byte[] publicKeyBytes = FileUtils.readFileToByteArray(new File(publicKeyPath));
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // Check the signature with the public key
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(signatureBytes);
        assertThat(signature.verify(signatureBytes));
    }
   
}
