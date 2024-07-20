package com.fares.book_network.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    @Async //this annotation will make the method run in a separate thread so the user wont have to wait for the email to be sent
    public void sendEmail(String to,
                          String username,
                          EmailTemplateName emailTemplate,
                          String ConfirmationURL,
                          String ActivationCode,
                          String subject) throws MessagingException {
        String templateName;
        if(emailTemplate==null){
            templateName = "confirm-email";
        }else{
            templateName = emailTemplate.getTemplateName();
        }
        MimeMessage mimeMessage= mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        Map<String,Object> properties = new HashMap<>();
        properties.put("username",username);
        properties.put("ConfirmationURL",ConfirmationURL);
        properties.put("ActivationCode",ActivationCode);

        Context context = new Context(); //thymeleaf context
        context.setVariables(properties);

        helper.setFrom("faresraboudi308@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String htmlContent = templateEngine.process(templateName,context);
        helper.setText(htmlContent,true);
        mailSender.send(mimeMessage);
    }
}
