import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnitTest {
    static Set<JUnitTest> testObjs = new HashSet<JUnitTest>();

    @Test
    void test1() {
        assertThat(testObjs.contains(this)).isFalse();
        testObjs.add(this);
    }

    @Test
    void test2() {
        assertThat(testObjs.contains(this)).isFalse();
        testObjs.add(this);
    }

    @Test
    void test3() {
        assertThat(testObjs.contains(this)).isFalse();
        testObjs.add(this);
    }
}
