package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import java.util.List;

public class TestUserService extends UserServiceImpl{
    public TestUserService(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
        super(userDao, userLevelUpgradePolicy);
    }

    @Override
    public List<User> getAll() {
        for(User user : super.getAll()) {
            super.update(user);
        }
        return null;
    }
}
