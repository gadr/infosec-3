import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import models.Group;
import models.User;
import org.apache.commons.io.FileUtils;
import play.*;
import play.db.DB;
import play.db.ebean.Transactional;
import utils.KeyPairGenerator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

public class Global extends GlobalSettings {

    @Override
    @Transactional
    public void onStart(Application app) {
        List<User> users = User.find.all();
        for (User u : users)
            u.delete();

        List<Group> groups = Group.find.all();
        for (Group g : groups)
            g.delete();

        Group g = new Group();
        g.setName("Administrador");
        g.save();

        System.out.println("Created admin group");

        User u = new User();
        u.setName("User");
        u.generateSalt();
        try {
            u.createPassword("BEBADO");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        u.setGroup(g);
        u.setUsername("user");
        try {
            u.setPublicKey(FileUtils.readFileToByteArray(new File("test/userpub")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        u.save();

        u = new User();
        u.setName("Administrador Maneiro");
        u.generateSalt();
        try {
            u.createPassword("MAFEVE");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        u.setGroup(g);
        u.setUsername("admin");

        g = new Group();
        g.setName("Usuário");
        g.save();

        System.out.println("Created user admin");

        try {
            KeyPairGenerator.generateKeyPair("admin", "senhalonga");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            u.setPublicKey(FileUtils.readFileToByteArray(new File("test/admin.pub")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        u.save();

        try {
            Process process = Runtime.getRuntime().exec( new String[] { "./create-applet-jar.sh" } );
            process.waitFor();
            process.destroy();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public void onStop(Application app) {
    }

}