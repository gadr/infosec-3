import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Group;
import models.User;
import play.*;
import play.db.ebean.Transactional;

import java.util.List;

public class Global extends GlobalSettings {

    @Override
    @Transactional
    public void onStart(Application app) {
        Group administrador = new Group();
        administrador.setName("Administrador");
        administrador.save();

        User user = new User();
        user.setUsername("teste");
        user.setPassword("123");
        user.setGroup(administrador);
        user.save();
    }

    @Override
    public void onStop(Application app) {
        Group grupo = Group.findByName("Administrador");
        User result = User.findUser("teste", "123");
        result.delete();
        grupo.delete();
    }

}