import checker.DigitalSignatureChecker;
import checker.EnvelopeChecker;
import checker.PrivateKeyChecker;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.*;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;

public class InfosecFilesAppletTest extends BaseModelTest {

    PrivateKey privateKey;
    PublicKey publicKey;

    @Before
    public void getPrivateKey() throws IOException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        String password = "segredo";
        String privateKeyPath = "test/userpriv";
        String publiceKeyPath = "test/userpub";

        File encodedPrivateKeyFile = new File(privateKeyPath);
        byte[] encodedPrivateKey = FileUtils.readFileToByteArray(encodedPrivateKeyFile);
        File encodedPublicKeyFile = new File(publiceKeyPath);
        byte[] encodedPublicKey = FileUtils.readFileToByteArray(encodedPublicKeyFile);

        privateKey = privateKeyChecker.decryptPrivateKey(encodedPrivateKey, password);
        publicKey = digitalSignatureChecker.readPublicKey(encodedPublicKey);
    }

    @Test
    public void getSeedFromEnvelope() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        // Arrange
        String fileEnv = "test/index.env";
        byte[] envelopeContent = FileUtils.readFileToByteArray(new File(fileEnv));

        // Test
        byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);

        // Assert
        System.out.println(">>>>>" + new String(seed, "UTF8"));
        assertThat(seed.length).isGreaterThan(0);
    }

    @Test
    public void getKeyFromSeed() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidKeySpecException {
        // Arrange
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        String fileEnv = "test/index.env";
        String fileEnc = "test/index.enc";
        byte[] envelopeContent = FileUtils.readFileToByteArray(new File(fileEnv));
        byte[] encryptedContent = FileUtils.readFileToByteArray(new File(fileEnc));
        byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);

        // Test
        Key key = EnvelopeChecker.getKeyFromSeed(seed);
        byte[] content = privateKeyChecker.decryptPKCS5(encryptedContent, key);

        // Assert
        System.out.println(">>>>>" + new String(content, "UTF8"));
        assertThat(content.length).isGreaterThan(0);
    }

    @Test
    public void verifySignatureFromEnvelope() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, SignatureException {
        // Arrange
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        String fileAsd = "test/index.asd";
        String fileEnv = "test/index.env";
        String fileEnc = "test/index.enc";
        byte[] signatureContent = FileUtils.readFileToByteArray(new File(fileAsd));
        byte[] envelopeContent = FileUtils.readFileToByteArray(new File(fileEnv));
        byte[] encryptedContent = FileUtils.readFileToByteArray(new File(fileEnc));
        byte[] seed = EnvelopeChecker.getSeedFromEnvelope(envelopeContent, privateKey);
        Key key = EnvelopeChecker.getKeyFromSeed(seed);
        byte[] content = privateKeyChecker.decryptPKCS5(encryptedContent, key);

        // Test
        boolean result = digitalSignatureChecker.verifySignature(publicKey, signatureContent, content);

        // Assert
        assertThat(result).isTrue();
    }

}
