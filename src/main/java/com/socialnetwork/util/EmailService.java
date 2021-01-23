package com.socialnetwork.util;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.socialnetwork.domain.EmailVerificationToken;

public interface EmailService {
	void sendVerificationEmail(EmailVerificationToken emailVerificationToken) throws AddressException, MessagingException;
	void send(String to, String subject, String content)  throws AddressException, MessagingException;
}
