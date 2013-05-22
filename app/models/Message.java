package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "Mensagem")
public class Message extends Model {

    public Message(String message, String code) {
        this.message = message;
        this.code = code;
    }

    @Id
    public Long gid;

    @Constraints.Required
    public String code;

    @Constraints.Required
    public String message;

    public Long getGid() {
        return gid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Finder<Long, Message> find = new Finder<Long, Message>(Long.class, Message.class);

    public static Message findByCode(String code) {
        return find.where().eq("code", code).findUnique();
    }

    public static int count() {
        return find.findRowCount();
    }

    public static List<Message> all() {
        return find.all();
    }

    public static void createAll(List<Message> messages) {
        for (Message m : messages){
            m.save();
        }
        System.out.println("Created " + messages.size() + " messages.");
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }
}