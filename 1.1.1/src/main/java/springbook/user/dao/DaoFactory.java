package springbook.user.dao;

// 객체의 생성 방법을 결정, 그렇게 만들어진 오브젝트를 리턴하는 역할을 하는 '팩토리'
public class DaoFactory {
    public static UserDao userDao() {
        UserDao userDao = new UserDao(connectionMaker());
        return userDao;
    }

    // 새로운 Dao를 리턴하는 메소드가 추가되어도 문제 없다.
//    public static AccountDao accountDao() {
//        AccountDao accountDao = new AccountDao(connectionMaker());
//        return accountDao;
//    }

    private static ConnectionMaker connectionMaker() {
        return new SimpleConnectionMaker(); // 공통된 부분을 리팩토링으로 분리
    }
}
