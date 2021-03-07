import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
    }

    @Test
    void upgradeLevel() {
        Level[] levels = Level.values();
        for(Level level : levels) {
            if(level.getNext() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.getNext());
        }
    }

    @Test
    public void upgradeLevel_X() {
        Level[] levels = Level.values();
        for(Level level : levels) {
            if (level.getNext() != null) continue;
            user.setLevel(level);
            assertThrows(IllegalArgumentException.class, () -> user.upgradeLevel());
        }
    }
}
