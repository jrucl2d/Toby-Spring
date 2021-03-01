package springbook.user.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.dao.jdbcStrategyPattern.JdbcContext;
import springbook.user.dao.jdbcStrategyPattern.StatementStrategy;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    };


    public UserDao() { }

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 로컬 클래스의 코드에서 외부 메소드 로컬 변수에 접근할 때는 final로 해줘야 한다.
    public void add(final User user) throws SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(), user.getName(), user.getPassword());
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

//        this.jdbcContext.executeSql("insert into users(id, name, password) values(?, ?, ?)",
//                user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() throws SQLException {
//        this.jdbcContext.executeSql("delete from users");
        this.jdbcTemplate.update("delete from users");
    }

    public User get(String id) throws SQLException {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", this.userRowMapper, id);
    }
    public List<User> getAll() throws SQLException {
        return this.jdbcTemplate.query("select * from users order by id", this.userRowMapper);
    }
    public int getCount() throws SQLException {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
//        Connection c = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        try{
//            c = dataSource.getConnection();
//            ps = c.prepareStatement("select count(*) from users");
//            rs = ps.executeQuery();
//            rs.next();
//            return rs.getInt(1);
//        } catch (SQLException e){
//            throw e;
//        } finally {
//            if(rs != null){
//                try{
//                    rs.close();
//                } catch (SQLException e) {}
//            }
//            if (ps != null) {
//                try{
//                    ps.close();
//                } catch (SQLException e) {}
//            }
//            if (c != null) {
//                try{
//                    c.close();
//                } catch (SQLException e) {}
//            }
//        }
    }
}
