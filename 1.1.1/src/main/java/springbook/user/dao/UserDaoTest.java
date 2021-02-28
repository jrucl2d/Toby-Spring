package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // UserDaoTest의 관심사 -Dao를 테스트하는 것- 에서 벗어나는 Connection 관련 초기화
        // 및 관계 설정 부분을 팩토리를 사용해 분리
        UserDao userDao = DaoFactory.userDao();
        User user = new User();
        user.setId("1");
        user.setName("YU");
        user.setPassword("1234");
        userDao.add(user);

        User user2 = userDao.get("1");

        System.out.println("user2 = " + user2);
    }
}
