package springbook.user.dao.jdbcStrategyPattern;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    // setter DI
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void workWithStatementStrategy(StatementStrategy strategy) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = dataSource.getConnection();
            ps = strategy.makePreparedStatement(c); // DI로 받은 전략을 호출
            ps.executeUpdate();
        } catch (SQLException e){
            throw e;
        } finally {
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
