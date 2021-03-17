package scope;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PrototypeScope {

    @Test
    void prototypeTest() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, PrototypeClientBean.class);

        Set<PrototypeBean> beans = new HashSet<>();

        // DI, DL 모두에서 항상 새로운 bean이 추가됨
        beans.add(ac.getBean(PrototypeBean.class));
        assertThat(beans.size()).isEqualTo(1);
        beans.add(ac.getBean(PrototypeBean.class));
        assertThat(beans.size()).isEqualTo(2);

        beans.add(ac.getBean(PrototypeClientBean.class).bean1);
        assertThat(beans.size()).isEqualTo(3);
        beans.add(ac.getBean(PrototypeClientBean.class).bean2);
        assertThat(beans.size()).isEqualTo(4);
    }

    @Scope("prototype")
    static class PrototypeBean {}
    static class PrototypeClientBean{
        @Autowired PrototypeBean bean1;
        @Autowired PrototypeBean bean2;
    }
}
