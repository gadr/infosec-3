package controllers;

import checker.DigitalSignatureChecker;
import models.User;
import play.*;
import play.mvc.*;

import views.html.*;

import java.io.IOException;
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
        User user = UserControl.getUser();
        if(user != null) {
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
        User result = User.authenticate(username, "123");
        if (result != null) {
            /*
            if (result.isBlocked()) {
                return forbidden("BLOCKED");
            }
            */
            result.addAccessNumber();
            result.save();
            session("connected", result.getUsername());
            return redirect("/");
        }
        else {
            return notFound("UNKNOWN");
        }
    }

    public static Result checkPassword() {
        String[] phonemes = request().body().asFormUrlEncoded().get("phonemes");
        System.out.println("Phonemes: " + Arrays.toString(phonemes));
        return ok("CHECK");
    }

    public static Result generateRandom512Bytes() {
        byte[] b = new byte[512];
        new Random().nextBytes(b);
        session("randomBytes", new String(b));
        return ok(b);
    }

    public static Result checkDigitalSignature() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, SignatureException, InvalidKeyException {
        String signature = request().body().asFormUrlEncoded().get("signature")[0];
        byte[] randomBytes = session("randomBytes").getBytes();
        String username = session("connected");
        User loggedInUser = User.findByUsername(username);
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        PublicKey publicKey = digitalSignatureChecker.readPublicKey(loggedInUser.getPublicKey());
        boolean isVerified = digitalSignatureChecker.verifySignature(publicKey, randomBytes);
        System.out.println(signature);
        return isVerified ? ok("CHECK") : unauthorized("WRONG_SIGNATURE");
    }

}
