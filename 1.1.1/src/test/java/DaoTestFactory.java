import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.ConnectionMaker;
import springbook.user.dao.CountingConnectionMaker;
import springbook.user.dao.SimpleConnectionMaker;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.dao.jdbcStrategyPattern.JdbcContext;
import springbook.user.service.*;

import javax.sql.DataSource;

@Configuration
public class DaoTestFactory {

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public UserServiceImpl userServiceImpl() {
        return new UserServiceImpl(userDao(), userLevelUpgradePolicy());
    }

    @Bean
    public UserService userService() {
        return new UserServiceTx(userServiceImpl(), transactionManager());
    }

    @Bean
    public MailSender mailSender() {
        MailSender mailSender = new DummyMailSender();
        return mailSender;
    }

    @Bean
    public UserLevelUpgradePolicy userLevelUpgradePolicy() {
        return new NormalUserLevelUpgradePolicy(userDao(), mailSender());
    }

    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc userDaoJdbc = new UserDaoJdbc(dataSource());
        return userDaoJdbc;
    }

    @Bean
    public JdbcContext jdbcContext() {
        JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource());
        return jdbcContext;
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

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new SimpleConnectionMaker();
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }
}
