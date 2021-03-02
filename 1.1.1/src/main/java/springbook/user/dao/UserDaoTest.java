package springbook.user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        ApplicationContext ac = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDaoJdbc userDaoJdbc = ac.getBean("userDao", UserDaoJdbc.class);
        User user = new User();
        user.setId("1");
        user.setName("YU");
        user.setPassword("1234");
        userDaoJdbc.add(user);

        User user2 = userDaoJdbc.get("1");

        System.out.println("user2 = " + user2);
        if(!user.getName().equals(user2.getName())){
            System.out.println("테스트 실패 : name");
        }
        else if (!user.getPassword().equals(user2.getPassword())){
            System.out.println("테스트 실패 : password");
        }
        else {
            System.out.println("테스트 성공");
        }
    }
}
