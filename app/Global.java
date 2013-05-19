import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Group;
import models.User;
import play.*;
import play.db.ebean.Transactional;

import java.util.Iterator;
import java.util.List;

public class Global extends GlobalSettings {

    @Override
    @Transactional
    public void onStart(Application app) {
        Group administrador = new Group();
        administrador.setName("Administrador");
        administrador.save();

        User user = new User();
        user.setUsername("breno");
        user.setName("Breno");
        user.setPassword("123");
        user.setGroup(administrador);
        user.save();
    }

    @Override
    public void onStop(Application app) {
        List<User> lista = User.find.all();
        for(Iterator<User> u = lista.iterator(); u.hasNext(); ) {
            User user = u.next();
            user.delete();
        }
        List<Group> listaG = Group.find.all();
        for(Iterator<Group> g = listaG.iterator(); g.hasNext(); ) {
            Group group = g.next();
            group.delete();
        }
    }

}