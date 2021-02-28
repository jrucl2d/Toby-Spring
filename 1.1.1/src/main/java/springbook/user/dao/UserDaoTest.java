package springbook.user.dao;

public class UserDaoTest {
    public static void main(String[] args) {
        ConnectionMaker connectionMaker = new SimpleConnectionMaker(); // 사용할 ConnectionMaker 구현체를 결정

        UserDao userDao = new UserDao(connectionMaker);
    }
}
