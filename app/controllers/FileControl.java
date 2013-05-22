package controllers;

import models.Log;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class FileControl extends Controller {

    public static Result files() {
        User user = User.findByUsername(session("connected"));
        Log.log("5004", user.getUsername());
        Log.log("8001", user.getUsername());
        return ok(files.render(user));
    }

}
