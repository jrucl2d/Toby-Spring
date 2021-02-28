package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//public class NUserDao extends UserDao{
//    public Connection getConnection() throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.jdbc.Driver");
//        Connection c = DriverManager.getConnection("jdbc:mysql://localhost/toby", "test", "test1234");
//        return c;
//    }
//
////    public static void main(String[] args) throws SQLException, ClassNotFoundException {
////        NUserDao nUserDao = new NUserDao();
////        Connection c = nUserDao.getConnection();
////    }
//}
