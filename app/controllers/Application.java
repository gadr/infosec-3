package controllers;

import checker.DigitalSignatureChecker;
import models.User;
import org.apache.commons.io.FileUtils;
import play.*;
import play.mvc.*;

import views.html.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Application extends Controller {
  
    public static Result index() {
        User user = User.findByUsername(session("connected"));
        boolean passwordChecked = "OK".equals(session("password"));
        boolean signatureChecked = "OK".equals(session("signature"));
        if(user != null && passwordChecked && signatureChecked) {
            return ok(menu.render(user));
        } else {
            return redirect("/login");
        }
    }

    public static Result login() {
        return ok(login.render());
    }

    public static Result logout() {
        session().clear();
        return redirect("/");
    }

    public static Result checkLogin() {
        String username = request().body().asFormUrlEncoded().get("username")[0];
        User result = User.findByUsername(username);
        if (result != null) {
            /*
            if (result.isBlocked()) {
                return forbidden("BLOCKED");
            }
            */
            result.addAccessNumber();
            result.save();
            session("connected", result.getUsername());
            return ok();
        }
        else {
            return notFound("UNKNOWN");
        }
    }

    public static Result checkPassword() {
        String[] phonemes = request().body().asFormUrlEncoded().get("phonemes");
        System.out.println("Phonemes: " + Arrays.toString(phonemes));
        session("password", "OK");
        return ok("OK");
    }

    public static Result generateRandom512Bytes() throws IOException {
        byte[] b = new byte[512];
        new Random().nextBytes(b);
        String username = session("connected");
        FileUtils.writeByteArrayToFile(new File("test/random-"+username), b);
        return ok(b);
    }

    public static Result checkDigitalSignature() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        byte[] signature = request().body().asRaw().asBytes();
        System.out.println("Signature length:" + signature.length);
        String username = session("connected");
        byte[] randomBytes = FileUtils.readFileToByteArray(new File("test/random-"+username));
        User loggedInUser = User.findByUsername(username);
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        PublicKey publicKey = digitalSignatureChecker.readPublicKey(loggedInUser.getPublicKey());
        boolean isVerified = digitalSignatureChecker.verifySignature(publicKey, signature, randomBytes);
        if (isVerified) {
            session("signature", "OK");
            return ok("OK");
        }
        else {
            session("signature", "WRONG");
            return unauthorized("WRONG");
        }
    }

}
