import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SpringTestContextTest {
    static ApplicationContext applicationContext;
    static Set<SpringTestContextTest> objs = new HashSet<>();
    static ApplicationContext contextObj = null;

    @BeforeAll
    static void setup() {
        applicationContext = new AnnotationConfigApplicationContext(EmptyBean.class);
    }

    @Test
    void test1() {
        assertThat(objs.contains(this)).isFalse();
        objs.add(this);
        assertThat(contextObj == null || contextObj == this.applicationContext).isTrue();
        contextObj = this.applicationContext;
    }

    @Test
    void test2() {
        System.out.println("this = " + this);
        System.out.println("applicationContext = " + applicationContext);
        System.out.println("contextObj = " + contextObj);
        assertThat(objs.contains(this)).isFalse();
        objs.add(this);
        assertThat(contextObj == null || contextObj == this.applicationContext).isTrue();
        contextObj = this.applicationContext;
    }

    @Test
    void test3() {
        assertThat(objs.contains(this)).isFalse();
        objs.add(this);
        assertThat(contextObj == null || contextObj == this.applicationContext).isTrue();
        contextObj = this.applicationContext;
    }
}

@Configuration
class EmptyBean {

}