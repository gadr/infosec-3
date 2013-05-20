import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Group;
import models.User;
import play.*;
import play.db.ebean.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

public class Global extends GlobalSettings {

    @Override
    @Transactional
    public void onStart(Application app) {
        /*
        Group gAdministrador = new Group();
        gAdministrador.setName("Administrador");
        gAdministrador.save();
        Group gUsuario = new Group();
        gUsuario.setName("Usuário");
        gUsuario.save();

        User user = new User();
        user.setUsername("breno");
        user.setName("Breno");
        try {
            user.generatePassword("123");
        } catch (NoSuchAlgorithmException e) {
            ;
        }
        user.setGroup(gAdministrador);
        user.save();
        */
    }

    @Override
    public void onStop(Application app) {
        /*
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
        */
    }

}