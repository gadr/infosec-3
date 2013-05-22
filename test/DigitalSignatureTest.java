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
        String username = "gadr";
        String privateKeyPath = "test/" + username + ".priv";
        String publicKeyPath = "test/" + username + ".pub";
        String password = "superfrasemuitogrande";

        utils.KeyPairGenerator.generateKeyPair(username, password);

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
    public void checkExampleDigitalSignature() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, SignatureException {
        // The Private Key Chair used by our applet
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        // Path to the private key
        String privateKeyPath = "test/userpriv";
        String publicKeyPath = "test/userpub";
        String password = "segredo";

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
