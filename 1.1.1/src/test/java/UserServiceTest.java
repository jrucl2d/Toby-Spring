import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.NormalUserLevelUpgradePolicy;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static springbook.user.service.NormalUserLevelUpgradePolicy.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.NormalUserLevelUpgradePolicy.MIN_RECCOMEND_FOR_GOLD;

public class UserServiceTest {
    private static ApplicationContext ac;
    private UserServiceTx userService;
    private MailSender mailSender;
    private PlatformTransactionManager transactionManager;
    private UserDao userDao;
    private List<User> users;

    @BeforeAll
    static void firstJob() {
        ac = new AnnotationConfigApplicationContext(DaoTestFactory.class);
    }
    @BeforeEach
    void beforeEach(){
        this.userService = ac.getBean("userService", UserServiceTx.class);
        this.userDao = ac.getBean("userDao", UserDao.class);
        this.mailSender = ac.getBean("mailSender", MailSender.class);
        this.transactionManager = ac.getBean("transactionManager", PlatformTransactionManager.class);
        this.users = Arrays.asList(
                new User("1", "YU", "1234", "a@a.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
                new User("2", "YU2", "12345", "b@b.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("3", "YU3", "123456", "c@c.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
                new User("4", "YU4", "1234567", "d@d.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
                new User("5", "YU5", "12345678", "e@e.com", Level.GOLD, 100, Integer.MAX_VALUE));
    }
    @Test
    @DisplayName("레벨 업그레이드가 잘 되는지")
    void upgradeLevels() {
        UserDao mockUserDao = mock(UserDao.class);
        MailSender mockMailSender = mock(MailSender.class);
        when(mockUserDao.getAll()).thenReturn(this.users);

        NormalUserLevelUpgradePolicy userLevelUpgradePolicy = new NormalUserLevelUpgradePolicy(mockUserDao, mockMailSender);
        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, userLevelUpgradePolicy);

        userServiceImpl.setUserLevelUpgradePolicy(userLevelUpgradePolicy);
        userService.setUserService(userServiceImpl);
        userService.upgradeLevels();

        // any : 파라미터 내용을 무시하고 호출 횟수만 확인 가능
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1)); // users.get(1)을 파라미터로 update()가 실행되었는지 확인
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor
                = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArgumentCaptor.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArgumentCaptor.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
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

    @Test
    void upgradeAllOrNothing() throws Exception {
        UserServiceImpl testUserServiceImpl = new UserServiceImpl(userDao, new TestUserUpgradePolicy(userDao, "4"));
        UserServiceTx testUserService = new UserServiceTx(testUserServiceImpl, transactionManager);
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try{
            testUserService.upgradeLevels();
            fail("TestUserServiceException Expected");
        } catch (TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1), false);
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
    class TestUserUpgradePolicy extends NormalUserLevelUpgradePolicy {
        private String id;
        public TestUserUpgradePolicy(UserDao userDao, String id) {
            super(userDao, mailSender);
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }
    static class TestUserServiceException extends RuntimeException{

    }
    class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();
        public List<String> getRequests() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            requests.add(simpleMessage.getTo()[0]); // 전송 요청 받은 이메일 주소
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {

        }
    }
}
