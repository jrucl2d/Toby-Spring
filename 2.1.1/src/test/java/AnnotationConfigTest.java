import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationConfigTest {

    @Test
    void simpleAutoWired() {
        AbstractApplicationContext ac = new AnnotationConfigApplicationContext(BeanA.class, BeanB.class);

        BeanA beanA = ac.getBean(BeanA.class);
        assertThat(beanA.beanB).isNotNull();
    }

    @Test
    void simpleAutoWired2() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(innerConfig.class);
        BeanA beanA = ac.getBean(BeanA.class);
        assertThat(beanA.beanB).isNotNull();
    }
    @Test
    void simpleAutoWired3() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(innerConfig2.class);
        BeanA beanA = ac.getBean(BeanA.class);
        assertThat(beanA.beanB).isNotNull();
    }

    private static class BeanA {
        @Autowired
        BeanB beanB;
    }
    private static class BeanB {
    }
    @Configuration
    private static class innerConfig {
        public innerConfig() {
            super();
        }
        @Bean
        public BeanA beanA() {
            return new BeanA();
        }
        @Bean
        public BeanB beanB() {
            return new BeanB();
        }
    }
    @Configuration
    private static class innerConfig2 {
        public innerConfig2() {
            super();
        }
        @Bean
        public BeanA beanA(){
            BeanA beanA = new BeanA();
            beanA.beanB = beanB();
            return beanA;
        }
//        @Bean
//        public BeanA beanA(BeanB beanB) {
//            BeanA beanA = new BeanA();
//            beanA.beanB = beanB;
//            return beanA;
//        }
        @Bean
        public BeanB beanB() {
            return new BeanB();
        }
    }
}
