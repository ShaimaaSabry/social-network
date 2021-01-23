package com.socialnetwork.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.socialnetwork.config.MailConfig;
import com.socialnetwork.config.email.EmailConfig;
import com.socialnetwork.domain.EmailVerificationToken;

@Service
public class EmailServiceImp implements EmailService {
	@Autowired
	private MailConfig mailConfig;

	@Autowired
	private EmailConfig emailConfig;
	
	public void sendVerificationEmail(EmailVerificationToken emailVerificationToken) throws AddressException, MessagingException {
		String verificationUrl = emailConfig.getVerificationEmail().getFrontendUrl() + emailVerificationToken.getToken();
		String content = verificationUrl;
		
		send(emailVerificationToken.getUser().getEmail(), emailConfig.getVerificationEmail().getSubject(), content);
	}
	
	@Override
	public void send(String to, String subject, String content) throws AddressException, MessagingException {
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
