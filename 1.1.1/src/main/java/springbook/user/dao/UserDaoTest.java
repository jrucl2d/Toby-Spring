package springbook.user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        ApplicationContext ac = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao userDao = ac.getBean("userDao", UserDao.class);
        UserDao userDao1 = ac.getBean("userDao", UserDao.class);
        System.out.println(userDao1 == userDao);
        UserDao userDao2 = new DaoFactory().userDao();
        UserDao userDao3 = new DaoFactory().userDao();
        System.out.println(userDao2 == userDao3);
//        User user = new User();
//        user.setId("1");
//        user.setName("YU");
//        user.setPassword("1234");
//        userDao.add(user);
//
//        User user2 = userDao.get("1");
//
//        System.out.println("user2 = " + user2);
    }
}
