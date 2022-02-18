package com.socialnetwork.util;

import com.socialnetwork.config.MailConfig;
import com.socialnetwork.config.email.EmailConfig;
import com.socialnetwork.domain.outport.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private EmailConfig emailConfig;

    public void sendVerificationEmail(String email, String token) throws MessagingException {
        String verificationUrl = emailConfig.getVerificationEmail().getFrontendUrl() + token;
        String content = verificationUrl;

        send(email, emailConfig.getVerificationEmail().getSubject(), content);
    }

    private void send(String to, String subject, String content) throws MessagingException {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", mailConfig.getHost());
        properties.put("mail.smtp.port", mailConfig.getPort());
        properties.put("mail.smtp.user", mailConfig.getUser());
        properties.put("mail.smtp.password", mailConfig.getPassword());
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties);

        MimeMessage message = new MimeMessage(session);
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(content);

        Transport transport = session.getTransport("smtp");
        transport.connect(mailConfig.getHost(), mailConfig.getUser(), mailConfig.getPassword());
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
