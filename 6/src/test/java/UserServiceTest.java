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
import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

public class UserServiceTest {
    private static ApplicationContext ac;
    private UserService userService;
    private PlatformTransactionManager transactionManager;
    private UserDao userDao;
    private List<User> users;

    // mail sender 테스트를 위해서 클라이언트가 바로 사용하지 않는 userServiceImpl이 필요하다.
    private UserServiceImpl userServiceImpl;
    private MailSender mailSender;

    @BeforeAll
    static void firstJob() {
        ac = new AnnotationConfigApplicationContext(DaoFactory.class);
    }
    @BeforeEach
    void beforeEach(){
        this.userService = ac.getBean("userService", UserServiceTx.class);
        this.userServiceImpl = ac.getBean("userServiceImpl", UserServiceImpl.class);

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
        // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성
        UserServiceImpl userServiceImplforTest = new UserServiceImpl();

        // UserDao Mock 오브젝트 DI
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImplforTest.setUserDao(mockUserDao);

        // 메일 발송 여부 확인을 위한 Mock 오브젝트 DI
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImplforTest.setMailSender(mockMailSender);

        // 테스트 대상 실행
        userServiceImplforTest.upgradeLevels();

        // DB에 저장된 결과 확인
        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "2", Level.SILVER);
        checkUserAndLevel(updated.get(1), "4", Level.GOLD);

        // Mock 오브젝트를 이용한 결과 확인
        List<String> requests = mockMailSender.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(requests.get(1)).isEqualTo(users.get(3).getEmail());
    }
    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
    }
    @Test
    @DisplayName("Mockito를 사용한 레벨 업그레이드 확인")
    void mockUpgradeLevels() {
        UserServiceImpl userServiceImpl2 = new UserServiceImpl();

        // 다이나믹한 목 오브젝트 생성과 메소드의 리턴 값 설정. 그리고 DI까지 설정
        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl2.setUserDao(mockUserDao);
        
        // 리턴값 없는 메소드를 가진 목 오브젝트는 더 쉬움
        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl2.setMailSender(mockMailSender);
        
        // userServiceImpl2가 실행되는 동안 목 오브젝트가 호출되면 자동으로 기록이 남음
        userServiceImpl2.upgradeLevels();
        
        // times : 메소드 호출 횟수
        // any() : 파라미터는 무시하고 호출 횟수만 확인 가능
        verify(mockUserDao, times(2)).update(any(User.class));
        // users.get(1)을 파라미터로 update()가 호출된 적 있는지 검증
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg
                = ArgumentCaptor.forClass(SimpleMailMessage.class);
        // 파라미터를 정밀하게 검사하기 위해 캡쳐 -> 파라미터보다는 파라미터 내부 정보 확인할 때 유용
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
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
    @DisplayName("레벨 업그레이드 중 에러 발생시 롤백되는지 확인")
    void upgradeAllOrNothing() {
        // 테스트용 UserServiceImpl
        UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setUserService(testUserService); // 수동 DI
        txUserService.setTransactionManager(transactionManager);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try{
            txUserService.upgradeLevels();
            fail("TestUserServiceException Expected");
        } catch (TestUserServiceException e) {

        }
        // 1번은 업데이트되고 3번에서 에러났을 때 1번이 롤백되었는지 확인
        checkLevelUpgraded(users.get(1), false);
    }
    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userupdate = userDao.get(user.getId());
        if(upgraded) {
            assertThat(userupdate.getLevel()).isEqualTo(user.getLevel().getNext());
        } else {
            assertThat(userupdate.getLevel()).isEqualTo(user.getLevel());
        }
    }


    // Mock UserDao의 스태틱 inner class
    static class MockUserDao implements UserDao {
        private List<User> users; // 레벨 업그레이드 후보 User 오브젝트 목록
        private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

        private MockUserDao(List<User> users){
            this.users = users;
        }
        public List<User> getUpdated() {
            return updated;
        }
        @Override
        public List<User> getAll() {
            return this.users;
        }
        @Override
        public void update(User user) {
            updated.add(user); // 업데이트 대상을 리스트에 추가
        }

        // Mock에서 사용하지 않을 메소드는 예외 던지도록 설
        public void add(User user) { throw new UnsupportedOperationException(); }
        public User get(String id) { throw new UnsupportedOperationException(); }
        public void deleteAll() { throw new UnsupportedOperationException(); }
        public int getCount() { throw new UnsupportedOperationException(); }
    }

    // UserService를 테스트하기 위한 스태틱 inner class
    static class TestUserService extends UserServiceImpl {
        private String id;

        private TestUserService(String id){
            this.id = id; // 예외를 발생시킬 User 오브젝트의 id를 지정
        }
        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }
    static class TestUserServiceException extends RuntimeException {}

    // MailSender를 테스트하기 위한 스태틱 inner class
    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<>();

        public List<String> getRequests() {
            return requests;
        }
        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            requests.add(simpleMessage.getTo()[0]);
        }
        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {
        }
    }
}
