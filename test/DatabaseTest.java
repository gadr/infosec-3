import com.avaje.ebean.Ebean;
import models.Group;
import models.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.test.FakeApplication;
import play.test.Helpers;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: Breno
 * Date: 18/05/13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseTest extends BaseModelTest{
    final Logger logger =
            LoggerFactory.getLogger(DatabaseTest.class);

    @Test
    public void saveGroup() {
        // Arrange
        String groupname = "Administrador";
        Group group = new Group();
        group.setName(groupname);

        // Test
        group.save();

        // Assert
        assertThat(group.getGid()).isNotNull();
    }

    @Test
    public void findGroup() {
        // Arrange
        String groupname = "Administrador";
        Group group = new Group();
        group.setName(groupname);
        group.save();

        // Test
        Group result = Group.findByName(groupname);

        // Assert
        assertThat(result).isNotNull();
    }

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
        user.setPassword(password);
        user.save();
        User result = User.findUser(username, password);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getGroup()).isNotNull();
        assertThat(result.getGroup().getName()).isEqualTo(groupname);
    }



}
