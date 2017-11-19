package server.q.serverq.controller;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import server.q.serverq.entity.MailObject;
import server.q.serverq.service.EmailService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Value("${attachment.invoice")
    private String attachmentPath;

    @Autowired
    private SimpleMailMessage template;

    private static final Map<String, Map<String, String>> labels;

    static {
        labels = new HashMap<>();

        //Simple email
        Map<String, String> props = new HashMap<>();
        props.put("headerText", "Send Simple EmailObject");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "");
        labels.put("send", props);

        //EmailObject with template
        props = new HashMap<>();
        props.put("headerText", "Send EmailObject Using Template");
        props.put("messageLabel", "Template Parameter");
        props.put("additionalInfo",
                "The parameter value will be added to the following message template:<br>" +
                        "<b>This is the test email template for your email:<br>'Template Parameter'</b>"
        );
        labels.put("sendTemplate", props);

        //EmailObject with attachment
        props = new HashMap<>();
        props.put("headerText", "Send EmailObject With Attachment");
        props.put("messageLabel", "Message");
        props.put("additionalInfo", "To make sure that you send an attachment with this email, change the value for the 'attachment.invoice' in the application.properties file to the path to the attachment.");
        labels.put("sendAttachment", props);
    }

    @RequestMapping(value = {"/send", "/sendTemplate", "/sendAttachment"}, method = RequestMethod.GET)
    public String createMail(Model model,
                             HttpServletRequest request) {
        String action = request.getRequestURL().substring(
                request.getRequestURL().lastIndexOf("/") + 1
        );
        Map<String, String> props = labels.get(action);
        Set<String> keys = props.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            model.addAttribute(key, props.get(key));
        }

        model.addAttribute("mailObject", new MailObject());
        return "email/send";
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<String> createMail(Model model,
                             @ModelAttribute("mailObject") @Valid MailObject mailObject,
                             Errors errors) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type","application/json");
        Map <String,String> map = new HashMap<>();

        if (errors.hasErrors()) {
            map.put("status","error");
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(map),headers, HttpStatus.OK);
        }
        emailService.sendMessage(mailObject.getTo(),
                mailObject.getSubject(), mailObject.getText());

        map.put("status","ok");
        return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(map),headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/sendTemplate", method = RequestMethod.POST)
    public ResponseEntity<String> createMailWithTemplate(Model model,
                                         @ModelAttribute("mailObject") @Valid MailObject mailObject,
                                         Errors errors) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type","application/json");
        Map <String,String> map = new HashMap<>();

        if (errors.hasErrors()) {
            map.put("status","error");
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(map),headers, HttpStatus.OK);
        }
        emailService.sendMessageUsingTemplate(mailObject.getTo(),
                mailObject.getSubject(),
                template,
                mailObject.getText());

        map.put("status","ok");
        return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(map),headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/sendAttachment", method = RequestMethod.POST)
    public ResponseEntity<String> createMailWithAttachment(Model model,
                                                          @ModelAttribute("mailObject") @Valid MailObject mailObject,
                                                          Errors errors) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type","application/json");
        Map <String,String> map = new HashMap<>();

        if (errors.hasErrors()) {
            map.put("status","error");
            return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(map),headers, HttpStatus.OK);
        }
        emailService.sendMessageWithAttachment(
                mailObject.getTo(),
                mailObject.getSubject(),
                mailObject.getText(),
                attachmentPath
        );

        map.put("status","ok");
        return new ResponseEntity<String>(new JSONSerializer().prettyPrint(true).serialize(map),headers, HttpStatus.OK);
    }

}
