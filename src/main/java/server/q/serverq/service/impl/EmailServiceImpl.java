package server.q.serverq.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import server.q.serverq.service.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service("EmailService")
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender emailSender;

    private static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
            LOGGER.info("sendMessage SEND SUCCESS");
        }catch (MailException e){
            LOGGER.error("ERROR sendMessage : {}",e.getMessage());
        }
    }

    @Override
    public void sendMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs) {
        String text = String.format(template.getText(),templateArgs);
        sendMessage(to,subject,text);
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try{
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoive",file);

            emailSender.send(message);

            LOGGER.info("sendMessageWithAttachment SEND SUCCESS");
        }catch (MessagingException e){
            LOGGER.error("ERROR sendMessageWithAttachment : {}",e.getMessage());
        }
    }
}
