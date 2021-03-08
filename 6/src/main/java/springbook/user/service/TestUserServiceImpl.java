package springbook.user.service;

import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;

public class TestUserServiceImpl extends UserServiceImpl{
    private String id = "4";

    @Override
    protected void upgradeLevel(User user) {
        if(user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }
}
