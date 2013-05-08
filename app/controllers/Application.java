package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

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

    public static Result auth() {
        String username = request().body().asFormUrlEncoded().get("username")[0];
        if ("teste".equals(username)) {
            session("connected", username);
            return redirect("/");
        }
        else {
            return unauthorized("ERROR");
        }
    }
  
}
