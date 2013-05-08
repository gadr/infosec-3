import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Group;
import play.*;
import play.db.ebean.Transactional;

import java.util.List;

public class Global extends GlobalSettings {

    @Override
    @Transactional
    public void onStart(Application app) {
        Logger.info("Application has started");

        Group grupo1 = new Group("Administrador");
        grupo1.save();

        Group grupo2 = new Group("Usuário");
        grupo2.save();

        List<Group> groups = Group.find.all();

        Logger.info(""+groups.size());
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }

}