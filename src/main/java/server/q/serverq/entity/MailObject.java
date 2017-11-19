package server.q.serverq.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MailObject {

    @org.hibernate.validator.constraints.Email
    @NotNull
    @Size(min = 1,message = "Please, set an email address to send the message to it")
    @Getter @Setter
    private String to;

    @Getter @Setter
    private String subject;

    @Getter @Setter
    private String text;

}
