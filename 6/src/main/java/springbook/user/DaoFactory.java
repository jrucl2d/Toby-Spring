package springbook.user;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.aop.TransactionAdvice;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.mailSender.DummyMailSender;
import springbook.user.service.TestUserService;
import springbook.user.service.UserServiceImpl;

import javax.sql.DataSource;

// 객체의 생성 방법을 결정, 그렇게 만들어진 오브젝트를 리턴하는 역할을 하는 '팩토리'
@Configuration // 어플리케이션 컨텍스트(빈 팩토리)가 사용할 설정정보라는 뜻
public class DaoFactory {

    // aop 관련 빈
    // 어드바이스
    @Bean
    public TransactionAdvice transactionAdvice() {
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(transactionManager());
        return transactionAdvice;
    }
    // 포인트컷
    @Bean
    public AspectJExpressionPointcut transactionPointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 포인트컷 표현식의 이름에 적용되는 패턴은 클래스 이름 패턴이 아니라 타입 패턴
        // -> 여기선 TestUserService가 UserServiceImpl을 상속했다.
        pointcut.setExpression("execution(* *..*ServiceImpl.upgrade*(..))");
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
    // aop 관련 빈 끝
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    // 프록시 자동 생성기에 의해서 포인트컷에 해당하는 이 객체가 실체가 아닌 프록시로 생성됨
    @Bean
    public UserServiceImpl userService() {
        UserServiceImpl userService =  new UserServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSender());
        return userService;
    }
    
    // 다이나믹 프록시에서 프록시 자동 생성기 사용으로 변경
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

    // 트랜잭션 롤백 확인용 TestUserService
    @Bean
    public TestUserService testUserService() {
        TestUserService testUserService = new TestUserService();
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