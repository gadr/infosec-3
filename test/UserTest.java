import models.Group;
import models.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Breno
 * Date: 18/05/13
 * Time: 16:46 * To change this template use File | Settings | File Templates.
 */
public class UserTest extends BaseModelTest{
    final Logger logger =
            LoggerFactory.getLogger(UserTest.class);

    @Test
    public void createUser() {
        // Arrange
        String groupname = "Administrador";
        String username = "breno";
        String password = "123";
        Group group = new Group();
        group.setName(groupname);
        group.save();

        // Test
        User user = new User();
        user.setGroup(Group.findByName(groupname));
        user.setUsername(username);
        try {
            user.createPassword(password);
        } catch (NoSuchAlgorithmException e) {
        }
        user.save();
        User result = User.findByUsername(username);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getGroup()).isNotNull();
        assertThat(result.getGroup().getName()).isEqualTo(groupname);
    }

    @Test
    public void generatePassword() {
        // Arrange
        String password = "123";
        User user = new User();

        try {
            // Test
            user.generateSalt();
            String generatedPassword = user.createPassword(password);
            String secGeneratedPassword = user.createPassword(password);
            user.generateSalt();
            String diffGeneratedPassword = user.createPassword(password);

            // Assert
            //System.out.println(generatedPassword);
            //System.out.println(secGeneratedPassword);
            //System.out.println(diffGeneratedPassword);
            assertThat(generatedPassword).isEqualTo(secGeneratedPassword);
            assertThat(generatedPassword).isNotEqualTo(diffGeneratedPassword);
        } catch (NoSuchAlgorithmException e) {
            // Assert
            assertThat(false).isTrue();
        }
    }

    @Test
    public void usernameExists() {
        // Arrange
        String username = "naoexisto";

        // Test
        boolean result = User.isUsernameValid(username);

        // Assert
        assertThat(result).isTrue();
    }



}
