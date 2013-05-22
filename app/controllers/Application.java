package controllers;

import checker.DigitalSignatureChecker;
import models.Log;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Application extends Controller {
  
    public static Result index() {
        User user = User.findByUsername(session("connected"));
        boolean passwordChecked = "OK".equals(session("password"));
        boolean signatureChecked = "OK".equals(session("signature"));
        if(user != null && passwordChecked && signatureChecked) {
            if(user.getGroup().isAdmin())
                return ok(menu.render(user));
            else
                return ok(usermenu.render(user));
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
            if (result.isBlocked()) {
                return forbidden("BLOCKED");
            }
            session("connected", result.getUsername());
            return ok();
        }
        else {
            return notFound("UNKNOWN");
        }
    }

    public static Result checkPassword() throws NoSuchAlgorithmException {
        String[] phonemesString = request().body().asFormUrlEncoded().get("phonemes[]");
        String[][] phonemesParsed = new String[3][4];
        for (int i = 0; i < 3; i++) {
            phonemesParsed[i] = phonemesString[i].split("-");
        }
        System.out.println("Phonemes: " + Arrays.toString(phonemesString));
        System.out.println("Phonemes Parsed: " + Arrays.toString(phonemesParsed[0]) + " " + Arrays.toString(phonemesParsed[1]) + " " + Arrays.toString(phonemesParsed[2]));
        User user = User.findByUsername(session("connected"));
        if (user.isBlocked()) {
            return forbidden("BLOCKED");
        }
        String password = user.getPassword();
        String salt = user.getSalt();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    String candidate = phonemesParsed[0][i] + phonemesParsed[1][j] + phonemesParsed[2][k];
                    String hashed = User.generatePassword(candidate, salt);
                    if (hashed.equals(password)) {
                        session("password", "OK");
                        return ok("OK");
                    }
                }
            }
        }
        user.addPasswordTry();
        user.save();
        if (user.isBlocked()) {
            return forbidden("BLOCKED");
        }
        else {
            return unauthorized("WRONG");
        }
    }

    public static Result generateRandom512Bytes() throws IOException {
        byte[] b = new byte[512];
        new Random().nextBytes(b);
        String username = session("connected");
        FileUtils.writeByteArrayToFile(new File("test/random-"+username), b);
        return ok(b);
    }

    public static Result checkDigitalSignature() throws NoSuchAlgorithmException {
        byte[] signature = request().body().asRaw().asBytes();
        System.out.println("Signature length:" + signature.length);
        boolean isVerified = false;
        String username = session("connected");
        User user = User.findByUsername(username);
        if (user.isBlocked()) {
            return forbidden("BLOCKED");
        }
        byte[] randomBytes = new byte[0];
        try {
            randomBytes = FileUtils.readFileToByteArray(new File("test/random-" + username));
            DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
            PublicKey publicKey = digitalSignatureChecker.readPublicKey(user.getPublicKey());
            isVerified = digitalSignatureChecker.verifySignature(publicKey, signature, randomBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        if (isVerified) {
            user.addAccessNumber();
            user.save();
            session("signature", "OK");
            return ok("OK");
        }
        else {
            session("signature", "WRONG");
            user.addSignatureTry();
            user.save();
            if (user.isBlocked()) {
                return forbidden("BLOCKED");
            }
            else {
                return unauthorized("WRONG");
            }
        }
    }

    public static Result log() {
        return ok(log.render(Log.all()));
    }
}
