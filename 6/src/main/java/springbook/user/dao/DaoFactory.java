package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.aop.TxProxyFactoryBean;
import springbook.user.mailSender.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;

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
    public UserServiceImpl userServiceImpl() {
        UserServiceImpl userService =  new UserServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSender());
        return userService;
    }

    // 프록시 -> 클라이언트가 바로 사용함
    @Bean
    public UserService userService() throws Exception {
        UserService userService = (UserService) txProxyFactoryBean().getObject();
        return userService;
    }

    @Bean
    public TxProxyFactoryBean txProxyFactoryBean() {
        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
        txProxyFactoryBean.setTarget(userServiceImpl());
        txProxyFactoryBean.setServiceInterface(springbook.user.service.UserService.class);
        txProxyFactoryBean.setTransactionManager(transactionManager());
        txProxyFactoryBean.setPattern("upgradeLevels");
        return txProxyFactoryBean;
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
