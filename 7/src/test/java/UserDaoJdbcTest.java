import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.DaoFactory;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDaoJdbcTest {
    // 테스트를 수행하는 데 필요한 정보나 오브젝트 -> 픽스쳐
    // 픽스쳐는 테스트에서 반복적으로 사용되므로 @BeforeEach로 설정하는 것이 좋다.
    private UserDaoJdbc userDaoJdbc;
    private User user1;
    private User user2;
    private User user3;
    private static ApplicationContext ac;
    private DataSource dataSource;

    @BeforeAll
    static void firstSetup() {
        ac = new AnnotationConfigApplicationContext(DaoFactory.class);
    }

    @BeforeEach
    void setup() {
//        System.out.println(this.ac);
//        System.out.println(this);
        userDaoJdbc = ac.getBean("userDao", UserDaoJdbc.class);
        user1 = new User("1", "YU", "1234", "yu1234@a.com", Level.BASIC, 1, 0);
        user2 = new User("2", "YU2", "12345", "yu1234@b.com", Level.SILVER, 55, 10);
        user3 = new User("3", "YU3", "123456", "yu1234@c.com", Level.GOLD, 100, 40);
        dataSource = ac.getBean("dataSource", DataSource.class);
    }
    
    @Test // Junit5부터 public 아니어도 됨
    @DisplayName("User DAO add, get 테스트")
    void addAndGet() throws SQLException {
        userDaoJdbc.deleteAll();
        assertThat(userDaoJdbc.getCount()).isEqualTo(0);

        userDaoJdbc.add(user1);
        userDaoJdbc.add(user2);
        assertThat(userDaoJdbc.getCount()).isEqualTo(2);

        User userget1 = userDaoJdbc.get("1");
        checkSameUser(user1, userget1);

        User userget2 = userDaoJdbc.get("2");
        checkSameUser(user2, userget2);
    }

    @Test
    public void getUser_X() throws SQLException {
        userDaoJdbc.deleteAll();
        assertThrows(EmptyResultDataAccessException.class, () -> userDaoJdbc.get("none"));
    }

    @Test
    void countTest() throws SQLException {
        userDaoJdbc.deleteAll();
        assertThat(userDaoJdbc.getCount()).isEqualTo(0);

        userDaoJdbc.add(user1);
        assertThat(userDaoJdbc.getCount()).isEqualTo(1);

        userDaoJdbc.add(user2);
        assertThat(userDaoJdbc.getCount()).isEqualTo(2);

        userDaoJdbc.add(user3);
        assertThat(userDaoJdbc.getCount()).isEqualTo(3);
    }

    @Test
    void getAll() throws SQLException {
        userDaoJdbc.deleteAll();
        
        // 데이터가 없는 경우
        List<User> users0 = userDaoJdbc.getAll();
        assertThat(users0.size()).isEqualTo(0);

        userDaoJdbc.add(user1);
        List<User> users1 = userDaoJdbc.getAll();
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        userDaoJdbc.add(user2);
        List<User> users2 = userDaoJdbc.getAll();
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        userDaoJdbc.add(user3);
        List<User> users3 = userDaoJdbc.getAll();
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    @Test
    void update() {
        userDaoJdbc.deleteAll();

        userDaoJdbc.add(user1); // 변경될 사용자
        userDaoJdbc.add(user2); // 변경되지 않을 사용자
        
        user1.setName("바보");
        user1.setPassword("p1");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        userDaoJdbc.update(user1);

        User user1update = userDaoJdbc.get(user1.getId());
        checkSameUser(user1, user1update);

        User user2update = userDaoJdbc.get(user2.getId());
        checkSameUser(user2, user2update); // where절이 있어야 작동하도록
    }

    private void checkSameUser(User u1, User u2){
        assertThat(u1.getId()).isEqualTo(u2.getId());
        assertThat(u1.getName()).isEqualTo(u2.getName());
        assertThat(u1.getPassword()).isEqualTo(u2.getPassword());
        assertThat(u1.getLevel()).isEqualTo(u2.getLevel());
        assertThat(u1.getLogin()).isEqualTo(u2.getLogin());
        assertThat(u1.getRecommend()).isEqualTo(u2.getRecommend());
    }

    @Test
    void duplicateKey() {
        userDaoJdbc.deleteAll();

        userDaoJdbc.add(user1);
        assertThrows(DuplicateKeyException.class, () -> userDaoJdbc.add(user1));
    }
}
