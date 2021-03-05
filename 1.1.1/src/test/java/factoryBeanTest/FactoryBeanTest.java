package factoryBeanTest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FactoryBeanTest {
    static ApplicationContext ac;

    @BeforeAll
    static void beforeAll() {
        ac = new AnnotationConfigApplicationContext(tmpConfig.class);
    }

    @Test
    void getMessageFromFactoryBean() {
        Object message = ac.getBean("message");
        Object factory = ac.getBean("&message"); // factory 빈 자체를 가져올 수 있다.
        assertThat(message.getClass()).isEqualTo(Message.class);
        assertThat(((Message) message).getText()).isEqualTo("Factory Bean");
        assertThat(factory.getClass()).isEqualTo(MessageFactoryBean.class);
    }

}
@Configuration
class tmpConfig {
    @Bean
    public MessageFactoryBean message() {
        MessageFactoryBean factoryBean = new MessageFactoryBean();
        factoryBean.setText("Factory Bean");
        return factoryBean;
    }
}
class Message {
    String text;

    public String getText() {
        return text;
    }

    // 생성자가 private이므로 외부에서 생성자를 통해 오브젝트 생성 불가 -> 스프링 빈 등록 불가
    private Message(String text) {
        this.text = text;
    }

    public static Message newMessage(String text) {
        return new Message(text);
    }
}
class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);
    }

    @Override
    public Class<? extends Message> getObjectType() {
        return Message.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}