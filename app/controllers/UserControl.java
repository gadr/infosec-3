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
        try {
            User user = getUser();
            return ok(userform.render(user,userForm));
        } catch (Exception e) {
            return notFound("UNKNOWN");
        }
    }

    public static Result submit() {
        return ok();
    }

    public static User getUser() throws Exception {
        String username = session("connected");
        if(username == null) throw new Exception("Nenhum usuario logado");
        return User.findByUsername(username);
    }

}
