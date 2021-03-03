import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserDaoJdbcNoSpringTest {
    UserDaoJdbc userDaoJdbc;
    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setup() {
        userDaoJdbc = new UserDaoJdbc();
        DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost/testdb", "test",
                "test1234", true);
        user1 = new User("1", "YU", "1234", Level.BASIC, 1, 0);
        user2 = new User("2", "YU2", "12345", Level.SILVER, 55, 10);
        user3 = new User("3", "YU3", "123456", Level.GOLD, 100, 40);
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
        assertThat(user1.getName()).isEqualTo(userget1.getName());
        assertThat(user1.getPassword()).isEqualTo(userget1.getPassword());

        User userget2 = userDaoJdbc.get("2");
        assertThat(user2.getName()).isEqualTo(userget2.getName());
        assertThat(user2.getPassword()).isEqualTo(userget2.getPassword());
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
}
