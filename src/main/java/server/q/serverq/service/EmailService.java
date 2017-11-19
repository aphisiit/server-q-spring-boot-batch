package server.q.serverq.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    public void sendMessage(String to,String subject,String text);
    public void sendMessageUsingTemplate(String to, String subject, SimpleMailMessage template,String ...templateArgs);
    public void sendMessageWithAttachment(String to,String subject,String text,String pathToAttachment);
}
