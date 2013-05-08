package models;

import java.util.*;
import javax.persistence.*;

import com.avaje.ebean.Ebean;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

@Entity
@Table(name = "Grupo")
public class Group extends Model {

    @Id
    public Integer gid;

    @Constraints.Required
    public String name;

    public Group(String name) {
        this.name = name;
    }

    public static Finder<Long,Group> find = new Finder<Long,Group>(
            Long.class, Group.class
    );
}