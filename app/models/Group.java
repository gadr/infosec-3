package models;

import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Ebean;
import org.h2.expression.ExpressionList;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
@Table(name = "Grupo")
public class Group extends Model {

    @Id
    public Long gid;

    @Constraints.Required
    public String name;

    public Long getGid() {
        return gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public static Finder<Long, Group> find = new Finder<Long, Group>(Long.class, Group.class);

    public static Group findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    public static int count() {
        return find.findRowCount();
    }

    public static List<Group> all() {
        return find.all();
    }

    public static void create(Group group) {
        group.save();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public boolean isAdmin() {
        return name.equals("Administrador");
    }
}