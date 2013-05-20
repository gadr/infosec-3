package controllers;

import models.Group;
import models.User;
import org.apache.commons.io.FileUtils;
import play.api.data.validation.ValidationError;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Logger.*;

public class UserControl extends Controller {

    static Form<User> userForm = Form.form(User.class);

    public static Result newUser() {
        Form<User> filledForm = userForm.bindFromRequest();
        List<Group> groups = Group.find.all();
        User user = getUser();
        filledForm.fill(user);
        return ok(userform.render(user, filledForm, groups));
    }

    public static Result submit() {
        Form<User> filledForm = userForm.bindFromRequest();
        List<Group> groups = Group.find.all();
        User user = getUser();
        boolean hasErrors = filledForm.hasErrors();
        if(hasErrors) {
            for(String key : filledForm.errors().keySet()){
                List<play.data.validation.ValidationError> currentError = filledForm.errors().get(key);
                for(play.data.validation.ValidationError error : currentError){
                    flash(key, error.message());
                }
            }
            return badRequest(userform.render(user, filledForm, groups));
        }

        User filledUser = new User();
        filledUser.name = filledForm.field("name").value();
        filledUser.username = filledForm.field("username").value();
        filledUser.password = filledForm.field("password").value();
        filledUser.passwordConfirmation = filledForm.field("passwordConfirmation").value();
        hasErrors = filledUser.validate();
        if (!filledUser.password.equals(filledUser.passwordConfirmation)) {
            flash("password", "A senha não confere.");
            hasErrors = true;
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart publicKeyFile = body.getFile("publicKeyPath");
        if (publicKeyFile != null) {
            user.publicKeyPath = publicKeyFile.getFilename();
            String contentType = publicKeyFile.getContentType();
            File publickKeyFile = publicKeyFile.getFile();
            try {
                filledUser.publicKey = FileUtils.readFileToByteArray(publickKeyFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            flash("arquivo", "Missing file");
        }

        if(hasErrors) {
            return badRequest(userform.render(user, filledForm, groups));
        } else {
            filledUser.save();
            return redirect("/new");
        }
    }

    public static User getUser() {
        String username = session("connected");
        return User.findByUsername(username);
    }

}
