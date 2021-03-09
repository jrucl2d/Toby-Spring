package springbook.user.service;

import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;

import java.util.List;

public class TestUserService extends UserServiceImpl{
    private String id = "4";

    @Override
    protected void upgradeLevel(User user) {
        if(user.getId().equals(this.id)) {
            throw new TestUserServiceException();
        }
        super.upgradeLevel(user);
    }

    @Override
    public List<User> getAll() {
        for(User user: super.getAll()){
            super.update(user); // readOnly 트랜잭션이 걸려있을 때 강제 업데이트
        }
        return null;
    }
}
