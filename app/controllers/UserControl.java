package controllers;

import models.User;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class UserControl extends Controller {

    public static Result newUser() {
        Form<models.User> userForm = Form.form(User.class);
        User user = getUser();
        return ok(userform.render(user,userForm));
    }

    public static Result submit() {
        return ok();
    }

    public static User getUser() {
        String username = session("connected");
        return User.findByUsername(username);
    }

}
