import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import applet.PrivateKeyChecker;
import controllers.routes;
import models.User;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }
    
    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }

    @Test
    public void checkDigitalSignature() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException, SignatureException {
        PrivateKeyChecker privateKeyChecker = new PrivateKeyChecker();
        String path = "test/gadr.priv";
        String password = "lala";
        Result generateRandomBytes = callAction(routes.ref.Application.generateRandom512Bytes());
        byte[] randomBytes = contentAsString(generateRandomBytes).getBytes();
        assertThat(randomBytes.length > 0);

        System.out.println("Start generating Private key");
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        // extract the encoded private key, this is an unencrypted PKCS#8 private key
        byte[] encodedprivkey = keyPair.getPrivate().getEncoded();


        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "DES");
        System.out.println("Finish generating DES key");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        System.out.println("Start encryption");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] pkcs5EncryptedKey = cipher.doFinal(encodedprivkey);

        FileUtils.writeByteArrayToFile(new File(path), pkcs5EncryptedKey);

        PrivateKey decryptedKey = privateKeyChecker.decryptPrivateKey(path, password);

        byte[] signatureBytes = privateKeyChecker.sign(decryptedKey, randomBytes);

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(keyPair.getPublic());
        signature.update(signatureBytes);
        assertThat(signature.verify(signatureBytes));
    }
   
}
