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

    public void workWithStatementStrategy(StatementStrategy strategy, Object ...args) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        try{
            c = dataSource.getConnection();
            ps = strategy.makePreparedStatement(c); // DI로 받은 전략을 호출
            // 가변 인자를 통해 바인딩 하는 과정 추가
            for (int i = 0; i < args.length; i++){
                Object arg = args[i];
                if(arg instanceof String){
                    ps.setString(i + 1, (String)arg);
                } else if (arg instanceof Integer){
                    ps.setInt(i + 1, (Integer)arg);
                }
            }
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
    public void executeSql(final String query, Object ...args) throws SQLException {
        workWithStatementStrategy(
                new StatementStrategy() {
                    @Override
                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                        PreparedStatement ps = c.prepareStatement(query);
                        return ps;
                    }
                }, args
        );
    }
}
