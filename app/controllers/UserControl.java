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
        User user = User.findByUsername(session("connected"));
        return ok(userform.render(user,userForm));
    }

    public static Result submit() {
        return ok();
    }

}
