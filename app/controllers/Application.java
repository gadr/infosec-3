package controllers;

import models.User;
import play.*;
import play.mvc.*;

import views.html.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Application extends Controller {
  
    public static Result index() {
        String userGid = session("connected");
        if(userGid != null) {
            User user = User.find(userGid);
            return ok(index.render(user));
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
        User result = User.findUser(username, "123");
        if (result != null) {
            /*
            if (result.isBlocked()) {
                return forbidden("BLOCKED");
            }
            */
            result.addAccessNumber();
            result.save();
            session("connected", result.getGid().toString());
            return ok("EXISTS");
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
        return ok(b);
    }

    public static Result checkDigitalSignature() {
        String signature = request().body().asFormUrlEncoded().get("signature")[0];
        System.out.println(signature);
        return ok("CHECK");
    }
  
}
