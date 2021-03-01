package springbook.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.dao.jdbcStrategyPattern.JdbcContext;
import springbook.user.dao.jdbcStrategyPattern.StatementStrategy;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
    private DataSource dataSource;
    private JdbcContext jdbcContext;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void setJdbcContext(JdbcContext jdbcContext){
        this.jdbcContext = jdbcContext;
    }
    public UserDao() { }

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 로컬 클래스의 코드에서 외부 메소드 로컬 변수에 접근할 때는 final로 해줘야 한다.
    public void add(final User user) throws SQLException {
//        // 익명 클래스 사용
//        StatementStrategy strategy = new StatementStrategy() {
//            @Override
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                PreparedStatement ps = null;
//                ps = c.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPassword());
//                return ps;
//            }
//        };
//        this.jdbcContext.workWithStatementStrategy(strategy);
        this.jdbcContext.executeSql("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() throws SQLException {
        this.jdbcContext.executeSql("delete from users");
    }

    public User get(String id) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            c = dataSource.getConnection();
            ps = c.prepareStatement("select * from users where id = ?");
            ps.setString(1, id);

            rs = ps.executeQuery();
            User user = null;
            if(rs.next()){
                user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }
            if (user == null) throw new EmptyResultDataAccessException(1);
            return user;
        } catch (SQLException e){
            throw e;
        } finally {
            if(rs != null){
                try{
                    rs.close();
                } catch (SQLException e) {}
            }
            if (ps != null) {
                try{
                    ps.close();
                } catch (SQLException e) {}
            }
            if (c != null) {
                try{
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e){
            throw e;
        } finally {
            if(rs != null){
                try{
                    rs.close();
                } catch (SQLException e) {}
            }
            if (ps != null) {
                try{
                    ps.close();
                } catch (SQLException e) {}
            }
            if (c != null) {
                try{
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

}
