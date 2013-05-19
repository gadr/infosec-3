package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

import java.util.Date;

@Entity
@Table(name = "Usario")
public class User extends Model{

	@Id
    Long gid;

    @Constraints.Required
    String username;

    String name;

    @Constraints.Required
    String password;

    @OneToOne
    Group group;

    Integer accessNumber = 1;

    Boolean blocked = Boolean.FALSE;

    Date blockedSince;

    private byte[] publicKey;

    public Long getGid() {
        return gid;
    }

    public Date getBlockedSince() {
        return blockedSince;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
        this.blockedSince = new Date();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Integer getAccessNumber() {
        return accessNumber;
    }

    public void addAccessNumber() {
        this.accessNumber++;
    }

    public static Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

    public static User find(String gid) {
        Long newGid = Long.parseLong(gid);
        return find.where().idEq(newGid).findUnique();
    }

    public static User findByUsername(String username) {
        return find.where().eq("username", username).findUnique();
    }

    public static User authenticate(String username, String password) {
        return find.where().eq("username", username).eq("password", password).findUnique();
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }
}
