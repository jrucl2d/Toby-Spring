package springbook.user;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.aop.NameMatchClassMethodPointcut;
import springbook.user.aop.TransactionAdvice;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.mailSender.DummyMailSender;
import springbook.user.service.TestUserServiceImpl;
import springbook.user.service.UserServiceImpl;

import javax.sql.DataSource;

// 객체의 생성 방법을 결정, 그렇게 만들어진 오브젝트를 리턴하는 역할을 하는 '팩토리'
@Configuration // 어플리케이션 컨텍스트(빈 팩토리)가 사용할 설정정보라는 뜻
public class DaoFactory {

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    // 실제 객체 -> 클라이언트가 바로 사용하지 않는다.
    @Bean
    public UserServiceImpl userService() {
        UserServiceImpl userService =  new UserServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSender());
        return userService;
    }

    // 어드바이스
    @Bean
    public TransactionAdvice transactionAdvice() {
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(transactionManager());
        return transactionAdvice;
    }
    // 포인트컷
    @Bean
    public NameMatchClassMethodPointcut transactionPointcut() {
        NameMatchClassMethodPointcut pointcut = new NameMatchClassMethodPointcut();
        pointcut.setMappedClassName("*ServiceImpl");
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }
    // 어드바이저
    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setAdvice(transactionAdvice());
        advisor.setPointcut(transactionPointcut());
        return advisor;
    }
    // 빈 후처리기
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

    // 트랜잭션 롤백 확인용 TestUserService
    @Bean
    public TestUserServiceImpl testUserService() {
        TestUserServiceImpl testUserService = new TestUserServiceImpl();
        testUserService.setUserDao(userDao());
        testUserService.setMailSender(mailSender());
        return testUserService;
    }

    @Bean
    public MailSender mailSender() {
        MailSender mailSender = new DummyMailSender();
        return mailSender;
    }

    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(dataSource());
        return userDaoJdbc;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/testdb");
        dataSource.setUsername("test");
        dataSource.setPassword("test1234");
        return dataSource;
    }
}