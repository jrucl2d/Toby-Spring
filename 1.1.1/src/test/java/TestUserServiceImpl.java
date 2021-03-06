import springbook.user.dao.UserDao;
import springbook.user.service.UserLevelUpgradePolicy;
import springbook.user.service.UserServiceImpl;

public class TestUserServiceImpl extends UserServiceImpl {
    private String id = "2";

    public TestUserServiceImpl(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
        super(userDao, userLevelUpgradePolicy);
    }
}
