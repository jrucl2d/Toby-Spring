package scope;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonBeanTest {
    @Test
    void singletonScope() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class, SingletonClientBean.class);

        // DL에서 싱글톤임을 확인
        Set<SingletonBean> beans = new HashSet<>();
        beans.add(ac.getBean(SingletonBean.class));
        beans.add(ac.getBean(SingletonBean.class));
        assertThat(beans.size()).isEqualTo(1);

        // DI에서 싱글톤임을 확인
        beans.add(ac.getBean(SingletonClientBean.class).bean1);
        beans.add(ac.getBean(SingletonClientBean.class).bean2);
        assertThat(beans.size()).isEqualTo(1);
    }

    static class SingletonBean {}
    static class SingletonClientBean {
        @Autowired SingletonBean bean1;
        @Autowired SingletonBean bean2;
    }
}
