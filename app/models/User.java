package models;

import javax.persistence.*;

import org.springframework.beans.factory.annotation.Required;
import play.db.ebean.*;
import play.data.validation.*;
import play.libs.F;
import sun.security.provider.MD5;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import static play.mvc.Controller.flash;

@Entity
@Table(name = "Usuario")
public class User extends Model{

	@Id
    public Long gid;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String password;

    @Transient
    public String passwordConfirmation;

    @OneToOne
    public Group group;

    public String salt = "";

    public Integer accessNumber = 1;

    public Boolean blocked = Boolean.FALSE;

    public Integer tries = 0;

    public Date blockedSince;

    public String publicKeyPath;

    public byte[] publicKey;

    public Long getGid() {
        return gid;
    }

    public Date getBlockedSince() {
        return blockedSince;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public Integer getTries() {
        return tries;
    }

    public void addTry() {
        tries++;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        Integer salt = random.nextInt(999999999);
        return this.salt = salt.toString();
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

    @SuppressWarnings("all")
    public String createPassword(String plainPassword) throws NoSuchAlgorithmException {
        return this.password = generatePassword(plainPassword, this.salt);
    }

    public static String toHex(String arg) {
        try {
            return String.format("%040x", new BigInteger(1, arg.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            return String.format("%040x", new BigInteger(1, arg.getBytes()));
        }
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

    @SuppressWarnings("all")
    public static String generatePassword(String plainPassword, String salt) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String passwordAndSalt = plainPassword + salt;
        messageDigest.update(passwordAndSalt.getBytes());
        byte[] password = messageDigest.digest();
        return toHex(new String(password));
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

    static public boolean isUsernameValid(String username) {
        User user = findByUsername(username);
        if (user == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validate() {
        boolean hasErrors = false;
        if (!isUsernameValid(username)) {
            flash("username", "Login "+username+" já existe.");
            hasErrors = true;
        }
        return hasErrors;
    }
}
