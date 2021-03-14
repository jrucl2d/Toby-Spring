import ioc.Hello;
import ioc.StringPrinter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTest {

    @Test
    void hello1() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("hello1", Hello.class);

        Hello hello1 = ac.getBean("hello1", Hello.class);
        assertThat(hello1).isNotNull();

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        ac.registerBeanDefinition("hello2", helloDef);

        Hello hello2 = ac.getBean("hello2", Hello.class);
        assertThat(hello2.sayHello()).isEqualTo("Hello Spring");
        assertThat(hello2).isNotEqualTo(hello1);

        assertThat(ac.getBeanFactory().getBeanDefinitionCount()).isEqualTo(2);
    }

    @Test
    void registerBeanWithDependency() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));

        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString()).isEqualTo("Hello Spring");
    }
}
