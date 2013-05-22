package controllers;

import models.Group;
import models.User;
import org.apache.commons.io.FileUtils;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserControl extends Controller {

    static Form<User> userForm = Form.form(User.class);

    public static Result newUser() {
        List<Group> groups = Group.find.all();
        User user = User.findByUsername(session("connected"));
        Form<User> filledForm = userForm.bindFromRequest();
        return ok(userform.render(user, filledForm, groups));
    }

    public static Result getPublic() {
        List<Group> groups = Group.find.all();
        User user = User.findByUsername(session("connected"));
        return ok(user.getPublicKey());
    }

    public static Result editUser() {
        List<Group> groups = Group.find.all();
        User user = User.findByUsername(session("connected"));
        Form<User> filledForm = userForm.fill(user);
        return ok(userform.render(user, filledForm, groups));
    }


    public static Result submit() {
        Form<User> filledForm = userForm.bindFromRequest();
        List<Group> groups = Group.find.all();
        User user = User.findByUsername(session("connected"));
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

        User newUser = new User();
        newUser.name = filledForm.field("name").value();
        newUser.username = filledForm.field("username").value();
        newUser.password = filledForm.field("password").value();
        Group g = Group.findByName(filledForm.field("group.name").value());
        newUser.setGroup(g);
        newUser.passwordConfirmation = filledForm.field("passwordConfirmation").value();
        hasErrors = newUser.validate();
        if (!newUser.password.equals(newUser.passwordConfirmation)) {
            flash("password", "A senha não confere.");
            hasErrors = true;
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart publicKeyFile = body.getFile("publicKeyPath");
        if (publicKeyFile != null) {
            newUser.publicKeyPath = publicKeyFile.getFilename();
            String contentType = publicKeyFile.getContentType();
            File publickKeyFile = publicKeyFile.getFile();
            try {
                newUser.publicKey = FileUtils.readFileToByteArray(publickKeyFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            flash("arquivo", "Missing file");
        }

        if(hasErrors) {
            return badRequest(userform.render(user, filledForm, groups));
        } else {
            newUser.generateSalt();
            try {
                newUser.createPassword(newUser.password);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            newUser.save();
            return redirect("/new");
        }
    }

}
