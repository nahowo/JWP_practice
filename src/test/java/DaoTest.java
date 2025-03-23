import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import web_application_server.model.User;
import web_server_launcher.core.ConnectionManager;
import web_server_launcher.dao.UserDao;

import java.util.Collection;


public class DaoTest {
    private User expected;
    private UserDao userDao;
    @BeforeEach
    public void setup() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("data.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
        expected = new User("testUser", "testpassword", "test", "test@naver.com");
        userDao = new UserDao();
    }

    @Test
    public void getTest() throws Exception {
        User expected = new User("nahowo", "1234", "nahyun park", "nahowo@naver.com");
        User actual = userDao.findByUserId("nahowo");
        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
        Assertions.assertEquals(expected.getPassword(), actual.getPassword());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    public void createTest() throws Exception {
        userDao.insert(expected);
        User actual = userDao.findByUserId(expected.getUserId());

        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
        Assertions.assertEquals(expected.getPassword(), actual.getPassword());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    public void updateTest() throws Exception {
        User UpdatedUser = new User("nahowo", "new1234", "newNahyun", "alien@naver.com");
        userDao.update(UpdatedUser);
        User expected = new User("nahowo", "new1234", "newNahyun", "alien@naver.com");
        User actual = userDao.findByUserId(UpdatedUser.getUserId());

        Assertions.assertEquals(expected.getUserId(), actual.getUserId());
        Assertions.assertEquals(expected.getPassword(), actual.getPassword());
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    public void findAllTest() throws Exception {
        Collection<User> users = userDao.findAll();
        Assertions.assertEquals(2, users.size());
    }
}
