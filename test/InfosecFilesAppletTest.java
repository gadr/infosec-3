import checker.DigitalSignatureChecker;
import checker.PrivateKeyChecker;
import controllers.routes;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;

import javax.crypto.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;

public class InfosecFilesAppletTest extends BaseModelTest {

    PrivateKey privateKey;
    Key key;

    @Before
    public void getPrivateKey() throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        String username = "user";
        String privateKeyPath = "test/" + username + "priv";

        File encodedPrivateKeyFile = new File(privateKeyPath);
        byte[] encodedPrivateKey = FileUtils.readFileToByteArray(encodedPrivateKeyFile);

        PrivateKey privateKey = privateKeyChecker.decryptPrivateKey(encodedPrivateKey, password);
    }

    @Test
    public void getSeed() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        // Arrange
        String fileEnv = "test/index.env";
        byte[] seed;
        byte[] envelopeContent = FileUtils.readFileToByteArray(new File(fileEnv));

        // Test
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        seed = cipher.doFinal(envelopeContent);

        // Assert
        assertThat(seed.length).isGreaterThan(0);
    }

    @Test
    public void getKeyFromEnvelope() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
        // Arrange
        String fileEnv = "test/index.env";
        byte[] seed;
        byte[] envelopeContent = FileUtils.readFileToByteArray(new File(fileEnv));

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        seed = cipher.doFinal(envelopeContent);

        // Test
        SecureRandom random = new SecureRandom(seed);
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56, random);
        Key key = keyGen.generateKey();


        // Assert
        String fileEnc = "test/index.enc";
        byte[] encryptedContent = FileUtils.readFileToByteArray(new File(fileEnc));

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] content = cipher.doFinal(encryptedContent);

    }

}
