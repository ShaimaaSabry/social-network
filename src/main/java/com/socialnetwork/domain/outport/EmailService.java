package com.socialnetwork.domain.outport;

import javax.mail.MessagingException;

public interface EmailService {
    void sendVerificationEmail(String email, String token) throws MessagingException;
}
