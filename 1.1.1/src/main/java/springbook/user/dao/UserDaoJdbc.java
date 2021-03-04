package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            return user;
        }
    };


    public UserDaoJdbc() {
    }

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 로컬 클래스의 코드에서 외부 메소드 로컬 변수에 접근할 때는 final로 해줘야 한다.
    public void add(final User user) {
            this.jdbcTemplate.update("insert into users(id, name, password, email, level, login, recommend) values(?, ?, ?, ?, ?, ?, ?)",
                    user.getId(), user.getName(), user.getPassword(), user.getEmail(), user.getLevel().getValue(), user.getLogin(), user.getRecommend());

    }

    public void deleteAll() {
//        this.jdbcContext.executeSql("delete from users");
        this.jdbcTemplate.update("delete from users");
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", this.userRowMapper, id);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userRowMapper);
    }

    public int getCount() {
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

    public void update(User user) {
        System.out.println("user = " + user);
        this.jdbcTemplate.update("update users set name = ?, password = ?, email = ?, level = ?, login = ?, " +
                "recommend = ? where id = ?", user.getName(), user.getPassword(), user.getEmail() ,user.getLevel().getValue(), user.getLogin(),
                user.getRecommend(), user.getId());
    }
}
