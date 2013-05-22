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
            if(user.getGroup().isAdmin()) {
                Log.log("5001", user.getUsername());
                return ok(menu.render(user));
            }
            else {
                Log.log("5001", user.getUsername());
                return ok(usermenu.render(user));
            }
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
        Log.log("2001");
        String username = request().body().asFormUrlEncoded().get("username")[0];
        User result = User.findByUsername(username);
        if (result != null) {
            if (result.isBlocked()) {
                Log.log("2004", username);
                Log.log("2002");
                return forbidden("BLOCKED");
            }
            session("connected", result.getUsername());
            Log.log("2003", username);
            Log.log("2002");
            return ok();
        }
        else {
            Log.log("2005", username);
            Log.log("2002");
            return notFound("UNKNOWN");
        }
    }

    public static Result checkPassword() throws NoSuchAlgorithmException {
        User user = User.findByUsername(session("connected"));
        Log.log("3001", user.getUsername());
        String[] phonemesString = request().body().asFormUrlEncoded().get("phonemes[]");
        String[][] phonemesParsed = new String[3][4];
        for (int i = 0; i < 3; i++) {
            phonemesParsed[i] = phonemesString[i].split("-");
        }
        System.out.println("Phonemes: " + Arrays.toString(phonemesString));
        System.out.println("Phonemes Parsed: " + Arrays.toString(phonemesParsed[0]) + " " + Arrays.toString(phonemesParsed[1]) + " " + Arrays.toString(phonemesParsed[2]));
        if (user.isBlocked()) {
            Log.log("3002", user.getUsername());
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
                        Log.log("3003", user.getUsername());
                        Log.log("3002", user.getUsername());
                        return ok("OK");
                    }
                }
            }
        }
        Log.log("3004", user.getUsername());
        user.addPasswordTry();
        user.save();
        if (user.isBlocked()) {
            Log.log("3002", user.getUsername());
            return forbidden("BLOCKED");
        }
        else {
            Log.log("3002", user.getUsername());
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
        Log.log("4001", username);
        User user = User.findByUsername(username);
        if (user.isBlocked()) {
            Log.log("4002", username);
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
            Log.log("4003", username);
            Log.log("4002", username);
            return ok("OK");
        }
        else {
            session("signature", "WRONG");
            user.addSignatureTry();
            user.save();
            if (user.isBlocked()) {
                Log.log("4002", username);
                return forbidden("BLOCKED");
            }
            else {
                Log.log("4002", username);
                return unauthorized("WRONG");
            }
        }
    }

    public static Result log() {
        return ok(log.render(Log.all()));
    }

    public static Result quit() {
        User user = User.findByUsername(session("connected"));
        String changedPassword = session("changedPassword");
        if (user.getAccessNumber() % 3 == 0 && !"OK".equals(changedPassword))
            return ok(quitdisabled.render(user));
        else
            return ok(quitconfirm.render(user));
    }

    public static Result createLog() {
        Http.RequestBody body = request().body();
        String code = body.asFormUrlEncoded().get("code") != null ? body.asFormUrlEncoded().get("code")[0] : "";
        String filename = body.asFormUrlEncoded().get("fileName") != null ? request().body().asFormUrlEncoded().get("fileName")[0] :  "";
        User user = User.findByUsername(session("connected"));
        Log.log(code, user.getUsername(), filename);
        return ok();
    }

}
