package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public interface UserService {
    void add(User user);
    void upgradeLevels();
}
