package springbook.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDaoJdbc userDaoJdbc = ac.getBean("userDao", UserDaoJdbc.class);
        userDaoJdbc.get("1");
        userDaoJdbc.get("1");
        userDaoJdbc.get("1");

        CountingConnectionMaker ccm = ac.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println(ccm.getCounter());
    }
}
