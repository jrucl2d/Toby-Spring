import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static springbook.user.service.NormalUserLevelUpgradePolicy.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.NormalUserLevelUpgradePolicy.MIN_RECCOMEND_FOR_GOLD;

public class UserServiceTest {
    private static ApplicationContext ac;
    private UserService userService;
    private UserDao userDao;
    private List<User> users;

    @BeforeAll
    static void firstJob() {
        ac = new AnnotationConfigApplicationContext(DaoTestFactory.class);
    }
    @BeforeEach
    void beforeEach(){
        this.userService = ac.getBean("userService", UserService.class);
        this.userDao = ac.getBean("userDao", UserDao.class);
        this.users = Arrays.asList(
                new User("1", "YU", "1234", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("2", "YU2", "12345", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("3", "YU3", "123456", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("4", "YU4", "1234567", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("5", "YU5", "12345678", Level.GOLD, 100, Integer.MAX_VALUE));
    }
    @Test
    @DisplayName("레벨 업그레이드가 잘 되는지")
    void upgradeLevels() {
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        userService.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.SILVER);
        checkLevel(users.get(3), Level.GOLD);
        checkLevel(users.get(4), Level.GOLD);
    }

    @Test
    @DisplayName("유저 추가시 기본 BASIC인지, 레벨 지정 가능한지")
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4); // Gold 레벨 -> 레벨이 지정된 유저
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null); // 레벨 없음 -> 기본 BASIC 처리되어야 함

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    private void checkLevel(User user, Level expected){
        User userupdate = userDao.get(user.getId());
        assertThat(userupdate.getLevel()).isEqualTo(expected);
    }
    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userupdate = userDao.get(user.getId());
        if(upgraded) {
            assertThat(userupdate.getLevel()).isEqualTo(user.getLevel().getNext());
        } else {
            assertThat(userupdate.getLevel()).isEqualTo(user.getLevel());
        }
    }
}
