package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;


public class UserServiceImpl implements UserService{

    private final UserDao userDao; // DI
    private UserLevelUpgradePolicy userLevelUpgradePolicy;

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public UserServiceImpl(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }


    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    // 데이터베이스 트랜잭션을 Service 단에서 시작
    public void upgradeLevels() {
        userLevelUpgradePolicy.upgradeLevels();
    }

    @Override
    public User get(String id) {
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }
}
