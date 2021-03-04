package springbook.user.service;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;

public class UserService {

    private final DataSource dataSource;
    private final UserDao userDao; // DI
    private final UserLevelUpgradePolicy userLevelUpgradePolicy;

    public UserService(DataSource dataSource, UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.dataSource = dataSource;
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    // 데이터베이스 트랜잭션을 Service 단에서 시작
    public void upgradeLevels() throws Exception{
        TransactionSynchronizationManager.initSynchronization(); // 트랜잭션 동기화 관리자 이용해 동기화 작업을 초기화
        Connection connection = DataSourceUtils.getConnection(dataSource);
        connection.setAutoCommit(false);
        try{
            userLevelUpgradePolicy.upgradeLevels();
            connection.commit();
        } catch (Exception e){
            connection.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource); // DB 커넥션 안전하게 닫기
            // 동기화 작업 종료 및 정리
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
