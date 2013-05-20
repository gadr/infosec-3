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
    }

    @Override
    public void onStop(Application app) {
    }

}