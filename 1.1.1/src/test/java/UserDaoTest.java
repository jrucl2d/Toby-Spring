import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDaoTest {
    // 테스트를 수행하는 데 필요한 정보나 오브젝트 -> 픽스쳐
    // 픽스쳐는 테스트에서 반복적으로 사용되므로 @BeforeEach로 설정하는 것이 좋다.
    private UserDao userDao;
    private User user1;
    private User user2;
    private User user3;
    private static ApplicationContext ac;

    @BeforeAll
    static void firstSetup() {
        ac = new AnnotationConfigApplicationContext(DaoTestFactory.class);
    }

    @BeforeEach
    void setup() {
//        System.out.println(this.ac);
//        System.out.println(this);
        userDao = ac.getBean("userDao", UserDao.class);
        user1 = new User("1", "YU", "1234");
        user2 = new User("2", "YU2", "12345");
        user3 = new User("3", "YU3", "123456");
    }
    
    @Test // Junit5부터 public 아니어도 됨
    @DisplayName("User DAO add, get 테스트")
    void addAndGet() throws SQLException {
        userDao.deleteAll();
        assertThat(userDao.getCount()).isEqualTo(0);

        userDao.add(user1);
        userDao.add(user2);
        assertThat(userDao.getCount()).isEqualTo(2);

        User userget1 = userDao.get("1");
        assertThat(user1.getName()).isEqualTo(userget1.getName());
        assertThat(user1.getPassword()).isEqualTo(userget1.getPassword());

        User userget2 = userDao.get("2");
        assertThat(user2.getName()).isEqualTo(userget2.getName());
        assertThat(user2.getPassword()).isEqualTo(userget2.getPassword());
    }

    @Test
    public void getUser_X() throws SQLException {
        userDao.deleteAll();
        assertThrows(EmptyResultDataAccessException.class, () -> userDao.get("none"));
    }

    @Test
    void countTest() throws SQLException {
        userDao.deleteAll();
        assertThat(userDao.getCount()).isEqualTo(0);

        userDao.add(user1);
        assertThat(userDao.getCount()).isEqualTo(1);

        userDao.add(user2);
        assertThat(userDao.getCount()).isEqualTo(2);

        userDao.add(user3);
        assertThat(userDao.getCount()).isEqualTo(3);
    }

    @Test
    void getAll() throws SQLException {
        userDao.deleteAll();
        
        // 데이터가 없는 경우
        List<User> users0 = userDao.getAll();
        assertThat(users0.size()).isEqualTo(0);

        userDao.add(user1);
        List<User> users1 = userDao.getAll();
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        userDao.add(user2);
        List<User> users2 = userDao.getAll();
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        userDao.add(user3);
        List<User> users3 = userDao.getAll();
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    private void checkSameUser(User u1, User u2){
        assertThat(u1.getId()).isEqualTo(u2.getId());
        assertThat(u1.getName()).isEqualTo(u1.getName());
        assertThat(u1.getPassword()).isEqualTo(u1.getPassword());
    }
}
