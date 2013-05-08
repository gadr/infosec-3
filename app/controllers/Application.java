package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import java.util.Arrays;
import java.util.Collections;

public class Application extends Controller {
  
    public static Result index() {
        String user = session("connected");
        if(user != null) {
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
        System.out.println(username);
        if ("existente".equals(username)) {
            session("connected", username);
            return ok("EXISTS");
        }
        else if ("bloqueado".equals(username)) {
            return forbidden("BLOCKED");
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

    public static Result checkDigitalSignature() {
        String signature = request().body().asFormUrlEncoded().get("signature")[0];
        System.out.println(signature);
        return ok("CHECK");
    }
  
}
