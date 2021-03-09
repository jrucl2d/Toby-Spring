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
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import springbook.user.aop.TransactionAdvice;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.mailSender.DummyMailSender;
import springbook.user.service.TestUserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.sqlService.*;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement // @Transactional을 사용 가능하게 해줌. <tx:annotation-driven />과 같음
public class DaoFactory {

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
        userDaoJdbc.setSqlService(sqlService());
        return userDaoJdbc;
    }

//    @Bean
//    public BaseSqlService sqlService() {
//        BaseSqlService service = new BaseSqlService();
//        service.setSqlReader(sqlReader());
//        service.setSqlRegistry(sqlRegistry());
//        return service;
//    }
//    @Bean
//    public JsonSqlReader sqlReader() {
//        JsonSqlReader reader = new JsonSqlReader();
//        reader.setSqlmapFile("sql.json");
//        return reader;
//    }
//    @Bean
//    public HashMapSqlRegistry sqlRegistry() {
//        return new HashMapSqlRegistry();
//    }
    public DefaultSqlService sqlService() {
        return new DefaultSqlService();
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