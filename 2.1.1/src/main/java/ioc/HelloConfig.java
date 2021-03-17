package ioc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloConfig {

    @Value("${db.username}")
    private String name;

    @Bean
    public Hello hello() {
        Hello hello = new Hello();
        hello.setName(this.name);
        hello.setPrinter(printer());
        return hello;
    }
    @Bean
    public Hello hello2() {
        Hello hello = new Hello();
        hello.setName("Spring2");
        hello.setPrinter(printer());
        return hello;
    }
    @Bean
    public Printer printer() {
        return new StringPrinter();
    }
}
