package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "InfosecLog")
public class Log extends Model {

    public Log(Date date, Message message, String username, String filename) {
        this.username = username;
        this.filename = filename;
        this.date = date;
        this.message = message;
    }

    @Id
    public Long gid;

    public String username;

    public String filename;

    @Constraints.Required
    public Date date;

    @Constraints.Required
    @ManyToOne
    public Message message;

    public static void log(String code, String username, String filename) {
        Date now = new Date();
        Message message = Message.findByCode(code);
        Log log = new Log(now, message, username, filename);
        log.save();
        System.out.println("Log: " + log);
    }

    public static void log(String code, String username) {
        log(code, username, "");
    }

    public static void log(String code) {
        log(code, "", "");
    }

    public Long getGid() {
        return gid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static Finder<Long, Log> find = new Finder<Long, Log>(Long.class, Log.class);

    public static int count() {
        return find.findRowCount();
    }

    public static List<Log> all() {
        return find.all();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public String getCode() {
        return message.getCode();
    }

    public String getMessageString() {
        return message != null ? message.getMessage().replaceAll("<login_name>", username).replaceAll("<arq_name>", filename) : "";
    }

    @Override
    public String toString() {
        return "Log{" +
                "date=" + date +
                ", message=" + getMessageString() +
                ", filename='" + filename + '\'' +
                '}';
    }
}