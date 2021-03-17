import ioc.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloTest {

    String basePath = "classpath:\\config\\";

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

    @Test
    void parentChildTest() {
        ApplicationContext parent = new GenericXmlApplicationContext(basePath + "parentContext.xml");
        GenericApplicationContext child = new GenericApplicationContext(parent);

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(child);
        reader.loadBeanDefinitions(basePath + "childContext.xml");
        child.refresh();

        Printer printer = child.getBean("printer", Printer.class);
        assertThat(printer).isNotNull();

        Hello hello = child.getBean("hello", Hello.class);
        assertThat(hello).isNotNull();
        hello.print();
        assertThat(printer.toString()).isEqualTo("Hello Child");
    }
    @Test
    void simpleBeanScanning() {
        ApplicationContext ac = new AnnotationConfigApplicationContext("ioc");
//        AnnotatedHello hello = ac.getBean("annotatedHello", AnnotatedHello.class); // 자동으로 클래스 이름에서 소문자로 바꾼 빈을 등록해줌
        AnnotatedHello hello = ac.getBean("myHello", AnnotatedHello.class);
        assertThat(hello).isNotNull();
    }
    @Test
    void simpleBeanScanning2() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AnnotatedHelloConfig.class);
        AnnotatedHello hello = ac.getBean("annotatedHello", AnnotatedHello.class);
        AnnotatedHelloConfig config = ac.getBean("annotatedHelloConfig", AnnotatedHelloConfig.class);
        assertThat(hello).isNotNull();
        assertThat(config).isNotNull();
        assertThat(config.annotatedHello()).isSameAs(hello); // new 해서 실행해도 같은 오브젝트(싱글톤)
    }
}
