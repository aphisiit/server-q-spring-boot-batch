package server.q.serverq.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;

@org.springframework.context.annotation.Configuration
@EnableAutoConfiguration
public class Configuration {

    @Bean
    public SimpleMailMessage templateSimpleMessage(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("This is the test email template for your email:\n%s\n");
        return message;
    }

}
