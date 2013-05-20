import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import checker.DigitalSignatureChecker;
import checker.PrivateKeyChecker;
import controllers.routes;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import play.mvc.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class DigitalSignatureTest extends BaseModelTest {

    @Before
    public void createKeys() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException {
        // Path to the private key
        String privateKeyPath = "test/gadr.priv";
        String publicKeyPath = "test/gadr.pub";
        String password = "superfrasemuitogrande";
        byte[] password64Bits = Arrays.copyOf(password.getBytes(), 8); // use only first 64 bits
        assertThat(password64Bits.length == 8);

        // Generate key pair
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
    }

    @Test
    public void checkDigitalSignature() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, SignatureException {
        // The Private Key Chair used by our applet
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
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

        File encodedPrivateKeyFile = new File(privateKeyPath);
        byte[] encodedPrivateKey = FileUtils.readFileToByteArray(encodedPrivateKeyFile);

        // Use the private key checker to decrypt the pkcs5 key into a encoded pkcs8 key
        PrivateKey decryptedKey = privateKeyChecker.decryptPrivateKey(encodedPrivateKey, password);

        // Sign the random bytes with the key
        byte[] signatureBytes = privateKeyChecker.sign(decryptedKey, randomBytes);
        assertThat(signatureBytes.length > 0);

        // Get the public key from File System
        PublicKey publicKey = digitalSignatureChecker.readPublicKey(FileUtils.readFileToByteArray(new File(publicKeyPath)));

        // Check the signature with the public key
        boolean isVerified = digitalSignatureChecker.verifySignature(publicKey, signatureBytes, randomBytes);
        assertThat(isVerified);
    }

    @Test
    public void checkByteToStringConversion() {
        byte[] b = new byte[512];
        new Random().nextBytes(b);
        String s = new String(b);
        byte[] convertedBytes = s.getBytes();
        assertThat(Arrays.equals(b, convertedBytes));
    }
}
