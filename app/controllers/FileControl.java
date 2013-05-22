package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class FileControl extends Controller {

    public static Result files() {
        User user = User.findByUsername(session("connected"));
        return ok(files.render(user));
    }

}
